package controllers

import javax.inject._

import akka.actor.ActorSystem
//import models.{Feed, JsonFormats, User}
import play.api.Logger
//import play.api.libs.json
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import reactivemongo.api.Cursor

import scala.concurrent.Future
//import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}

// BSON-JSON conversions/collection
import reactivemongo.play.json._, collection._

import scala.concurrent.ExecutionContext

/**
  * Created by dcastelltort on 24/03/17.
  */
@Singleton
class UserController @Inject()(actorSystem: ActorSystem, val reactiveMongoApi : ReactiveMongoApi)(implicit executionContext: ExecutionContext)
extends Controller with MongoController with ReactiveMongoComponents {

  def collection: JSONCollection = db.collection[JSONCollection]("persons")

  import play.api.data.Form
  import models._
  import models.JsonFormats._

  def create  = Action.async {
    val user = User(29, "John", "Smith", List(Feed("SlashDot News", "http://slashdot.org/slashdot.rdf")))
    val futureResult = collection.insert(user)
    futureResult.map(_ => Ok)
  }

  def createFromJson = Action.async(parse.json) {
    request => request.body.validate[User].map {
      user => collection.insert(user).map {
        lastError => Logger.debug(s"Successfully inserted with LastError: $lastError")
          Created
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def findByName(lastName: String) = Action.async {
    val cursor : Cursor[User] = collection.find(Json.obj("lastName" -> lastName)).sort(Json.obj("created" -> -1)).cursor[User]
    val futureUserList : Future[List[User]] = cursor.collect[List]()

    futureUserList.map {
      persons => Ok(Json.toJson(persons))
    }
  }
}
