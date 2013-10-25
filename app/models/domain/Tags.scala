package models.domain

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.lifted.ForeignKeyAction._
import play.api.libs.json.Json

case class Tag( card_id: Int,
                text: String)

object Tag {
  implicit val format = Json.format[Tag]
}

class Tags extends Table[Tag]("tag") {
  def card_id = column[Int]("card_id")
  def text = column[String]("text")
  def * = card_id ~ text <> (Tag(_,_), Tag.unapply)

  def pk = primaryKey("tag_PK", (card_id, text))

  def card = foreignKey("tag_card_FK", card_id, DAO.cards)(_.id, onDelete = Cascade)
}
