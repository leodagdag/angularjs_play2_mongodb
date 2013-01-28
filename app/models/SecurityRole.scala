package models

import be.objectify.deadbolt.core.models.Role

/**
 * @author leodagdag
 */
case class SecurityRole(code: String) extends Role {
  override def getName: String = code
}

