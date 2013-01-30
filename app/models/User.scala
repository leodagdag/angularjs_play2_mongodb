package models

import org.joda.time.DateTime

import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType._
import reactivemongo.api._
import indexes.Index
import indexes.Index
import indexes.Index
import reactivemongo.bson._
import reactivemongo.bson.handlers._
import reactivemongo.bson.handlers.DefaultBSONHandlers._
import play.modules.reactivemongo.PlayBsonImplicits._
import play.api._

import libs.json._
import libs.json.JsObject
import libs.json.JsString
import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._
import scala.concurrent.ExecutionContext.Implicits.global

import concurrent.Future
import reactivemongo.api.QueryBuilder
import reactivemongo.bson.BSONDateTime
import reactivemongo.bson.BSONString
import reactivemongo.api.QueryBuilder
import reactivemongo.bson.BSONDateTime
import reactivemongo.bson.BSONString
import reactivemongo.api.QueryBuilder
import reactivemongo.bson.BSONDateTime
import reactivemongo.bson.BSONString

/**
 * @author leodagdag
 */
case class User(id: Option[BSONObjectID],
                username: String,
                password: String,
                role: String,
                firstName: String,
                lastName: String,
                created: Option[DateTime],
                updated: Option[DateTime]) {}

object User {

  import play.api.Play.current

  private val dbName = "user"
  val db = ReactiveMongoPlugin.db.collection(dbName)

  val indexes = List(
    Index(List("username" -> Ascending), unique = true),
    Index(List("username" -> Ascending, "password" -> Ascending), unique = true)
  )

  def ensureIndexes() {
    indexes.foreach {
      index =>
        db.indexesManager.ensure(index).onComplete {
          case result =>
            Logger.info(s"Checked index $index for [$dbName], result is $result")
        }
    }
  }


  /** Generates a new ID and adds it to your JSON using Json extended notation for BSON */
  val generateId: Reads[JsObject] = (__ \ '_id \ '$oid).json.put(JsString(BSONObjectID.generate.stringify))

  /** Writes an ID in Json Extended Notation */
  val toObjectId: OWrites[String] = OWrites[String] {
    s => Json.obj("_id" -> Json.obj("$oid" -> s))
  }

  implicit object UserBSONReader extends BSONReader[User] {
    def fromBSON(document: BSONDocument): User = {
      val doc = document.toTraversable
      User(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONString]("username").get.value,
        doc.getAs[BSONString]("password").get.value,
        doc.getAs[BSONString]("role").get.value,
        doc.getAs[BSONString]("firstName").get.value,
        doc.getAs[BSONString]("lastName").get.value,
        doc.getAs[BSONDateTime]("created").map(dt => new DateTime(dt.value)),
        doc.getAs[BSONDateTime]("updated").map(dt => new DateTime(dt.value)))
    }
  }

  implicit object UserBSONWriter extends BSONWriter[User] {
    def toBSON(user: User) = {
      BSONDocument(
        "_id" -> user.id.getOrElse(BSONObjectID.generate),
        "username" -> BSONString(user.username),
        "password" -> BSONString(user.password),
        "role" -> BSONString(user.password),
        "firstName" -> BSONString(user.firstName),
        "lastName" -> BSONString(user.lastName),
        "created" -> user.created.map(date => BSONDateTime(date.getMillis)),
        "updated" -> user.updated.map(date => BSONDateTime(date.getMillis))
      )
    }
  }

  def byId(id: String) = {
    val q: QueryBuilder = QueryBuilder().query(toObjectId.writes(id))
    User.db.find[User](q).headOption
  }

  def byUsername(username: String) = {
    val q: QueryBuilder = QueryBuilder().query(BSONDocument("username" -> new BSONString(username)))
    User.db.find[User](q).headOption
  }

  def all() = {
    val q: QueryBuilder = QueryBuilder().query(BSONDocument())
    User.db.find[User](q).toList()
  }

  def rawFetch(username: String) = {
    implicit val writer = JsObjectWriter
    implicit val reader = JsObjectReader
    val projection = BSONDocument(
      "_id" -> BSONInteger(1),
      "username" -> BSONInteger(1),
      "password" -> BSONInteger(1),
      "firstName" -> BSONInteger(1),
      "created" -> BSONInteger(1)
    )
    val q: QueryBuilder = QueryBuilder().query(BSONDocument("username" -> new BSONString(username)))
      .projection(projection)
    User.db.find[JsObject](q).headOption
  }

  def rawAll() = {
    implicit val writer = JsObjectWriter
    implicit val reader = JsObjectReader
    val q: QueryBuilder = QueryBuilder().query(BSONDocument())
    User.db.find[JsObject](q).toList()
  }
}
