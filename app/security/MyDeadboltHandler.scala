package security

import be.objectify.deadbolt.core.models.Subject
import be.objectify.deadbolt.scala.{DeadboltHandler, DynamicResourceHandler}
import concurrent.Await
import concurrent.duration.FiniteDuration
import controllers.routes
import java.util.concurrent.TimeUnit
import play.api.http.{MediaRange, MimeTypes}
import play.api.mvc.{Security, Results, Result, Request}


/**
 * @author leodagdag
 */
class MyDeadboltHandler(dynamicResourceHandler: Option[DynamicResourceHandler] = None) extends DeadboltHandler {

	def beforeAuthCheck[A](request: Request[A]) = None

	override def getSubject[A](request: Request[A]): Option[Subject] = {
		request.session.get("username")
			.map(username => Await.result(Auth.asSubject(username), FiniteDuration(5, TimeUnit.SECONDS)))
			.getOrElse(None)
	}

	def getDynamicResourceHandler[A](request: Request[A]): Option[DynamicResourceHandler] = ???

	def onAccessFailure[A](request: Request[A]): Result = {
		request.acceptedTypes
			.find(mr => mr == MediaRange(MimeTypes.JAVASCRIPT) || mr == MediaRange(MimeTypes.JSON))
			.map(some => Results.Unauthorized)
			.getOrElse(
			Results.Redirect(routes.Authentication.login())
				.flashing(
				request.getQueryString("redirect")
					.map(redirect => ("redirect" -> redirect))
					.getOrElse(("" -> ""))
			))
			.withSession(request.session - Security.username)
	}
}
