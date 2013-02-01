package security


import be.objectify.deadbolt.core.models.{Permission, Subject}
import concurrent.Future

import play.api.Play.current
import play.libs.Scala
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.QueryBuilder
import reactivemongo.bson.handlers.DefaultBSONHandlers.{DefaultBSONDocumentWriter, DefaultBSONReaderHandler}
import reactivemongo.bson.handlers.{BSONWriter, BSONReader}
import reactivemongo.bson.{BSONInteger, BSONString, BSONDocument, BSONObjectID}
import scala.concurrent.ExecutionContext.Implicits.global


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

	def checkAuthentication(auth: (String, String)): Future[Option[Auth]] = {
		val crit = BSONDocument("username" -> BSONString(auth._1), "password" -> BSONString(auth._2))
		val projection = BSONDocument("_id" -> BSONInteger(1), "username" -> BSONInteger(1), "password" -> BSONInteger(1), "role" -> BSONInteger(1))
		val q = QueryBuilder()
			.query(crit)
			.projection(projection)
		db.find[Auth](q).headOption
	}

	def asSubject(username: String) = {
		val q: QueryBuilder = QueryBuilder().query(BSONDocument("username" -> new BSONString(username)))
		db.find[Subject](q).headOption
	}
}
