package models

import org.joda.time.DateTime
import be.objectify.deadbolt.core.models._

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers._
import reactivemongo.bson.handlers.DefaultBSONHandlers._

import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json._
import play.libs.Scala

/**
 * @author leodagdag
 */
case class User(id: Option[BSONObjectID],
                username: String,
                password: String,
                role: String,
                firstName: String,
                lastName: String,
                creationDate: Option[DateTime],
                updateDate: Option[DateTime]) extends Subject {
  def getRoles: java.util.List[SecurityRole] = Scala.asJava(List(SecurityRole(role)))

  def getPermissions: java.util.List[_ <: Permission] = ???

  def getIdentifier: String = username
}

object User {

  import play.api.Play.current

  lazy val db: DefaultCollection = ReactiveMongoPlugin.db.collection("user")


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
        doc.getAs[BSONDateTime]("creationDate").map(dt => new DateTime(dt.value)),
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
        "creationDate" -> user.creationDate.map(date => BSONDateTime(date.getMillis)),
        "updateDate" -> user.updateDate.map(date => BSONDateTime(date.getMillis)))
    }
  }

  def byUsername(username: String) = {
    val q: QueryBuilder = QueryBuilder().query(BSONDocument("username" -> new BSONString(username)))
    User.db.find[User](q).headOption
  }

  def asSubject(username: String) = {
    val q: QueryBuilder = QueryBuilder().query(BSONDocument("username" -> new BSONString(username)))
    User.db.find[Subject](q).headOption
  }

  def checkAuthentication(auth: JsObject) = {
    val q = QueryBuilder().query(auth)
    User.db.find[User](q).headOption
  }


}
