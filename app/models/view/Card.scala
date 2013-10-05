package models.view

import org.joda.time.DateTime
import play.api.libs.json._
import models._
import scala.slick.driver.MySQLDriver.simple._

case class Card( id: Option[Int],
                 recipient: List[String],
                 senders: List[String],
                 date: DateTime,
                 message: String,
                 tags: List[String],
                 comments: List[Comment])

object Card {
  implicit val format = Json.format[Card]

  def fromDM(card: domain.Card): Card = {
    import Database.threadLocalSession
    services.CardService.db.withSession {
      import card._
      val commentsQuery = for (comment <- domain.Comments if comment.card_id === id) yield comment
      val comments = commentsQuery.list.map(Comment.fromDM)
      val coauthors = for (author <- domain.CoAuthors if author.card_id === id) yield author.author
      val senders = sender :: coauthors.list
      val recipientsQuery = for (recipient <- domain.Recipients if recipient.card_id === id) yield recipient
      val recipients = recipientsQuery.list.map(_.recipient)
      val tags = for (tag <- domain.Tags if tag.card_id === id) yield tag

      Card(id, recipients, senders, date, message, tags.list.map(_.text), comments)
    }
  }
}