package controllers

import play.api.mvc._
import services.{UserService, LDAPContext}
import play.api.libs.json.Json
import scala.util.Try
import play.api.libs.Crypto
import models.request.LoginRequest

trait Authentication extends Controller {
  import models.domain.User

  final def fail(reason: String) = {
    Unauthorized("must be authenticated")
  }

  def getUser(request: RequestHeader): Option[User] = {
    for {
      userEncCookie <- request.cookies.get(AuthController.sessionCookieName)
      userCookie = Crypto.decryptAES(userEncCookie.value)
      cookieJson <- Try(Json.parse(userCookie)).toOption
      user <- cookieJson.asOpt[User]
    } yield user 
  }

  final def authenticated[A](action: User => Action[A]) = {

    Security.Authenticated(
      getUser,
      _ => fail("no ticket found")) {
      user =>
        val userAction = action(user) 
        Action(userAction.parser) {
        request => userAction(request)
      }
    }
  }

}

object AuthController extends Controller {

  val sessionCookieName = "session"
  val usernameCookie = "username"

  def mockLogin = Action(parse.json) { implicit request =>
    request.body.asOpt[LoginRequest] match {
      case Some(loginRequest) =>
        val username = loginRequest.username
        UserService.getUser(username) match {
          case Some(user) =>
            val sessionCookie = Crypto.encryptAES(Json.toJson(user).toString)
            val session = Cookie(sessionCookieName, sessionCookie)
            val username = Cookie(usernameCookie, user.userName, httpOnly = false)
            Ok("ok").withCookies(session, username)
          case None =>
            Unauthorized("Authentication failed")
        }
      case None =>
        BadRequest
    }
  }

  def login = Action(parse.json) { implicit request =>
    request.body.asOpt[LoginRequest] match {
      case Some(loginRequest) =>
        val username = loginRequest.username
        val password = loginRequest.password
        val auth = LDAPContext.authenticate(username, password)
        // FIXME auth is the LDAP user info, may not be in sync with the User table
        auth match {
          case Some(ldapUserInfo) =>
            UserService.getUser(ldapUserInfo.userName) match {
              case Some(user) =>
                val sessionCookie = Crypto.encryptAES(Json.toJson(user).toString)
                val session = Cookie(sessionCookieName, sessionCookie, httpOnly = true)
                val username = Cookie(usernameCookie, user.userName, httpOnly = false)
                Ok("ok").withCookies(session, username)
              case None =>
                Unauthorized("Unknown user, not in last LDAP sync")
            }
          case None =>
            Unauthorized("Authentication failed")
        }
      case None =>
        BadRequest
    }
  }

  def logout = Action {
    val cookiesToDiscard = DiscardingCookie(sessionCookieName, usernameCookie)
    Ok("ok").discardingCookies(cookiesToDiscard)
  }
}
