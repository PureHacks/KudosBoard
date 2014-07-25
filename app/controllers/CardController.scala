package controllers

import org.omg.CosNaming.NamingContextPackage.NotFound
import play.api.mvc._
import services._
import services.CardService._
import models.view.Card
import play.api.libs.json._
import models.request.AddCardRequest
import scala.util.Try

object CardController extends Controller {

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

  def getCards(startIndex: Int, maxResults: Option[Int]) = Action { request =>
    val forUsers = request.queryString.get("forUser").getOrElse(Seq())
    val tags = request.queryString.get("tag").getOrElse(Seq())
    //val startIndex = request.queryString.get("startIndex").flatMap(_.lastOption).flatMap(x => Try(x.toInt).toOption)
    //val maxResults = request.queryString.get("maxResults").flatMap(_.lastOption).flatMap(x => Try(x.toInt).toOption)
    val searchTerms = request.queryString.get("searchTerm").getOrElse(Seq()).toList
    val sortBy = request.queryString.get("sortBy").flatMap(_.lastOption).getOrElse("date")
    val sortDir = request.queryString.get("sortDir").flatMap(_.lastOption).getOrElse("desc")
    val cards: List[Card] = CardService.getCards(forUsers, startIndex, maxResults, tags, searchTerms, sortBy, sortDir)
    val result = Json.toJson(cards)
    Ok(result)
  }

  def getMyCards = Authenticated { request =>
    val startIndex = request.queryString.get("startIndex").flatMap(_.lastOption).flatMap(x => Try(x.toInt).toOption)
    val maxResults = request.queryString.get("maxResults").flatMap(_.lastOption).flatMap(x => Try(x.toInt).toOption)
    val sortBy = request.queryString.get("sortBy").flatMap(_.lastOption).getOrElse("date")
    val sortDir = request.queryString.get("sortDir").flatMap(_.lastOption).getOrElse("desc")
    val cards: List[Card] = CardService.getCards(Seq(request.user.userName), startIndex.getOrElse(1), maxResults, Seq(), Seq(), sortBy, sortDir)
    val result = Json.toJson(cards)
    Ok(result)
  }

  def getByTag(tag: String) = Action { request =>
    val cards: List[Card] = CardService.getCardsByTags(Seq(tag))
    Ok(Json.toJson(cards))
  }

  def getCardComments(id: Int) = Action { request =>
    CardService.getCard(id).map { card =>
      Ok(Json.toJson(card.comments))
    }.getOrElse(NotFound)
  }

  def deleteCard(card_id: Int) = Authenticated { request =>
    CardService.deleteCard(card_id, request.user.userName)
    Ok("ok")
  }

  def search = Action { request =>
    val searchTerms = request.queryString.get("searchTerm").getOrElse(Seq()).toList
    val cards: List[Card] = CardService.smartSearch(searchTerms)
    Ok(Json.toJson(cards))
  }

}
