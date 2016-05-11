package responses

import play.api.libs.json.Json

object DefaultResponses {
  case class DefaultResponse(status: Int, message: String)

  implicit val jsonFormat = Json.format[DefaultResponse]
}