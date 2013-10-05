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

  def getCards(forUser: Option[String] = None): List[view.Card] = {
    db.withSession {
      val query = forUser match {
        case Some(user) =>
          for { card <- domain.Cards
                recipient <- domain.Recipients
                if recipient.card_id === card.id && recipient.recipient === user
          } yield card
        case None => for (card <- domain.Cards.sortBy(_.date)) yield card
      }
      query.list.map(view.Card.fromDM)
    }
  }

  def addCard(request: AddCardRequest) {
    db.withSession {
      val card_id = domain.Cards.autoInc.insert(request.toCard)
      val recipients = request.recipients.map(domain.Recipient(card_id, _))
      domain.Recipients.insertAll(recipients: _*)
      val tags = request.message.split(" ").filter(_.startsWith("#")).map(domain.Tag(card_id, _))
      domain.Tags.insertAll(tags: _*)
    }
  }

  def getComment(comment_id: Int): Option[domain.Comment] = {
    db.withSession {
      val query = for (comment <- domain.Comments if comment.id === comment_id) yield comment
      query.firstOption
    }
  }

  def addComment(card_id: Int, request: AddCommentRequest) {
    db.withSession {
      import request._
      val now = DateTime.now
      val comment = models.domain.Comment(None, card_id, author, now, message)
      domain.Comments.insert(comment)
    }
  }

}
