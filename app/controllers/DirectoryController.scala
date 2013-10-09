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
    val result = ldapContext.searchEmail(emailPattern + "*").toSeq
    Ok(Json.toJson(result))
  }

  def getAll = Action {
    val ldapContext = LDAPContext.searchContext
    val result = ldapContext.findAll().toSeq
    Ok(Json.toJson(result))
  }


}
