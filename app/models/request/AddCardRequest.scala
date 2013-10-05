package models.request

import models.domain
import org.joda.time.DateTime
import play.api.libs.json.Json

case class AddCardRequest( recipients: Seq[String],
                           sender: String,
                           message: String) {
  def toCard: domain.Card = {
    val now = DateTime.now()
    domain.Card(None, sender, now, message)
  }
}

object AddCardRequest {
  implicit val format = Json.format[AddCardRequest]
}