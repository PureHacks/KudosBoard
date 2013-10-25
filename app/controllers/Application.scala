package controllers

import play.api.mvc._

import play.api.libs.json.Json
import services.{UserService, CardService}

object Application extends Controller {

  def index = CardController.getCards(startIndex = 1, maxResults = None)

  def smartSearch(searchTerm: String) = Action { request =>
    val usernames = UserService.searchUsername(searchTerm).map(_.userName)
    val tags = CardService.searchTags(searchTerm)
    val result = Json.obj(
      "users" -> usernames,
      "tags" -> tags
    )
    Ok(result)
  }

  def searchTags = Action { request =>
    val prefixes = request.queryString.get("startsWith")
    val tags: List[String] = CardService.searchTags(prefixes)
    Ok(Json.toJson(tags))
  }
}