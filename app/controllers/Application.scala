package controllers

import be.objectify.deadbolt.scala.DeadboltActions
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import security.{SecurityRole, MyDeadboltHandler}

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

	def user = Restrictions(List(Array(SecurityRole.user)), new MyDeadboltHandler) {
		Action {
			Ok(Json.obj("result" -> "User access OK!"))
		}
	}

	def all = Restrictions(List(Array(SecurityRole.user), Array(SecurityRole.admin)), new MyDeadboltHandler) {
		Action {
			Ok(Json.obj("result" -> "User and Admin access OK!"))
		}
	}

	def admin = Restrictions(List(Array(SecurityRole.admin)), new MyDeadboltHandler) {
		Action {
			Ok(Json.obj("result" -> "Admin access OK!"))
		}
	}
}