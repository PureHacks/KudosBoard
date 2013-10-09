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
  def username = column[String]("username")

  def * = card_id ~ username <> (Recipient(_,_), Recipient.unapply)

  def pk = primaryKey("recipient_PK", (username, card_id))

  def card = foreignKey("card_FK", card_id, Cards)(_.id)
  def user = foreignKey("user_FK", username, Users)(_.username)
}