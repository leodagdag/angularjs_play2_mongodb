package controllers

import models.User

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.json._
import play.api.mvc._

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers._

import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._

import scala.concurrent._

/**
 * @author leodagdag
 */
object Users extends Controller with MongoController {

  /** Full Person validator */
  val validateUser: Reads[JsObject] = (
    (__ \ 'username).json.pickBranch and
      (__ \ 'password).json.pickBranch and
      (__ \ 'firstName).json.pickBranch and
      (__ \ 'lastName).json.pickBranch
    ).reduce

  /** Generates a new ID and adds it to your JSON using Json extended notation for BSON */
  val generateId: Reads[JsObject] = (__ \ '_id \ '$oid).json.put(JsString(BSONObjectID.generate.stringify))
  /** Generates a new date and adds it to your JSON using Json extended notation for BSON */
  val generateCreated: Reads[JsObject] = (__ \ 'created \ '$date).json.put(JsNumber((new java.util.Date).getTime))
  /** Updates Json by adding both ID and date */
  val addMongoIdAndDate: Reads[JsObject] = __.json.update((generateId and generateCreated).reduce)

  /** Writes an ID in Json Extended Notation */
  val toObjectId: OWrites[String] = OWrites[String] {
    s => Json.obj("_id" -> Json.obj("$oid" -> s))
  }

  /** Full Person validator */
  val validatePerson: Reads[JsObject] = (
    (__ \ 'username).json.pickBranch and
      (__ \ 'password).json.pickBranch and
      (__ \ 'firstName).json.pickBranch and
      (__ \ 'lastName).json.pickBranch
    ).reduce

  /** Converts JSON into Mongo update selector by just copying whole object in $set field */
  val toMongoUpdate: Reads[JsObject] = (__ \ '$set).json.copyFrom(__.json.pick)

  val toUser: OWrites[User] = OWrites[User] {
    user => Json.obj("_id" -> user.id.map(oid => oid.stringify),
      "username" -> user.username,
      "password" -> user.password,
      "role" -> user.role,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "created" -> user.created.map(dt => dt.getMillis),
      "updated" -> user.updated.map(dt => dt.getMillis)
    )
  }


  def create = Action(parse.json) {
    request =>
      request.body.transform(validateUser andThen addMongoIdAndDate).map {
        jsobj =>
          Async {
            User.db
            User.db.insert(jsobj).map {
              p =>
                Ok(jsobj)
            }.recover {
              case e =>
                InternalServerError(JsString("exception %s".format(e.getMessage)))
            }
          }
      }.recoverTotal {
        err =>
          InternalServerError(JsError.toFlatJson(err))
      }
  }


  def updatePerson(id: String) = Action(parse.json) {
    request =>
      request.body.transform(validatePerson).flatMap {
        jsobj =>
          jsobj.transform(toMongoUpdate).map {
            updateSelector =>
              Async {
                User.db.update(
                  toObjectId.writes(id),
                  updateSelector
                ).map {
                  lastError =>
                    if (lastError.ok)
                      Ok(Json.obj("msg" -> s"person $id updated"))
                    else
                      InternalServerError(JsString("error %s".format(lastError.stringify)))
                }
              }
          }
      }.recoverTotal {
        e =>
          BadRequest(JsError.toFlatJson(e))
      }
  }


  def fetch(username: String) = Action {
    implicit val reader = User.UserBSONReader
    Async {
      // get the documents having this id (there will be 0 or 1 result)

      // ... so we get optionally the matching article, if any
      // let's use for-comprehensions to compose futures (see http://doc.akka.io/docs/akka/2.0.3/scala/futures.html#For_Comprehensions for more information)
      for {
      // get a future option of article
        maybeUser: Option[User] <- User.byUsername(username)
        // if there is some article, return a future of result with the article and its attachments
        result <- maybeUser.map {
          (user: User) =>
            println(user)
            Future(Ok(Json.toJson(toUser.writes(user))))
        }.getOrElse(Future(NotFound))
      } yield result
    }
  }

/*
 def all(username: String) = Action {
   Async {
     for {
       maybeUsers: List[User] <- User.all()
      /*
    users <- maybeUsers.map{
         u =>
           toUser.writes(u)
       }

       result <-          Future(Ok(Json.arr(maybeUsers:_*)))


     } yield result

   }
 }
 */

}
