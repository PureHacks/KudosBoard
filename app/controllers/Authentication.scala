package controllers

import play.api.mvc._
import services.{UserService, LDAPContext}
import play.api.libs.json.Json
import scala.util.Try
import play.api.libs.Crypto
import models.request.LoginRequest
import models.domain.User
import scala.concurrent.Future

class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request)

object Authenticated extends ActionBuilder[AuthenticatedRequest] {
  def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
    getUser(request).map { user =>
      block(new AuthenticatedRequest(user, request))
    } getOrElse {
      Future.successful(Results.Unauthorized)
    }
  }

  def getUser(request: RequestHeader): Option[User] = {
    for {
      userEncCookie <- request.cookies.get(AuthController.sessionCookieName)
      userCookie = Crypto.decryptAES(userEncCookie.value)
      cookieJson <- Try(Json.parse(userCookie)).toOption
      user <- cookieJson.asOpt[User]
    } yield user
  }
}

object AuthController extends Controller {
  import utils.FormatJsError._

  val sessionCookieName = "session"
  val usernameCookie = "username"

  def mockLogin = Action(parse.json) { implicit request =>
    request.body.validate[LoginRequest].map { loginRequest =>
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
    }.recoverTotal(jsErr => BadRequest(jsErr.toString))
  }

  def login = Action(parse.json) { implicit request =>
    request.body.validate[LoginRequest].map { loginRequest =>
      val username = loginRequest.username
      val password = loginRequest.password
      LDAPContext.authenticate(username, password) match {
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
    }.recoverTotal(jsErr => BadRequest(Json.toJson(jsErr)))
  }

  def logout = Action {
    val cookiesToDiscard = Seq(
      DiscardingCookie(sessionCookieName),
      DiscardingCookie(usernameCookie))
    Ok("ok").discardingCookies(cookiesToDiscard: _*)
  }
}
