package models.domain

import scala.slick.driver.MySQLDriver.simple._
import play.api.libs.json.Json

case class User( userName: String,
                 email: String,
                 firstName: String,
                 lastName: String)

object User {
  implicit val format = Json.format[User]
}

class Users extends Table[User]("user") {
  def username = column[String]("username", O.PrimaryKey)
  def firstname = column[String]("firstname")
  def lastname = column[String]("lastname")
  def email = column[String]("email")

  def * = username ~ email ~ firstname ~ lastname <> (User(_,_,_,_), User.unapply)

  def cards = for {
      recipient <- DAO.recipients
      if recipient.username === username
      card <- recipient.card
    } yield card
}
