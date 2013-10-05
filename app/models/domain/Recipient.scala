package models.domain

import scala.slick.driver.MySQLDriver.simple._
import play.api.libs.json.Json

case class Recipient( card_id: Int,
                      recipient: String)

object Recipient{
  implicit val format = Json.format[Recipient]
}

object Recipients extends Table[Recipient]("recipient") {
  def card_id = column[Int]("card_id")
  def recipient = column[String]("recipient")

  def * = card_id ~ recipient <> (Recipient(_,_), Recipient.unapply)

  def pk = primaryKey("recipient_PK", (card_id, recipient))
}