package models

import play.api.libs.json.{Json, OFormat}

case class User(username: String, password: String)

object User {
  implicit val format: OFormat[User] = Json.format[User]
}
