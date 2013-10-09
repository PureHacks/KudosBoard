package controllers

import play.api.mvc._
import services._
import play.api.libs.json._
import models.request.AddCardRequest
import play.mvc.Security.Authenticated

/*trait Auth extends Controller {
  def authenticated[A](f: => String => Action[A]): Action[A] = Action { request: Request[AnyContent] =>
    request.cookies.get("username") match {
      case Some(username) => f(username)(request)
      case None => NotFound()
    }
  }
}*/

object CardController extends Controller { //with Auth {

  def getCard(id: Int) = Action {
    CardService.getCard(id) match {
      case Some(card) => Ok(Json.toJson(card))
      case None => NotFound
    }
  }

  def addCard() = //authenticated { username =>
    Action(parse.json) { request =>
      request.body.asOpt[AddCardRequest] match {
        case Some(card) =>
          val username = request.queryString.get("username").flatMap(_.headOption).getOrElse("")
          CardService.addCard(username, card)
          Ok("ok")
        case None =>
          BadRequest
      }
    //}
  }

  def getCards = Action { request =>
    val forUsers = request.queryString.get("forUser").flatMap(_.lastOption)
    val cards = CardService.getCards(forUsers)
    val result = Json.toJson(cards)
    Ok(result)
  }

}
