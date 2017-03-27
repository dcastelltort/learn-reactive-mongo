package models

import play.api.libs.json.Json

/**
  * Created by dcastelltort on 24/03/17.
  */
case class User(
                 age: Int,
                 firstName: String,
                 lastName : String,
                 feeds: List[Feed])

case class Feed(name: String, url: String)

object JsonFormats {

  implicit val feedFormat = Json.format[Feed]
  implicit val userFormat = Json.format[User]
}