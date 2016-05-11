package controllers

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

class ProductController extends Controller {

  def loadProductImages() = {
    val q = for { (p, pi) <- products join productImages on (_.id === _.productId) } yield (p.id, pi)
    val qResult = Await.result(db.run(q.result), Duration.Inf)
    qResult.groupBy(_._1).mapValues(_.map(_._2))
  }

  def loadJoinedProduct() = {
    for { ((p, pd), c) <- products join productDetails on (_.id === _.productId) join colors on (_._2.colorId === _.id) } yield (p, pd, c)
  }

  def groupJoinedProduct(qResult: Seq[(ProductRow, ProductDetailRow, ColorRow)]) = {
    qResult.groupBy(_._1).mapValues(_.map(x => ProductDetails(x._2.id, x._3, x._2.size, x._2.stock, x._2.price)))
  }

  def parseToProductResult(grouped: Map[ProductRow, Seq[ProductDetails]]) = {
    grouped.map(x => ProductResult(x._1.id, x._1.name, x._1.desc, x._2))
  }

  def index = Authenticated(
    rs => {
      val qResult = Await.result(db.run(loadJoinedProduct().result), Duration.Inf)
      val grouped = groupJoinedProduct(qResult)
      val result = parseToProductResult(grouped)
      Ok(toJson(Json.obj("products" -> result)))
    })

  def find(id: Option[Int], size: Option[String], color: Option[String], minPrice: Option[Double], maxPrice: Option[Double]): Action[AnyContent] = Authenticated(
    rs => {
      val join = loadJoinedProduct()
      val filtered = join.filter { x =>
        ((x._1.id === id || id.isEmpty)
          && (x._2.size === size || size.isEmpty)
          && (x._3.name like (s"%${color.getOrElse("")}%"))
          && (x._2.price >= minPrice || minPrice.isEmpty)
          && (x._2.price <= maxPrice || maxPrice.isEmpty))
      }
      val qResult = Await.result(db.run(filtered.result), Duration.Inf)
      val grouped = groupJoinedProduct(qResult)
      val result = parseToProductResult(grouped)
      Ok(toJson(Json.obj("products" -> result)))
    })

  def parseToObject(rs: Request[Any]): Option[ProductRow] = {
    def parseIfNotEmpty(json: Option[JsValue]) = {
      json match {
        case Some(x) => x.asOpt[ProductRow]
        case _       => None
      }
    }
    rs.body match {
      case x: AnyContentAsJson => parseIfNotEmpty(x.asJson)
      case x: AnyContentAsRaw  => parseIfNotEmpty(x.asJson)
      case x: AnyContentAsText => parseIfNotEmpty(x.asJson)
      case _                   => None
    }
  }

  def insert(): Action[AnyContent] = Authenticated(
    rs => {
      parseToObject(rs) match {
        case Some(x) => {
          val result = Await.result(db.run(products.filter { row => row.id === x.id }.result), Duration.Inf)
          if (!result.isEmpty)
            BadRequest(Json.toJson(DefaultResponse(BadRequest.header.status, s"Item with id ${x.id} already exists. Use /update instead.")))
          else {
            db.run(products += x)
            Ok(toJson(x))
          }
        }
        case None => BadRequest(Json.toJson(DefaultResponse(BadRequest.header.status, "Invalid body")))
      }
    })

  def update(): Action[AnyContent] = Authenticated(
    rs => {
      parseToObject(rs) match {
        case Some(x) => {
          val result = Await.result(db.run(products.filter { row => row.id === x.id }.result), Duration.Inf)
          if (result.isEmpty)
            BadRequest(Json.toJson(DefaultResponse(BadRequest.header.status, s"Item with id ${x.id} not exists. Use /insert instead.")))
          else {
//            val q = for { c <- products if c.id === x.id } yield (c.name, c.parent)
//            db.run(q.update(x.name, x.parent))
            Ok(toJson(x))
          }
        }
        case None => BadRequest(Json.toJson(DefaultResponse(BadRequest.header.status, "Invalid body")))
      }
    })

  def delete(id: Option[Int]): Action[AnyContent] = Authenticated(
    rs => {
      val result = Await.result(db.run(products.filter { _.id === id }.delete), Duration.Inf)
      Ok(Json.toJson(DefaultResponse(Ok.header.status, s"$result rows deleted.")))
    })

}