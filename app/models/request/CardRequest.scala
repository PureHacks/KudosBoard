package models.request

import play.api.libs.json.Json

case class CardRequest(id: Int)

object CardRequest {
  implicit val format = Json.format[CardRequest]
}
