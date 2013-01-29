package controllers

import security.{MyDeadboltHandler, MyUserlessDeadboltHandler}
import be.objectify.deadbolt.scala.DeadboltActions

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import models.User
import play.modules.reactivemongo.MongoController
import play.api.Logger

/**
 * @author leodagdag
 */
object Application extends Controller with DeadboltActions with MongoController {

  def index = SubjectPresent(new MyUserlessDeadboltHandler) {
    Action {
      Ok(Json.obj("result" -> "index : Access OK!"))
    }
  }

  def backoffice = Restrictions(List(Array("admin")), new MyDeadboltHandler) {
    Action {
      Ok(Json.obj("result" -> "backoffice : Access OK!"))
    }
  }

  def account = Restrictions(List(Array("user")), new MyDeadboltHandler) {
    Action {
      Ok(Json.obj("result" -> "account : Access OK!"))
    }
  }

  def secured = Restrictions(List(Array("user"),Array("admin")), new MyDeadboltHandler) {
    Action {
      Ok(Json.obj("result" -> "account : Access OK!"))
    }
  }

  /** Full Person validator */
  val validateAuthentication: Reads[JsObject] = (
    (__ \ 'username).json.pickBranch and
      (__ \ 'password).json.pickBranch
    ).reduce

  val toAuthQry: OWrites[JsObject] = OWrites[JsObject] {
    jsobj => Json.obj(Security.username -> jsobj \ "username", "password" -> jsobj \ "password")
  }

  def authenticate = Action(parse.json) {
    request =>
      request.body.transform(validateAuthentication).map {
        jsobj =>
          Async {
            User.checkAuthentication(toAuthQry.writes(jsobj)).map {
              check =>
                check match {
                  case Some(c) => Ok.withSession(request.session.+((Security.username -> (jsobj \ "username").as[String])))
                  case None => Unauthorized(Json.obj("result" -> "Access KO!")).withSession(request.session - Security.username)
                }
            }.recover {
              case e =>
                Logger.error("authenticate",e)
                InternalServerError(JsString("exception %s".format(e.getMessage)))
            }

          }
      }.recoverTotal {
        err =>
          InternalServerError(JsError.toFlatJson(err))
      }
  }

  def logout = Action {
    request =>
      Ok.withSession(request.session - Security.username)
  }

}