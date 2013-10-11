package controllers

import play.api.mvc._
import play.api.libs.json.Json
import services.LDAPContext
import services.EmailNotification

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

  def sendNotification(emailPattern: String) = Action {
    EmailNotification.send(List(emailPattern), List(emailPattern), "You have received Props from someone","That someone is you! (for now)")
    Ok(Json.toJson("email sent"))
  }
}
