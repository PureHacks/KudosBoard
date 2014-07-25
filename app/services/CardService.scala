package services

import scala.slick.driver.MySQLDriver.simple._
import play.api.db.DB
import play.api.Play.current
import Database.threadLocalSession
import models._
import models.request._
import org.joda.time.DateTime
import models.domain.{Cards, Tags, DAO}


object CardService {

  type CardQuery = Query[Cards, domain.Card]

  val emptyQuery = for { card <- DAO.cards if 0 == 1} yield card

  val db = Database.forDataSource(DB.getDataSource())

  implicit def toView(q: CardQuery): List[view.Card] = {
    db.withSession {
      q.elements.map(view.Card.fromDM).toList
    }
  }

  def getCard(id: Int): Option[view.Card] = {
    db.withSession {
      val results =
        for( card <- DAO.cards
             if card.id === id)
        yield card
      results.firstOption.map(view.Card.fromDM)
    }
  }

  /**
   *
   * @param forUsers Cards for any of the users in the sequence
   * @param startIndex
   * @param maxResults
   * @param tags Cards with one of the given tags
   * @param searchTerms
   * @return All cards matching all of the above criteria
   */
  def getCards(forUsers: Seq[String] = Seq(),
               startIndex: Int = 1,
               maxResults: Option[Int] = None,
               tags: Seq[String] = Seq(),
               searchTerms: Seq[String] = Seq(),
               sortBy: String,
               sortDir: String): CardQuery = {
    db.withSession {

      val bySearchTerms = searchTerms.isEmpty match {
        case false =>
          (sortBy, sortDir) match {
            case ("sender", _) => smartSearch(searchTerms.toList).sortBy(_.sender_id)
            case (_, "desc") => smartSearch(searchTerms.toList).sortBy(_.date desc)
            case (_, _) => smartSearch(searchTerms.toList).sortBy(_.date)
          }
          smartSearch(searchTerms.toList).sortBy(_.date)
        case true =>
          (sortBy, sortDir) match {
            case ("sender", _) => Query(DAO.cards).sortBy(_.sender_id)
            case (_, "desc") => Query(DAO.cards).sortBy(_.date desc)
            case (_, _) => Query(DAO.cards).sortBy(_.date)
          }
      }


      val byUser = forUsers.isEmpty match {
        case false =>
          for {
            card <- bySearchTerms
            if (for (r <- card.recipients if r.username inSetBind forUsers) yield r).exists
          } yield card
        case true =>
          bySearchTerms
      }
      val byUserAndTag = tags.isEmpty match {
        case false =>
          for {
            card <- byUser
            tag <- card.tags
            if tag.text inSetBind tags
          } yield card
        case true =>
          byUser
      }
      val withStartIndex = for (c <- byUserAndTag.drop((startIndex-1) max 0)) yield c
      val withMaxResults = maxResults.map(rows => for (c <- withStartIndex.take(rows)) yield c).getOrElse(withStartIndex)

      withMaxResults
    }
  }

  def addCard(sender: String, request: AddCardRequest) {
    db.withSession {
      val now = DateTime.now
      val card = domain.Card(None, sender, now, request.message)
      val card_id = DAO.cards.autoInc.insert(card)
      val recipients = request.recipients.map(domain.Recipient(card_id, _))
      DAO.recipients.insertAll(recipients: _*)
      val tags = request.message.split(" ").filter(_.startsWith("#")).map(tagWord => domain.Tag(card_id, tagWord.takeWhile(_.isLetterOrDigit)))
      DAO.tags.insertAll(tags: _*)
      val coAuthors = request.senders.map(domain.CoAuthor(card_id, _))
      DAO.coAuthors.insertAll(coAuthors: _*)
      EmailNotification.sendNotification(card_id)
    }
  }

  def deleteCard(card_id: Int, username: String): Boolean = {
    db.withSession {
      val cardsToDelete = for {
        card <- DAO.cards
        if card.id === card_id
        if (for {sender <- card.sender if sender.username === username} yield sender).exists
      } yield card
      cardsToDelete.delete > 0
    }
  }

  def getComment(comment_id: Int): Option[domain.Comment] = {
    db.withSession {
      val query = for (comment <- DAO.comments if comment.id === comment_id) yield comment
      query.firstOption
    }
  }

  def addComment(card_id: Int, author: String, request: AddCommentRequest) {
    db.withSession {
      import request._
      val now = DateTime.now
      val comment = domain.Comment(None, card_id, author, now, message)
      DAO.comments.insert(comment)
    }
  }

  def getCardsByTags(tags: Seq[String]): CardQuery = {
    db.withSession {
      for {
        tag <- DAO.tags
        if tag.text inSetBind tags
        card <- tag.card
      } yield card
    }
  }

  def searchByTag(searchTag: String): CardQuery = {
    for {
      card <- DAO.cards
      if (for (tag <- card.tags if tag.text startsWith searchTag) yield tag).exists
    } yield card
  }

  def smartSearch(terms: List[String]): CardQuery = {
    if (terms.isEmpty)
      emptyQuery
    else {
      val qs = terms.map(smartSearch)
      qs.reduce(_ union _)
    }
  }

  def smartSearch(term: String): CardQuery = {
    searchByTag(term) union searchByUsername(term)
  }

  def searchByUsername(usernamePrefix: String): CardQuery = {
    for {
      card <- DAO.cards
      if (for (recipient <- card.recipients if recipient.username startsWith usernamePrefix) yield recipient).exists
    } yield card
  }

  def searchTags(prefixes: Option[Seq[String]]): List[String] = {
    db.withSession {
      prefixes.filter(!_.isEmpty) match {
        case None =>
          (for (t <- DAO.tags) yield t).groupBy(_.text).map(_._1).list
        case Some(tagPrefixes) =>
          (tagPrefixes.map(searchTagsQuery) reduce (_ union _)).list
      }
    }
  }

  def searchTags(prefix: String): List[String] = searchTags(Some(Seq(prefix)))

  def searchTagsQuery(prefix: String): Query[_, String] = {
      val tags = for {
        tag <- DAO.tags
        if tag.text startsWith prefix
      } yield tag
      tags.groupBy(_.text).map(_._1)
  }

}
