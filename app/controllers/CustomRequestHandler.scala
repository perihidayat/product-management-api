package controllers

import play.api.http.DefaultHttpRequestHandler
import play.api.mvc.RequestHeader
import play.api.mvc.Action
import play.api.mvc.Result
import play.api.http._
import javax.inject.Inject
import router.Routes
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class CustomRequestHandler @Inject() (errorHandler: HttpErrorHandler,
                                      configuration: HttpConfiguration, filters: HttpFilters, router: Routes) extends DefaultHttpRequestHandler(router, errorHandler, configuration, filters) {

  def AsJson[A](action: Action[A]): Action[A] = Action(action.parser) { request =>
    action(request) match {
      case s: Result => s.withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
      case result    => Await.result(result, Duration.Inf).withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
    }
  }

  override def routeRequest(request: RequestHeader) = {
    super.routeRequest(request).map {
      case action: Action[_] => AsJson(action)
      case other             => other
    }
  }

}