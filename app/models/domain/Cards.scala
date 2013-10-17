package models.domain

import scala.slick.driver.MySQLDriver.simple._
import play.api.libs.json.Json
import org.joda.time.DateTime
import mappers.DateTimeMapper._

case class Card( id: Option[Int],
                 sender_id: String,
                 date: DateTime,
                 message: String)

object Card {
  implicit val format = Json.format[Card]
}

class Cards extends Table[Card]("card") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def sender_id = column[String]("sender")
  def date = column[DateTime]("date")
  def message = column[String]("message")

  def * = id.? ~ sender_id ~ date ~ message <> (Card(_,_,_,_), Card.unapply)
  def autoInc = id.? ~ sender_id ~ date ~ message <> (Card(_,_,_,_), Card.unapply) returning id

  def sender = foreignKey("card_sender_FK", sender_id, DAO.users)(_.username)

  def comments = for (comment <- DAO.comments if comment.card_id === id) yield comment

  def recipients = for {
    recipient <- DAO.recipients if recipient.card_id === id
    user <- recipient.user
  } yield user

  def coAuthors = for {
    coAuthor <- DAO.coAuthors if coAuthor.card_id === id
    user <- coAuthor.user
  } yield user

  def tags = for (tag <- DAO.tags if tag.card_id === id) yield tag
}
