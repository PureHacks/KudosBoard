package services.requests

import play.api.libs.json._

case class LoginRequest( username: String,
                         password: String)

object LoginRequest {
  implicit val format = Json.format[LoginRequest]
}
