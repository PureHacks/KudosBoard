package controllers

import play.api.mvc._
import services.requests.LoginRequest
import services.{UserService, LDAPContext}
import play.api.libs.json.Json
import scala.util.Try

trait Auth extends Controller {

  final def fail(reason: String) = {
    Unauthorized("must be authenticated")
  }

  def getUsername(request: RequestHeader): Option[String] = {
    for {
      userCookie <- request.cookies.get("user")
      cookieJson <- Try(Json.parse(userCookie.value)).toOption
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

  def mockLogin = Action(parse.json) { implicit request =>
    request.body.asOpt[LoginRequest] match {
      case Some(loginRequest) =>
        val username = loginRequest.username
        UserService.getUser(username) match {
          case Some(user) =>
            val cookie = Cookie("user", Json.toJson(user).toString)
            Ok("ok").withCookies(cookie)
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
        //val auth = LDAPContext.searchContext.authenticate(username, password)
        val auth = LDAPContext.authenticate(username, password)
        auth match {
          case Some(userInfo) =>
            val cookieValue = Json.toJson(userInfo).toString()
            val userCookie = Cookie("username", cookieValue)
            Ok("ok").withCookies(userCookie)
          case None =>
            Unauthorized("Authentication failed")
        }
      case None =>
        BadRequest
    }

  }
}
