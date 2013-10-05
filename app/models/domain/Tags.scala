package models.domain

import scala.slick.driver.MySQLDriver.simple._
import play.api.libs.json.Json

case class Tag( card_id: Int,
                text: String)

object Tag {
  implicit val format = Json.format[Tag]
}

object Tags extends Table[Tag]("tag") {
  def card_id = column[Int]("card_id")
  def text = column[String]("text")
  def * = card_id ~ text <> (Tag(_,_), Tag.unapply)
}
