package models.domain

import play.api.libs.json._
import scala.slick.driver.MySQLDriver.simple._

case class CoAuthor( card_id: Int,
                     author: String)

object CoAuthor {
  implicit val format = Json.format[CoAuthor]
}

object CoAuthors extends Table[(CoAuthor)]("coauthor") {
  def card_id = column[Int]("card_id")
  def author = column[String]("author")
  def * = card_id ~ author <> (CoAuthor(_,_), CoAuthor.unapply)

  def pk = primaryKey("coauthors_PK", (card_id, author))
}

