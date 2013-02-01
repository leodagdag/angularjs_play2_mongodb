package security

import be.objectify.deadbolt.core.models.Subject
import be.objectify.deadbolt.scala.DynamicResourceHandler
import play.api.mvc.Request

/**
 * @author leodagdag
 */
class MyUserlessDeadboltHandler(dynamicResourceHandler: DynamicResourceHandler = null) extends MyDeadboltHandler {

	override def getSubject[A](request: Request[A]): Option[Subject] = super.getSubject(request)

}
