package services

import scala.slick.driver.MySQLDriver.simple._
import play.api.db.DB
import play.api.Play.current
import Database.threadLocalSession
import models._
import models.request._
import org.joda.time.DateTime
import models.domain.{Cards, Tags}


object CardService {

  type CardQuery = Query[Cards.type, domain.Card]

  val emptyQuery = for { card <- Cards if 0 == 1} yield card

  val db = Database.forDataSource(DB.getDataSource())

  implicit def toView(q: CardQuery): List[view.Card] = {
    db.withSession {
      q.elements.map(view.Card.fromDM).toList
    }
  }

  def getCard(id: Int): Option[view.Card] = {
    db.withSession {
      val results =
        for( card <- domain.Cards
             if card.id === id)
        yield card
      results.firstOption.map(view.Card.fromDM)
    }
  }

  def getCards(forUser: Option[String] = None, startIndex: Option[Int] = None, maxResults: Option[Int] = None): CardQuery = {
    db.withSession {
      val query = forUser match {
        case Some(username) =>
          for { recipient <- domain.Recipients
                if recipient.username === username
                card <- recipient.card.sortBy(_.id desc)
          } yield {
            card
          }
        case None => for (card <- domain.Cards.sortBy(_.id desc)) yield card
      }
      val withStartIndex = startIndex.map(idx => query.drop(idx)).getOrElse(query)
      val withMaxResults = maxResults.map(rows => withStartIndex.take(rows)).getOrElse(withStartIndex)

      withMaxResults
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
      cardsToDelete.delete > 0
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

  def getCardsByTag(tagText: String): CardQuery = {
    db.withSession {
      for {
        tag <- Tags
        if tag.text === tagText
        card <- tag.card
      } yield card
    }
  }

  def searchByTag(searchTag: String): CardQuery = {
    db.withSession {
      for {
        tag <- Tags
        if tag.text.startsWith(searchTag)
        card <- tag.card
      } yield card
    }
  }

  def smartSearch(terms: List[String]): CardQuery = {
    if (terms.isEmpty)
      emptyQuery
    else {
      val qs = terms.map(smartSearch)
      qs foreach (q => println(q.selectStatement))
      qs.reduce(_ union _)
    }
  }

  def smartSearch(term: String): CardQuery = {
    if (term.startsWith("#")) {
      getCardsByTag(term)
    } else {
      val matchSender = for {
        card <- Cards
        sender <- card.sender
        if sender.firstname.startsWith(term) || sender.lastname.startsWith(term)
      } yield card
      val matchRecipient = for {
        card <- Cards
        recipient <- card.recipients
        if recipient.firstname.startsWith(term) || recipient.lastname.startsWith(term)
      } yield card
      matchSender union matchRecipient
    }
  }

}
