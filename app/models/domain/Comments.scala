package models.domain

import scala.slick.driver.MySQLDriver.simple._
import play.api.libs.json.Json
import org.joda.time.DateTime
import mappers.DateTimeMapper._

case class Comment( id: Option[Int],
                    card_id: Int,
                    author: String,
                    date: DateTime,
                    message: String)

object Comment {
  implicit val format = Json.format[Comment]
}

object Comments extends Table[Comment]("comment") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def card_id = column[Int]("card_id")
  def author = column[String]("author")
  def date = column[DateTime]("date")
  def message = column[String]("message")
  def * = id.? ~ card_id ~ author ~ date ~ message <> (Comment(_,_,_,_,_), Comment.unapply)

  def card = foreignKey("CARD_FK", card_id, Cards)(_.id)
}
