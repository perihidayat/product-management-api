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

class ProductController extends Controller {

  def loadJoinedProduct() = {
    for { ((p, pd), c) <- products join productDetails on (_.id === _.productId) join colors on (_._2.colorId === _.id) } yield (p, pd, c)
  }
  
  def groupProductDetail(qResult: Seq[(ProductRow, ProductDetailRow, ColorRow)]) = {
    qResult.groupBy(_._1).mapValues(_.map(x => ProductDetails(x._2.id, x._3, x._2.size, x._2.stock, x._2.price)))
  }

  def loadProductImages(productIds: Seq[Int]) = {
	  val join = for {
		  p <- products
		  pi <- productImages if p.id === pi.productId if p.id inSet productIds
	  } yield (p.id, pi)
	  val qResult = Await.result(db.run(join.result), Duration.Inf)
	  qResult.groupBy(_._1).mapValues(_.map(_._2))
  }

  def parseToProductResult(details: Map[ProductRow, Seq[ProductDetails]], images: Map[Int, Seq[ProductImageRow]]) : Iterable[ProductResult] = {
    details.map(x => ProductResult(x._1.id, x._1.name, x._1.desc, x._2, images.get(x._1.id).getOrElse(Seq[ProductImageRow]())))
  }

  def processResult(qResult: Seq[(ProductRow, ProductDetailRow, ColorRow)]) : Iterable[ProductResult] = {
    val groupedDetails = groupProductDetail(qResult)
    val productIds = groupedDetails.map(_._1.id)
    val productImages = loadProductImages(productIds.toSeq)
    parseToProductResult(groupedDetails, productImages)
  }
  
  def index = Authenticated(
    rs => {
      val qResult = Await.result(db.run(loadJoinedProduct().result), Duration.Inf)
      Ok(toJson(Json.obj("products" -> processResult(qResult))))
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
      Ok(toJson(Json.obj("products" -> processResult(qResult))))
    })

  def insertOrUpdate(): Action[AnyContent] = Authenticated(
    rs => {
      parseBodyToProduct(rs) match {
        case Some(x) => {
          val result = Await.result(db.run(products.insertOrUpdate(x)), Duration.Inf)
          Ok(Json.toJson(DefaultResponse(Ok.header.status, s"$result rows affected.")))
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