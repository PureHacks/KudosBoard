package controllers

import play.api.mvc._
import play.api.libs.json._
import services.UserService

object UserController extends Controller {

  def getUser(username: String) = Action {
    UserService.getUser(username) match {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound
    }
  }

  def searchNames(firstNamePrefix: String, lastNamePrefix: String) = Action {
    val users = UserService.searchNames(firstNamePrefix, lastNamePrefix)
    Ok(Json.toJson(users))
  }

  def searchUsername(usernamePrefix: String) = Action {
    val users = UserService.searchUsername(usernamePrefix)
    Ok(Json.toJson(users))
  }

  def ldapSync = Action {
    UserService.ldapSync()
    Ok("ok")
  }
}
