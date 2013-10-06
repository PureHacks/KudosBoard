package controllers

import play.api.mvc._
import play.api.libs.json.Json
import play.api.data._
import play.api.data.Forms._
import services.LDAPContext

object DirectoryController extends Controller {

  def searchEmail(emailPattern: String) = Action {
    val ldapContext = LDAPContext.searchContext
    val result = ldapContext.search(emailPattern + "*")
    Ok(Json.toJson(result))
  }

  def getAll = Action {
    val ldapContext = LDAPContext.searchContext
    val result = ldapContext.findAll()
    Ok(Json.toJson(result))
  }

  def login = Action { implicit request =>
    val loginForm = Form(
      tuple(
        "username" -> text,
        "password" -> text
      )
    )
    val (username, password) = loginForm.bindFromRequest().get
    LDAPContext.authenticate(username, password) match {
      case true => Ok("ok")
      case false => Unauthorized("Authentication failed")
    }
  }
}
