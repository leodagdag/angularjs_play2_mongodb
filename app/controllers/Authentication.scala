package controllers

import be.objectify.deadbolt.scala.DeadboltActions
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.optional
import play.api.data.Forms.text
import play.api.data.Forms.tuple
import play.api.libs.json.JsString
import play.api.mvc.{Action, Security, Results, Controller}
import play.modules.reactivemongo.MongoController
import scala.Some
import security.Auth


/**
 * @author leodagdag
 */
object Authentication extends Controller with DeadboltActions with MongoController {

	def login = Action {
		implicit request =>
			Ok(views.html.login())
	}

	val authenticateForm = Form(tuple(
		"username" -> nonEmptyText,
		"password" -> nonEmptyText,
		"redirect" -> optional(text)
	))

	def authenticate = Action {
		implicit request =>
			authenticateForm.bindFromRequest.fold(
				withErrors => BadRequest("KO"),
				login => {
					Async {
						Auth.checkAuthentication((login._1, login._2)).map {
							check =>
								check match {
									case Some(ok) => {
										login._3.map(next => Results.Redirect(routes.Application.index() + "#" + next))
											.getOrElse(Results.Redirect(routes.Application.index()))
											.withSession(request.session + (Security.username -> login._1))
									}
									case None => Results.Redirect(routes.Authentication.login())
										.withSession(request.session - Security.username)
										.flashing(("errormsg" -> "Bad login or password"))
								}
						}.recover {
							case e =>
								Logger.error("authenticate", e)
								InternalServerError(JsString("exception %s".format(e.getMessage)))
						}
					}
				}
			)

	}

	def logout = Action {
		request =>
			Results.Redirect(routes.Authentication.login()).withSession(request.session - Security.username)
	}
}
