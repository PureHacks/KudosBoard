package controllers

import play.api.mvc._
import services._
import play.api.libs.json._
import models.request.AddCardRequest
import scala.util.Try

object CardController extends Controller with Authentication {

  def getCard(id: Int) = Action {
    CardService.getCard(id) match {
      case Some(card) => Ok(Json.toJson(card))
      case None => NotFound
    }
  }

  def addCard() = authenticated { user =>
    Action(parse.json) { request =>
      request.body.asOpt[AddCardRequest] match {
        case Some(card) =>
          CardService.addCard(user.userName, card)
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

  def getMyCards = authenticated { user =>
    Action { request =>
      val startIndex = request.queryString.get("startIndex").flatMap(_.lastOption).flatMap(x => Try(x.toInt).toOption)
      val maxResults = request.queryString.get("maxResults").flatMap(_.lastOption).flatMap(x => Try(x.toInt).toOption)
      val cards = CardService.getCards(Some(user.userName), startIndex, maxResults)
      val result = Json.toJson(cards)
      Ok(result)
    }
  }

  def getCardComments(id: Int) = Action { request =>
    CardService.getCard(id).map { card =>
      Ok(Json.toJson(card.comments))
    }.getOrElse(NotFound)
  }

  def deleteCard(card_id: Int) = authenticated { user =>
    Action {
      CardService.deleteCard(card_id, user.userName)
      Ok("ok")
    }
  }

}
