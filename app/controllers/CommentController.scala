package controllers

import play.api.mvc._
import services._
import play.api.libs.json._
import models.request._

object CommentController extends Controller {

  def get(id: Int) = Action {
    CardService.getComment(id) match {
      case Some(comment) =>
        val result = Json.toJson(comment)
        Ok(result)
      case None => NotFound
    }
  }

  def add(card_id: Int) = Action(parse.json) { request =>
    request.body.asOpt[AddCommentRequest] match {
      case Some(addCommentRequest) =>
        CardService.addComment(card_id, addCommentRequest)
        Ok("ok")
      case None =>
        BadRequest
    }
  }
}
