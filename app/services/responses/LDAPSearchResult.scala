package services.responses

import play.api.libs.json._

case class LDAPSearchResult( userName: String,
                             firstName: String,
                             lastName: String,
                             email: String)

object LDAPSearchResult {
  implicit val format = Json.format[LDAPSearchResult]
}