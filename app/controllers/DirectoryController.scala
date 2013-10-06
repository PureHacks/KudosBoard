package controllers

import play.api.mvc._
import play.api.libs.json.Json

object DirectoryController extends Controller {

  def searchEmail(emailPattern: String) = Action {
    val ldapContext = new services.LDAPContext
    val result = ldapContext.search(emailPattern + "*")
    Ok(Json.toJson(result))
  }

  def getAll = Action {
    val ldapContext = new services.LDAPContext
    val result = ldapContext.findAll()
    Ok(Json.toJson(result))
  }
}
