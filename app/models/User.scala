package models

import org.joda.time.DateTime

import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType._
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers._
import reactivemongo.bson.handlers.DefaultBSONHandlers._

import play.api._

import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._
import scala.concurrent.ExecutionContext.Implicits.global

import concurrent.Future

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
        doc.getAs[BSONDateTime]("updateDate").map(dt => new DateTime(dt.value)))
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

  object AuthBSONWriter extends BSONWriter[User] {
    def toBSON(user: User) = {

      BSONDocument(
        "_id" -> user.id.getOrElse(BSONObjectID.generate),
        "username" -> BSONString(user.username),
        "password" -> BSONString(user.password),
        "role" -> BSONString(user.password)
      )
    }
  }

  def byId(id: String): Future[Option[User]] = {
    val q: QueryBuilder = QueryBuilder().query(toObjectId.writes(id))
    User.db.find[User](q).headOption
  }

  def byUsername(username: String): Future[Option[User]] = {
    val q: QueryBuilder = QueryBuilder().query(BSONDocument("username" -> new BSONString(username)))
    User.db.find[User](q).headOption
  }


  def all() = {
    val q: QueryBuilder = QueryBuilder().query(BSONDocument())
    User.db.find[User](q).toList()
  }
}
