package services.responses

import play.api.libs.json._

case class LDAPUserInfo( userName: String,
                             firstName: String,
                             lastName: String,
                             email: String)

object LDAPUserInfo {
  implicit val format = Json.format[LDAPUserInfo]
}