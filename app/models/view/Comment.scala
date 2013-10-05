package models.view

import org.joda.time.DateTime
import play.api.libs.json._

case class Comment( id: Option[Int],
                    card_id: Int,
                    author: String,
                    date: DateTime,
                    message: String)

object Comment {
  implicit val format = Json.format[Comment]

  def fromDM(dm: models.domain.Comment): Comment = {
    import dm._
    Comment(id, card_id, author, date, message)
  }
}