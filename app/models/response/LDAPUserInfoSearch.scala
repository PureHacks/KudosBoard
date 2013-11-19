package models.response

import play.api.libs.json._

// for typeahead search
case class LDAPUserInfoSearch( userName: String,
                             firstName: String,
                             lastName: String,
                             email: String)

object LDAPUserInfoSearch {
  implicit val format = Json.format[LDAPUserInfo]
  implicit object LDAPUserInfoSearchFormat extends Format[LDAPUserInfoSearch] {
    def writes(o:LDAPUserInfoSearch): JsValue = JsObject(
      List("value" -> JsString(o.email),
        "token" -> Json.arr(o.firstName, o.lastName),
        "firstName" -> JsString(o.firstName),
        "lastName" -> JsString(o.lastName),
        "userName" -> JsString(o.userName)
      )
    )
    def reads(json: JsValue) = JsSuccess( LDAPUserInfoSearch (
      (json \ "value").as[String],
      (json \ "firstName").as[String],
      (json \ "lastName").as[String],
      (json \ "userName").as[String]
    ))
  }
}
