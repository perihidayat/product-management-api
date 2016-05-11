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

class CategoryController extends Controller {

  def index = Authenticated(
    rs => {
      val result = Await.result(db.run(categories.result), Duration.Inf)
      Ok(toJson(Json.obj("categories" -> result)))
    })

  def find(id: Option[Int], name: Option[String], parent: Option[Int]): Action[AnyContent] = Authenticated(
    rs => {
      val filtered = categories.filter { x =>
        ((x.id === id || id.isEmpty)
          && (x.name like (s"%${name.getOrElse("")}%"))
          && (x.parent === parent || parent.isEmpty))
      }
      val result = Await.result(db.run(filtered.result), Duration.Inf)
      Ok(toJson(Json.obj("categories" -> result)))
    })

  def parseToObject(rs: Request[Any]): Option[CategoryRow] = {
    def parseIfNotEmpty(json: Option[JsValue]): Option[CategoryRow] = {
      json match {
        case Some(x) => x.asOpt[CategoryRow]
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
          val result = Await.result(db.run(categories.filter { row => row.id === x.id }.result), Duration.Inf)
          if (!result.isEmpty)
            BadRequest(Json.toJson(DefaultResponse(BadRequest.header.status, s"Item with id ${x.id} already exists. Use /update instead.")))
          else {
            db.run(categories += x)
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
          val result = Await.result(db.run(categories.filter { row => row.id === x.id }.result), Duration.Inf)
          if (result.isEmpty)
            BadRequest(Json.toJson(DefaultResponse(BadRequest.header.status, s"Item with id ${x.id} not exists. Use /insert instead.")))
          else {
            val q = for { c <- categories if c.id === x.id } yield (c.name, c.parent)
            db.run(q.update(x.name, x.parent))
            Ok(toJson(x))
          }
        }
        case None => BadRequest(Json.toJson(DefaultResponse(BadRequest.header.status, "Invalid body")))
      }
    })

  def delete(id: Option[Int]): Action[AnyContent] = Authenticated(
    rs => {
      val result = Await.result(db.run(categories.filter { _.id === id }.delete), Duration.Inf)
      Ok(Json.toJson(DefaultResponse(Ok.header.status, s"$result rows deleted.")))
    })

  def up(id: Option[Int]): Action[AnyContent] = Authenticated(
    rs => {
      Ok(toJson(Json.obj("hierarchy" -> loadTopDownCategories(id))))
    })

  def loadTopDownCategories(id: Option[Int]): Seq[CategoryRow] = {
    val cond = id match { case Some(x) => s"$x" case None => "id" }
    val sql = sql"""WITH RECURSIVE cnt(id) AS (
           SELECT id
           from mst_category 
           where id = #$cond
           UNION ALL
           SELECT cat.id
           FROM mst_category cat, cnt
           where cat.parent = cnt.id
       ) 
       select * from mst_category where id in cnt""".as[CategoryRow]
    val result = Await.result(db.run(sql), Duration.Inf)
    //    val grouped = result.groupBy { _.parent }
    //    val res = grouped.foldLeft(Map[Int, CategoryHierarchy]())((res, in) =>
    //      in._1 match {
    //        case None    => res ++ in._2.map { x => (x.id, CategoryHierarchy(x.id, x.name, Option(Seq[CategoryRow]()))) }
    //        case Some(x) => res.contains(x)
    //      })
    result.toSeq
  }

  def down(id: Option[Int]): Action[AnyContent] = Authenticated(
    rs => {
      Ok(toJson(Json.obj("hierarchy" -> loadBottomUpCategories(id))))
    })

  def loadBottomUpCategories(id: Option[Int]): Seq[CategoryRow] = {
    val cond = id match { case Some(x) => s"$x" case None => "id" }
    val sql = sql"""WITH RECURSIVE cnt(id) AS (
             SELECT id
             from mst_category
             where id = #$cond
             UNION ALL
             SELECT cat.parent 
             FROM mst_category cat, cnt
             where cat.id = cnt.id
       ) 
       select * from mst_category where id in cnt""".as[CategoryRow]
    val result = Await.result(db.run(sql), Duration.Inf)
    result.toSeq
  }

}