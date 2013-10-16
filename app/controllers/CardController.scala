package controllers

import play.api.mvc._
import services._
import services.CardService._
import models.view.Card
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

  def addCard() = Authenticated(parse.json) { request =>
    request.body.asOpt[AddCardRequest] match {
      case Some(card) =>
        val username = request.user.userName
        CardService.addCard(username, card)
        Ok("ok")
      case None =>
        BadRequest
    }
  }

  def getCards = Action { request =>
    val forUsers = request.queryString.get("forUser").flatMap(_.lastOption)
    val startIndex = request.queryString.get("startIndex").flatMap(_.lastOption).flatMap(x => Try(x.toInt).toOption)
    val maxResults = request.queryString.get("maxResults").flatMap(_.lastOption).flatMap(x => Try(x.toInt).toOption)
    val cards: List[Card] = CardService.getCards(forUsers, startIndex, maxResults)
    val result = Json.toJson(cards)
    Ok(result)
  }

  def getMyCards = Authenticated { request =>
    val startIndex = request.queryString.get("startIndex").flatMap(_.lastOption).flatMap(x => Try(x.toInt).toOption)
    val maxResults = request.queryString.get("maxResults").flatMap(_.lastOption).flatMap(x => Try(x.toInt).toOption)
    val cards: List[Card] = CardService.getCards(Some(request.user.userName), startIndex, maxResults)
    val result = Json.toJson(cards)
    Ok(result)
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

  def search = Action { request =>
    val searchTerms = request.queryString.get("searchTerm").getOrElse(Seq()).toList
    val cards: List[Card] = CardService.smartSearch(searchTerms)
    Ok(Json.toJson(cards))
  }

}
