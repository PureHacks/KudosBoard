package controllers

import play.api.mvc._
import services._
import play.api.libs.json._
import models.request.AddCardRequest

object CardController extends Controller {

  def getCard(id: Int) = Action {
    CardService.getCard(id) match {
      case Some(card) => Ok(Json.toJson(card))
      case None => NotFound
    }
  }

  def addCard() = Action(parse.json) { request =>
    request.body.asOpt[AddCardRequest] match {
      case Some(card) =>
        CardService.addCard(card)
        Ok("ok")
      case None =>
        BadRequest
    }
  }

  def getCards = Action { request =>
    val forUsers = request.queryString.get("forUser").flatMap(_.lastOption)
    val cards = CardService.getCards(forUsers)
    val result = Json.toJson(cards)
    Ok(result)
  }

}
