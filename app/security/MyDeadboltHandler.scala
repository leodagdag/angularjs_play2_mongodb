package security

import be.objectify.deadbolt.scala.{DeadboltHandler, DynamicResourceHandler}
import play.api.mvc.{Results, Result, Request}
import be.objectify.deadbolt.core.models.Subject
import models.Auth
import concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit
import concurrent.Await

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
    Results.Unauthorized
  }
}
