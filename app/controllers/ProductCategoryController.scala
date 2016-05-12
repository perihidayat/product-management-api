package controllers

import util.JsonUtil._
import models.Tables._
import models.Tables.profile.api._
import controllers.AuthenticationController._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.mvc._
import responses.DefaultResponses._
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class ProductCategoryController extends Controller {

  def index = Authenticated(
    rs => {
      val result = Await.result(db.run(productCategories.result), Duration.Inf)
      Ok(toJson(Json.obj("productCategorys" -> result)))
    })

  def insertOrUpdate(): Action[AnyContent] = Authenticated(
    rs => {
      parseBodyToProductCategory(rs) match {
        case Some(x) => {
          val result = Await.result(db.run(productCategories.insertOrUpdate(x)), Duration.Inf)
          Ok(Json.toJson(DefaultResponse(Ok.header.status, s"$result rows affected.")))
        }
        case None => BadRequest(Json.toJson(DefaultResponse(BadRequest.header.status, "Invalid body")))
      }
    })

  def delete(id: Option[Int]): Action[AnyContent] = Authenticated(
    rs => {
      val result = Await.result(db.run(productCategories.filter { _.id === id }.delete), Duration.Inf)
      Ok(Json.toJson(DefaultResponse(Ok.header.status, s"$result rows deleted.")))
    })

}