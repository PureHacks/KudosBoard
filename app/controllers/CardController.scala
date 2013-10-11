package controllers

import play.api.mvc._
import services._
import play.api.libs.json._
import models.request.AddCardRequest
import scala.util.Try

object CardController extends Controller with Auth {

  def getCard(id: Int) = Action {
    CardService.getCard(id) match {
      case Some(card) => Ok(Json.toJson(card))
      case None => NotFound
    }
  }

  def addCard() = secured { username =>
    Action(parse.json) { request =>
      request.body.asOpt[AddCardRequest] match {
        case Some(card) =>
          CardService.addCard(username, card)
          Ok("ok")
        case None =>
          BadRequest
      }
    }
  }

  def getCards = Action { request =>
    val forUsers = request.queryString.get("forUser").flatMap(_.lastOption)
    val startIndex = request.queryString.get("startIndex").flatMap(_.lastOption).flatMap(x => Try(x.toInt).toOption)
    val maxResults = request.queryString.get("maxResults").flatMap(_.lastOption).flatMap(x => Try(x.toInt).toOption)
    val cards = CardService.getCards(forUsers, startIndex, maxResults)
    val result = Json.toJson(cards)
    Ok(result)
  }

  def getCardComments(id: Int) = Action { request =>
    CardService.getCard(id).map { card =>
      Ok(Json.toJson(card.comments))
    }.getOrElse(NotFound)
  }

}
