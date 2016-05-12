package util

import models.Tables._
import play.api.libs.json._
import play.api.mvc._

object JsonUtil {
  implicit val categoryFormat = Json.format[CategoryRow]
  implicit val colorFormat = Json.format[ColorRow]
  implicit val productFormat = Json.format[ProductRow]
  implicit val productDetailFormat = Json.format[ProductDetailRow]
  implicit val productImageFormat = Json.format[ProductImageRow]
  implicit val productCategoryFormat = Json.format[ProductCategoryRow]
  implicit val productDetailsFormat = Json.format[ProductDetails]
  implicit val productResultFormat = Json.format[ProductResult]

  def getJsValue(rs: Request[Any]): Option[JsValue] = {
    rs.body match {
      case x: AnyContentAsJson => x.asJson
      case x: AnyContentAsRaw  => x.asJson
      case x: AnyContentAsText => x.asJson
      case _                   => None
    }
  }

  def parseBodyToCategory(rs: Request[Any]): Option[CategoryRow] = {
    val json = getJsValue(rs)
    json match {
      case Some(x) => x.asOpt[CategoryRow]
    }
  }

  def parseBodyToProduct(rs: Request[Any]): Option[ProductRow] = {
    val json = getJsValue(rs)
    json match {
      case Some(x) => x.asOpt[ProductRow]
    }
  }

  def parseBodyToProductDetail(rs: Request[Any]): Option[ProductDetailRow] = {
    val json = getJsValue(rs)
    json match {
      case Some(x) => x.asOpt[ProductDetailRow]
    }
  }

  def parseBodyToColor(rs: Request[Any]): Option[ColorRow] = {
    val json = getJsValue(rs)
    json match {
      case Some(x) => x.asOpt[ColorRow]
    }
  }

  def parseBodyToProductImage(rs: Request[Any]): Option[ProductImageRow] = {
    val json = getJsValue(rs)
    json match {
      case Some(x) => x.asOpt[ProductImageRow]
    }
  }

  def parseBodyToProductCategory(rs: Request[Any]): Option[ProductCategoryRow] = {
    val json = getJsValue(rs)
    json match {
      case Some(x) => x.asOpt[ProductCategoryRow]
    }
  }

}