package controllers

import play.api.mvc._
import services.requests.LoginRequest
import services.{UserService, LDAPContext}
import play.api.libs.json.Json
import scala.util.Try
import play.api.libs.Crypto

trait Auth extends Controller {

  final def fail(reason: String) = {
    Unauthorized("must be authenticated")
  }

  def getUsername(request: RequestHeader): Option[String] = {
    for {
      userEncCookie <- request.cookies.get("user")
      userCookie = Crypto.decryptAES(userEncCookie.value)
      cookieJson <- Try(Json.parse(userCookie)).toOption
      username <- (cookieJson \ "userName").asOpt[String]
    } yield username
  }

  final def secured[A](action: String => Action[A]) = {

    Security.Authenticated(
      getUsername,
      _ => fail("no ticket found")) {
      username => Action(action(username).parser) {
        request => withTicket(username) {
          action(username)(request)
        }
      }
    }
  }

  private def withTicket(ticket: String)(produceResult: => Result): Result =
    isValid(ticket) match {
      case valid => if (valid) produceResult else fail(s"provided ticket $ticket is invalid")
    }

  def isValid(ticket: String): Boolean = true

}

object Authentication extends Controller {

  private val userCookieName = "user"

  def mockLogin = Action(parse.json) { implicit request =>
    request.body.asOpt[LoginRequest] match {
      case Some(loginRequest) =>
        val username = loginRequest.username
        UserService.getUser(username) match {
          case Some(user) =>
            val encryptedSession = Crypto.encryptAES(Json.toJson(user).toString)
            val session = Cookie("user", encryptedSession)
            Ok("ok").withCookies(session)
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
        auth match {
          case Some(user) =>
            val encryptedUserCookie = Crypto.encryptAES(Json.toJson(user).toString)
            val session = Cookie("user", encryptedUserCookie)
            Ok("ok").withCookies(session)
          case None =>
            Unauthorized("Authentication failed")
        }
      case None =>
        BadRequest
    }
  }

  def logout = Action {
    val cookiesToDiscard = DiscardingCookie(userCookieName)
    Ok("ok").discardingCookies(cookiesToDiscard)
  }
}
