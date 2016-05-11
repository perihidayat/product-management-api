package controllers

import org.joda.time.DateTime

import play.api.http.HeaderNames
import play.api.mvc.Action
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.Results._
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.JsValue
import java.security.MessageDigest
import org.apache.commons.codec.binary.Base64
import com.typesafe.config.ConfigFactory
import play.api.mvc._
import play.api.libs.json.Json
import responses.DefaultResponses._

object AuthenticationController {

  val formatter = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z")
  val appSecret = ConfigFactory.load().getString("application.secret")

  def Authenticated(f: (Request[Any]) => Result) = {
    Action {
      request =>
        {
          /*
          val headers = request.headers
          if (!isValidTimestamp(headers.get(HeaderNames.DATE))) {
            RequestTimeout(Json.toJson(DefaultResponse(RequestTimeout.header.status, "RequestTimeout")))
          } else {
            val unauthorizedResult = Unauthorized(Json.toJson(DefaultResponse(Unauthorized.header.status, "Unauthorized")))
            val auth = headers.get(HeaderNames.AUTHORIZATION)
            auth match {
              case Some(x) => if (x == calculateMd5(request)) f(request) else unauthorizedResult
              case None    => unauthorizedResult
            }
          }
          */
        	f(request)
        }
    }
  }

  private def isValidTimestamp(dateHeader: Option[String]): Boolean = {
    dateHeader match {
      case Some(x) => {
        DateTime.now().minusMinutes(1440).isBefore(DateTime.parse(x, formatter))
      }
      case None => false
    }
  }

  private def calculateMd5(request: Request[Any]): String = {
    val date = request.headers.get(HeaderNames.DATE).get
    val params = request.queryString.map { case (k,v) => k -> v.mkString }
    val paramArgs = params.foldLeft("") { (s: String, pair: (String, String)) => s + pair._2 }
    val stringBody = request.body match {
      case AnyContentAsEmpty   => ""
      case x: AnyContentAsJson => Json.toJson(x.asJson)
      case x: AnyContentAsRaw  => Json.toJson(x.asJson)
    }
    val toSign = appSecret + date + paramArgs + stringBody
    val digest = MessageDigest.getInstance("MD5").digest(toSign.getBytes)
    digest.map("%02x".format(_)).mkString
  }
}
