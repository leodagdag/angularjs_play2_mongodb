package controllers

import security.{Auth, MyDeadboltHandler}
import be.objectify.deadbolt.scala.DeadboltActions

//import play.api.libs.functional.syntax._
//import play.api.libs.json.Reads._

import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.api.Logger

import play.api.data.Forms._
import play.api.data.Form

//import play.api.mvc.Results.EmptyContent

/**
 * @author leodagdag
 */
object Application extends Controller with DeadboltActions with MongoController {

	def index = SubjectPresent(new MyDeadboltHandler) {
		Action {
			implicit request =>
				Ok(views.html.index(new MyDeadboltHandler()))
		}
	}

	def user = Restrictions(List(Array("user")), new MyDeadboltHandler) {
		Action {
			Ok(Json.obj("result" -> "User access OK!"))
		}
	}

	def all = Restrictions(List(Array("user"), Array("admin")), new MyDeadboltHandler) {
		Action {
			Ok(Json.obj("result" -> "Uaer and Admin access OK!"))
		}
	}

	def admin = Restrictions(List(Array("admin")), new MyDeadboltHandler) {
		Action {
			Ok(Json.obj("result" -> "Admin access OK!"))
		}
	}
	/*
	def login = Action {
		implicit request =>
			Ok(views.html.login())
	}

	val authenticateForm = Form(tuple(
		"username" -> nonEmptyText,
		"password" -> nonEmptyText
		"redirect" -> optional(text)
	)
	)

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
										  login._3.map{
											  next =>
												  println(routes.Application.index().absoluteURL())
												  println(next)
												  Results.Redirect(routes.Application.index().absoluteURL() + "/" + next)
										  }
											  .getOrElse(Results.Redirect(routes.Application.index()))
											  .withSession(request.session + (Security.username -> login._1))
									}
									case None => Ok(views.html.login()).withSession(request.session - Security.username)
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
			Ok.withSession(request.session - Security.username)
	}
	*/
	/*
		/** Full Person validator */
		val validateAuthentication: Reads[JsObject] = (
			(__ \ 'username).json.pickBranch and
				(__ \ 'password).json.pickBranch
			).reduce

		val toAuthQry: OWrites[JsObject] = OWrites[JsObject] {
			jsobj => Json.obj(
				Security.username -> jsobj \ "username",
				"password" -> jsobj \ "password"
			)
		}

		def authenticate = Action(parse.json) {
			request =>
				request.body.transform(validateAuthentication).map {
					jsobj =>
						Async {
							Auth.checkAuthentication(toAuthQry.writes(jsobj)).map {
								check =>
									check match {
										case Some(c) => Ok.withSession(request.session.+((Security.username -> (jsobj \ "username").as[String])))
										case None => Unauthorized(JsString("Access KO !")).withSession(request.session - Security.username)
									}
							}.recover {
								case e =>
									Logger.error("authenticate", e)
									InternalServerError(JsString("exception %s".format(e.getMessage)))
							}

						}
				}.recoverTotal {
					err =>
						InternalServerError(JsError.toFlatJson(err))
				}
		}
	*/
}