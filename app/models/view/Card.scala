package models.view

import org.joda.time.DateTime
import play.api.libs.json._
import models._
import scala.slick.driver.MySQLDriver.simple._
import models.domain.DAO

case class Card( id: Option[Int],
                 recipients: List[domain.User],
                 senders: List[domain.User],
                 date: DateTime,
                 message: String,
                 tags: List[String],
                 comments: List[Comment])

object Card {
  implicit val format = Json.format[Card]

  implicit def fromDM(card: domain.Card): Card = {
    import Database.threadLocalSession
    services.CardService.db.withSession {
      val commentsQuery = for {
        comment <- DAO.comments
        if comment.card_id === card.id
      } yield comment
      val comments = commentsQuery.elements.map(Comment.fromDM).toList
      val coauthors = for {
        author <- DAO.coAuthors if author.card_id === card.id
        user <- author.user
      } yield user
      val sender = for {
        c <- DAO.cards if c.id === card.id
        s <- c.sender
      } yield s
      val senders = (sender union coauthors).list
      val recipientsQuery = for {
        recipient <- DAO.recipients if recipient.card_id === card.id
        user <- recipient.user
      } yield user
      val recipients = recipientsQuery.list
      val tagsQuery = for {
        tag <- DAO.tags if tag.card_id === card.id
      } yield tag
      val tags = tagsQuery.elements.map(_.text).toList

      Card(card.id, recipients, senders, card.date, card.message, tags, comments)
    }
  }
}