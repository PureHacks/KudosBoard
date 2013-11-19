package services

import javax.naming.{NamingEnumeration, Context, AuthenticationException}
import javax.naming.directory.{SearchResult, InitialDirContext, SearchControls}
import models.response.LDAPUserInfo
import models.response.LDAPUserInfoSearch
import models.domain.User

import play.api.Play.current
import scala.util.Try


object LDAPContext {

  implicit class RichIterator[A](val it: Iterator[A]) extends AnyVal {
    def headOption: Option[A] = if (it.hasNext) Some(it.next()) else None
  }
  
  def searchContext: LDAPContext = {
    val ctxt = for {
      principal <- current.configuration.getString("ldap.principal")
      credentials <- current.configuration.getString("ldap.credentials")
    } yield {
      new LDAPContext(principal, credentials)
    }
    ctxt.getOrElse{
      throw new Exception("LDAP configuration is missing search account credentials")
    }
  }

  def authenticate(username: String, password: String): Option[LDAPUserInfo] = {
    Try {
      val ctxt = new LDAPContext(s"$username@nurun.com", password)
      ctxt.searchUsername(username)
    }.toOption.flatten
  }

}

class LDAPContext(username: String, password: String) extends InitialDirContext {
  import LDAPContext._

  implicit def NamingEnumerationIterator(searchResults: NamingEnumeration[SearchResult]) = new Iterator[SearchResult] {
    def hasNext: Boolean = searchResults.hasMore
    def next(): SearchResult = searchResults.next()
  }

  private val mailStr = "ou=users,ou=Toronto,dc=nurun,dc=com"

  private val ctx = {
    val createEnv = for {
      url <- current.configuration.getString("ldap.url")
      authentication <- current.configuration.getString("ldap.authentication")
    } yield {
      val env = new java.util.Hashtable[String, String]()
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
      env.put(Context.PROVIDER_URL, url)
      env.put(Context.SECURITY_AUTHENTICATION, authentication)
      env.put(Context.SECURITY_PRINCIPAL, username)
      env.put(Context.SECURITY_CREDENTIALS, password)
      env.put(Context.REFERRAL, "follow") //TODO what does this do?
      env.put(Context.SECURITY_PROTOCOL, "tls")
      env
    }
    createEnv match {
      case Some(env) => new InitialDirContext(env)
      case None => throw new Exception("LDAP configuration missing or incomplete")
    }
  }


  def searchAll(): Iterator[LDAPUserInfoSearch] = typeaheadSearch("*@nurun.com")

  def typeaheadSearch(emailPattern: String): Iterator[LDAPUserInfoSearch] = {
    val constraints = new SearchControls()
    constraints.setSearchScope(SearchControls.SUBTREE_SCOPE)

    val filterStr = s"mail=$emailPattern"

    Option(ctx.search(mailStr, filterStr, constraints))
      .map(_.map(getTypeaheadInfoSearch))
      .getOrElse(Iterator())
  }

  private def getTypeaheadInfoSearch(searchResult: SearchResult): LDAPUserInfoSearch = {
    val dn = searchResult.getName
    val cn = dn.replaceFirst("CN=", "")
    val names = cn.split(" ")

    val attrs = searchResult.getAttributes
    def getAttr(attr: String) = Option(attrs.get(attr.toLowerCase)).map(_.get().asInstanceOf[String])
    def readAttr(attr: String): Option[String] = getAttr(attr).map(_.replaceFirst(s"$attr: ", ""))
    val firstName = readAttr("givenname").getOrElse("")
    val lastName = readAttr("sn").getOrElse("")
    val email = readAttr("mail").getOrElse("")
    val userName = email.takeWhile(_ != '@')

    LDAPUserInfoSearch(userName, firstName, lastName, email)
  }


  
  def findAll(): Iterator[LDAPUserInfo] = searchEmail("*@nurun.com")

  def searchEmail(emailPattern: String): Iterator[LDAPUserInfo] = {
    val constraints = new SearchControls()
    constraints.setSearchScope(SearchControls.SUBTREE_SCOPE)

    val filterStr = s"mail=$emailPattern"

    Option(ctx.search(mailStr, filterStr, constraints))
      .map(_.map(getUserInfo))
      .getOrElse(Iterator())
  }

  def searchUsername(username: String): Option[LDAPUserInfo] = {
    val constraints = new SearchControls()
    constraints.setSearchScope(SearchControls.SUBTREE_SCOPE)

    val filterStr = s"sAMAccountName=$username"

    val searchResults: Iterator[SearchResult] = ctx.search(mailStr, filterStr, constraints)
    Option(searchResults).flatMap(_.headOption).map(getUserInfo)
  }
  
  private def getUserInfo(searchResult: SearchResult): LDAPUserInfo = {

    val dn = searchResult.getName
    val cn = dn.replaceFirst("CN=", "")
    val names = cn.split(" ")
    //val firstName = names.headOption.getOrElse("")
    //val lastName = sn.replaceFirst("sn: ","")//names.lastOption.getOrElse("")

    val attrs = searchResult.getAttributes
    def getAttr(attr: String) = Option(attrs.get(attr.toLowerCase)).map(_.get().asInstanceOf[String])
    def readAttr(attr: String): Option[String] = getAttr(attr).map(_.replaceFirst(s"$attr: ", ""))
    val firstName = readAttr("givenname").getOrElse("")
    val lastName = readAttr("sn").getOrElse("")
    val email = readAttr("mail").getOrElse("")
    val userName = email.takeWhile(_ != '@')

    LDAPUserInfo(userName, firstName, lastName, email)
  }

  def authenticate(username: String, password: String): Option[LDAPUserInfo] = {
    try {
      val searchBase = "ou=Toronto,dc=nurun,dc=com"
      val searchString = "(&(objectCategory=user)(sAMAccountName=" + username + "))"
      val constraints = new SearchControls()
      constraints.setSearchScope(SearchControls.SUBTREE_SCOPE)
      constraints.setCountLimit(1)
      constraints.setTimeLimit(5000)
      val searchResult = ctx.search(searchBase, searchString, constraints)
      val getDnAndInfo = Option(searchResult).map { searchResults =>
        searchResults.map( sr =>  (sr.getAttributes.get("distinguishedName").get().asInstanceOf[String], getUserInfo(sr)))
      }.flatMap(_.headOption)
      getDnAndInfo.map { case (dn, userInfo) =>
        new LDAPContext(dn, password)
        userInfo
      }
    } catch {
      case ae: AuthenticationException => None
    }
  }

  override def finalize() {
    ctx.close()
  }

}
