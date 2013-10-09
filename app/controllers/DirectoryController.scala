package controllers

import play.api.mvc._
import play.api.libs.json.Json
import play.api.data._
import play.api.data.Forms._
import services.LDAPContext
import services.requests.LoginRequest

object DirectoryController extends Controller {

  def searchEmail(emailPattern: String) = Action {
    val ldapContext = LDAPContext.searchContext
    val result = ldapContext.searchEmail(emailPattern + "*")
    Ok(Json.toJson(result))
  }

  def getAll = Action {
    val ldapContext = LDAPContext.searchContext
    val result = ldapContext.findAll()
    Ok(Json.toJson(result))
  }

  def login = Action(parse.json) { implicit request =>

    request.body.asOpt[LoginRequest] match {
      case Some(loginRequest) =>
        val username = loginRequest.username
        val password = loginRequest.password
        LDAPContext.searchContext.authenticate(username, password) match {
          case Some(userInfo) =>
            val cookieValue = Json.toJson(userInfo).toString()
            val userCookie = Cookie("user", cookieValue)
            Ok("ok").withCookies(userCookie)
          case None =>
            Unauthorized("Authentication failed")
        }
      case None =>
        BadRequest
    }

  }
}
