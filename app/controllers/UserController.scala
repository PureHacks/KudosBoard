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

  def getUsers(firstNamePrefix: String, lastNamePrefix: String) = Action {
    val users = UserService.searchUsers(firstNamePrefix, lastNamePrefix)
    Ok(Json.toJson(users))
  }

  def ldapSync = Action {
    UserService.ldapSync()
    Ok("ok")
  }
}
