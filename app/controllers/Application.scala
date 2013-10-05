package controllers

import play.api.mvc._

import play.api.libs.json.Json
import services.CardService

object Application extends Controller {

  def index = Action {
    val cards = CardService.getCards()
    val result = Json.toJson(cards)
    Ok(result)
  }

}