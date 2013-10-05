package controllers

import play.api.mvc._
import play.api.libs.json._
import models.domain.User

object UserController extends Controller {

  def getUser(userName: String) = Action {
    // TODO
    val user = User(userName, "", "", "")
    Ok(Json.toJson(user))
  }

  def getUsers(firstNamePrefix: String, lastNamePrefix: String) = Action {
    // TODO
    val users: Seq[User] = Seq()
    Ok(Json.toJson(users))
  }
}
