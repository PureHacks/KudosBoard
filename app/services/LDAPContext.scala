package services

import javax.naming.{NamingEnumeration, Context, AuthenticationException}
import javax.naming.directory.{SearchResult, InitialDirContext, SearchControls}
import services.responses.LDAPUserInfo
import models.domain.User

import play.api.Play.current



object LDAPContext {

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

}

class LDAPContext(username: String, password: String) extends InitialDirContext {

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
      env
    }
    createEnv match {
      case Some(env) => new InitialDirContext(env)
      case None => throw new Exception("LDAP configuration missing or incomplete")
    }
  }

  def findAll(): Seq[LDAPUserInfo] = searchEmail("*@nurun.com")

  def searchEmail(emailPattern: String): Seq[LDAPUserInfo] = {
    val constraints = new SearchControls()
    constraints.setSearchScope(SearchControls.SUBTREE_SCOPE)

    val filterStr = s"mail=$emailPattern"

    Option(ctx.search(mailStr, filterStr, constraints)).map(
      getSearchResults(getUserInfo)
    ).getOrElse(Seq())
  }

  def searchUsername(username: String): Option[LDAPUserInfo] = {
    val constraints = new SearchControls()
    constraints.setSearchScope(SearchControls.SUBTREE_SCOPE)

    val filterStr = s"CN=$username"

    Option(ctx.search(mailStr, filterStr, constraints)).flatMap(
      getSearchResults(getUserInfo)(_).headOption
    )
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
    val email = readAttr("userPrincipalName").getOrElse("")
    val userName = email.takeWhile(_ != '@')

    LDAPUserInfo(userName, firstName, lastName, email)
  }
  
  private def getSearchResults[T](makeResult: SearchResult => T)(searchResults: NamingEnumeration[SearchResult]): Seq[T] = {
    val result = scala.collection.mutable.Buffer[T]()
    
    while(searchResults.hasMore) {
      try {
        val sr = searchResults.next()
        result += makeResult(sr)        
      }
    }
    result.toSeq
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
      val getDnAndInfo = Option(searchResult).map {
        getSearchResults( sr =>
          (sr.getAttributes.get("distinguishedName").get().asInstanceOf[String], getUserInfo(sr)))
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
