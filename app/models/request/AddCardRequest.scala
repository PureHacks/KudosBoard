package models.request

import models.domain
import org.joda.time.DateTime
import play.api.libs.json.Json

case class AddCardRequest( recipients: Seq[String],
                           coSenders: Seq[String],
                           message: String) {
}

object AddCardRequest {
  implicit val format = Json.format[AddCardRequest]
}