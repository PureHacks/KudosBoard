package models.request

import play.api.libs.json._

case class AddCommentRequest( author: String,
                              message: String)

object AddCommentRequest {
  implicit val format = Json.format[AddCommentRequest]
}