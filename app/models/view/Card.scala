package models.view

import org.joda.time.DateTime
import play.api.libs.json._
import models._
import scala.slick.driver.MySQLDriver.simple._
import models.domain.Cards

case class Card( id: Option[Int],
                 recipients: List[domain.User],
                 senders: List[domain.User],
                 date: DateTime,
                 message: String,
                 tags: List[String],
                 comments: List[Comment])

object Card {
  implicit val format = Json.format[Card]

  def fromDM(card: domain.Card): Card = {
    import Database.threadLocalSession
    services.CardService.db.withSession {
      val commentsQuery = for (comment <- domain.Comments if comment.card_id === card.id) yield comment
      val comments = commentsQuery.list.map(Comment.fromDM)
      val coauthors = for {
        author <- domain.CoAuthors if author.card_id === card.id
        user <- author.user
      } yield user
      val sender = for {
        c <- Cards if c.id === card.id
        s <- c.sender
      } yield s
      val senders = (sender union coauthors).list
      val recipientsQuery = for {
        recipient <- domain.Recipients if recipient.card_id === card.id
        user <- recipient.user
      } yield user
      val recipients = recipientsQuery.list
      val tags = for (tag <- domain.Tags if tag.card_id === card.id) yield tag

      Card(card.id, recipients, senders, card.date, card.message, tags.list.map(_.text), comments)
    }
  }
}