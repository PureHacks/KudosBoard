package services

import javax.naming.Context
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls
import services.responses.LDAPSearchResult
import javax.naming.AuthenticationException

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

  def authenticate(username: String, password: String): Boolean = {
    try {
      new LDAPContext(username, password)
      true
    } catch {
      case ae: AuthenticationException => false
      case e => throw e
    }
  }
}

class LDAPContext(userName: String, password: String) extends InitialDirContext {

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
      env.put(Context.SECURITY_PRINCIPAL, userName) // specify the username
      env.put(Context.SECURITY_CREDENTIALS, password)
      env
    }
    createEnv match {
      case Some(env) => new InitialDirContext(env)
      case None => throw new Exception("LDAP configuration missing or incomplete")
    }
  }

  def findAll(): Seq[LDAPSearchResult] = search("*@nurun.com")

  def search(emailPattern: String): Seq[LDAPSearchResult] = {
    val constraints = new SearchControls()
    constraints.setSearchScope(SearchControls.SUBTREE_SCOPE)

    val filterStr = s"mail=$emailPattern"

    Option(ctx.search(mailStr, filterStr, constraints)).map { searchResults =>
      val result = scala.collection.mutable.Buffer[LDAPSearchResult]()

      while(searchResults.hasMore) {
        try {
          val sr = searchResults.next()
          val dn = sr.getName
          val cn = dn.replaceFirst("CN=", "")
          val names = cn.split(" ")
          val firstName = names.headOption.getOrElse("")
          val lastName = names.lastOption.getOrElse("")

          val attrs = sr.getAttributes
          val email = attrs.get("mail").get().asInstanceOf[String]
          val userName = email.takeWhile(_ != '@')

          result += LDAPSearchResult(userName, firstName, lastName, email)
        }
      }
      result.toSeq
    }.getOrElse(Seq())

  }

  override def finalize() {
    ctx.close()
  }

}
