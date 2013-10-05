package models.domain

import play.api.libs.json._

case class User( userName: String,
                 email: String,
                 firstName: String,
                 lastName: String)

object User {
  implicit val format = Json.format[User]
}
