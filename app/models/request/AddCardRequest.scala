package models.request

import play.api.libs.json.Json

case class AddCardRequest( recipients: Seq[String],
                           senders: Seq[String],
                           message: String) {
}

object AddCardRequest {
  implicit val format = Json.format[AddCardRequest]
}