package security

import be.objectify.deadbolt.scala.{DeadboltHandler, DynamicResourceHandler}
import be.objectify.deadbolt.core.models.Subject
import concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import concurrent.Await
import play.api.mvc._
import controllers.routes
import play.api.http.{MediaRange, MimeTypes}
import scala.Some
import play.api.mvc.Results.EmptyContent


/**
 * @author leodagdag
 */
class MyDeadboltHandler(dynamicResourceHandler: Option[DynamicResourceHandler] = None) extends DeadboltHandler {
	def beforeAuthCheck[A](request: Request[A]) = None

	override def getSubject[A](request: Request[A]): Option[Subject] = {
		request.session.get("username") match {
			case Some(username) => Await.result(Auth.asSubject(username), FiniteDuration(5, TimeUnit.SECONDS))
			case None => None
		}
	}

	def getDynamicResourceHandler[A](request: Request[A]): Option[DynamicResourceHandler] = ???

	def onAccessFailure[A](request: Request[A]): Result = {

		request.acceptedTypes
			.find(mr => mr == MediaRange(MimeTypes.JAVASCRIPT) || mr == MediaRange(MimeTypes.JSON))
			.map(some => Results.Unauthorized)
			.getOrElse(
			Results.Redirect(routes.Authentication.login())
				.flashing{
				println("request.getQueryString(redirect) = " + request.getQueryString("redirect"))
				request.getQueryString("redirect") match {
					case Some(redirect) => {
						println("redirect = " + redirect)
						"redirect" -> redirect
					}
					case None => ("" -> "")
				}
			})
			.withSession(request.session - Security.username)


	}
}
