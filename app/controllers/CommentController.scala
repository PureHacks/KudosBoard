package controllers

import play.api.mvc._
import services._
import play.api.libs.json._
import models.request._
import utils.FormatJsError._

object CommentController extends Controller {

  def get(id: Int) = Action {
    CardService.getComment(id) match {
      case Some(comment) =>
        val result = Json.toJson(comment)
        Ok(result)
      case None => NotFound
    }
  }

  def add(card_id: Int) = Authenticated(parse.json) { request =>
    request.body.validate[AddCommentRequest].map { addCommentRequest =>
      CardService.addComment(card_id, request.user.userName, addCommentRequest)
      Ok("ok")
    }.recoverTotal(jsErr => BadRequest(Json.toJson(jsErr)))
  }
}
