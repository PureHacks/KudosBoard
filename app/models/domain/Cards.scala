package models.domain

import scala.slick.driver.MySQLDriver.simple._
import play.api.libs.json.Json
import org.joda.time.DateTime
import mappers.DateTimeMapper._

case class Card( id: Option[Int],
                 sender: String,
                 date: DateTime,
                 message: String)

object Card {
  implicit val format = Json.format[Card]
}

object Cards extends Table[Card]("card") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def sender = column[String]("sender")
  def date = column[DateTime]("date")
  def message = column[String]("message")
  def comments =
    for (comment <- Comments
         if comment.card_id === id) yield comment

  def * = id.? ~ sender ~ date ~ message <> (Card(_,_,_,_), Card.unapply)

  def autoInc = id.? ~ sender ~ date ~ message <> (Card(_,_,_,_), Card.unapply) returning id

}
