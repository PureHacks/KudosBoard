package services

import play.api.libs.json._
import scala.slick.driver.MySQLDriver.simple._
import play.api.db.DB
import play.api.Play.current
import Database.threadLocalSession
import models._
import models.request._
import org.joda.time.DateTime

case class CardRequest(id: Int)

object CardRequest {
  implicit val format = Json.format[CardRequest]
}

object CardService {

  val db = Database.forDataSource(DB.getDataSource())

  def getCard(id: Int): Option[view.Card] = {
    db.withSession {
      val results =
        for( card <- domain.Cards
             if card.id === id)
        yield card
      results.firstOption.map(view.Card.fromDM)
    }
  }

  def getCards(forUser: Option[String] = None, startIndex: Option[Int] = None, maxResults: Option[Int] = None): List[view.Card] = {
    db.withSession {
      val query = forUser match {
        case Some(username) =>
          for { recipient <- domain.Recipients
                if recipient.username === username
                card <- recipient.card.sortBy(_.date desc)
          } yield {
            card
          }
        case None => for (card <- domain.Cards.sortBy(_.date desc)) yield card
      }
      val withStartIndex = startIndex.map(idx => query.drop(idx)).getOrElse(query)
      val withMaxResults = maxResults.map(rows => withStartIndex.take(rows)).getOrElse(withStartIndex)

      withMaxResults.list.map(view.Card.fromDM)
    }
  }

  def addCard(sender: String, request: AddCardRequest) {
    db.withSession {
      val now = DateTime.now
      val card = domain.Card(None, sender, now, request.message)
      val card_id = domain.Cards.autoInc.insert(card)
      val recipients = request.recipients.map(domain.Recipient(card_id, _))
      domain.Recipients.insertAll(recipients: _*)
      val tags = request.message.split(" ").filter(_.startsWith("#")).map(domain.Tag(card_id, _))
      domain.Tags.insertAll(tags: _*)
      val coAuthors = request.senders.map(domain.CoAuthor(card_id, _))
      domain.CoAuthors.insertAll(coAuthors: _*)
      EmailNotification.sendNotification(card_id)
    }
  }

  def deleteCard(card_id: Int, username: String): Boolean = {
    db.withSession {
      val cardsToDelete = for {
        card <- domain.Cards
        if card.id === card_id
        if (for {sender <- card.sender if sender.username === username} yield sender).exists
      } yield card
      val result = !cardsToDelete.list.isEmpty
      cardsToDelete.delete
      result
    }
  }

  def getComment(comment_id: Int): Option[domain.Comment] = {
    db.withSession {
      val query = for (comment <- domain.Comments if comment.id === comment_id) yield comment
      query.firstOption
    }
  }

  def addComment(card_id: Int, author: String, request: AddCommentRequest) {
    db.withSession {
      import request._
      val now = DateTime.now
      val comment = models.domain.Comment(None, card_id, author, now, message)
      domain.Comments.insert(comment)
    }
  }

}
