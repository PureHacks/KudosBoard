package controllers

import play.api.mvc._

import play.api.libs.json.Json
import services.CardService

object Application extends Controller {

  def index = CardController.getCards

}