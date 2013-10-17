package controllers

import play.api.mvc._

import play.api.libs.json.Json
import services.{UserService, CardService}
import services.CardService._
import models.domain.User
import models.view.Card

object Application extends Controller {

  def index = CardController.getCards

  def smartSearch(searchTerm: String) = Action { request =>
    val usernames = UserService.searchUsername(searchTerm).map(_.userName)
    val tags = CardService.searchTags(searchTerm)
    val result = Json.obj(
      "users" -> usernames,
      "tags" -> tags
    )
    Ok(result)
  }

  def searchTags(prefix: String) = Action {
    val tags: List[String] = CardService.searchTags(prefix)
    Ok(Json.toJson(tags))
  }
}