package models

import be.objectify.deadbolt.core.models._


import reactivemongo.bson._
import reactivemongo.bson.handlers._
import reactivemongo.bson.handlers.DefaultBSONHandlers._

import play.modules.reactivemongo._
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json._
import play.libs.Scala
import reactivemongo.api.QueryBuilder

import reactivemongo.bson.BSONString

/**
 * @author leodagdag
 */
case class Auth(id: Option[BSONObjectID],
                username: String,
                password: String,
                role: String) extends Subject {
  def getRoles: java.util.List[SecurityRole] = Scala.asJava(List(SecurityRole(role)))

  def getPermissions: java.util.List[_ <: Permission] = ???

  def getIdentifier: String = username
}

object Auth {

  import play.api.Play.current

  private val dbName = "user"

  private val db = ReactiveMongoPlugin.db.collection(dbName)

  implicit object AuthBSONReader extends BSONReader[Auth] {
    def fromBSON(document: BSONDocument): Auth = {
      val doc = document.toTraversable
      Auth(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONString]("username").get.value,
        doc.getAs[BSONString]("password").get.value,
        doc.getAs[BSONString]("role").get.value
      )
    }
  }

  implicit object AuthBSONWriter extends BSONWriter[Auth] {
    def toBSON(auth: Auth) = {
      BSONDocument(
        "_id" -> auth.id.getOrElse(BSONObjectID.generate),
        "username" -> BSONString(auth.username),
        "password" -> BSONString(auth.password),
        "role" -> BSONString(auth.password)
      )
    }
  }

  def checkAuthentication(auth: JsObject) = {
    import play.modules.reactivemongo.PlayBsonImplicits._
    val projection = BSONDocument("_id" -> BSONInteger(1), "username" -> BSONInteger(1), "password" -> BSONInteger(1), "role" -> BSONInteger(1))
    val q = QueryBuilder()
      .query(auth)
      .projection(projection)
    db.find[Auth](q).headOption
  }

  def asSubject(username: String) = {
    val q: QueryBuilder = QueryBuilder().query(BSONDocument("username" -> new BSONString(username)))
    User.db.find[Subject](q).headOption
  }
}
