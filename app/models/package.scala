import play.api.libs.json._
import play.api.libs.json.JsObject
import reactivemongo.bson.BSONObjectID

/**
 * @author leodagdag
 */
package object models {
  /** Generates a new ID and adds it to your JSON using Json extended notation for BSON */
  val generateId: Reads[JsObject] = (__ \ '_id \ '$oid).json.put(JsString(BSONObjectID.generate.stringify))
}
