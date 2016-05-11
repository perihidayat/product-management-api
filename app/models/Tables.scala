package models

import slick.jdbc.JdbcBackend.DatabaseFactoryDef
import play.api.libs.json.Json
import play.api.libs.json.Writes

object Tables extends {
  val profile = slick.driver.SQLiteDriver
} with DatabaseFactoryDef {

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{ GetResult => GR }

  implicit val db = forURL("jdbc:sqlite:db/salestock.db", driver = "org.sqlite.JDBC")

  case class CategoryRow(id: Int, name: String, parent: Option[Int])
  /** GetResult implicit for fetching CategoryRow objects using plain SQL queries */
  implicit def GetResultCategoryRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[Int]]): GR[CategoryRow] = GR {
    prs =>
      import prs._
      CategoryRow.tupled((<<[Int], <<[String], <<?[Int]))
  }
  /** Table description of table mst_category. Objects of this class serve as prototypes for rows in queries. */
  class Category(_tableTag: Tag) extends Table[CategoryRow](_tableTag, "mst_category") {
    def * = (id, name, parent) <> (CategoryRow.tupled, CategoryRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), parent).shaped.<>({ r => import r._; _1.map(_ => CategoryRow.tupled((_1.get, _2.get, _3))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INTEGER), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    /** Database column name SqlType(STRING) */
    val name: Rep[String] = column[String]("name")
    /** Database column parent SqlType(INTEGER) */
    val parent: Rep[Option[Int]] = column[Option[Int]]("parent")
  }
  /** Collection-like TableQuery object for table Category */
  lazy val categories = new TableQuery(tag => new Category(tag))
  implicit val categoryFormat = Json.format[CategoryRow]

  case class CategoryHierarchy(id: Int, name: String, childs: Option[Seq[CategoryRow]])
  
  /**
   * Entity class storing rows of table Color
   *  @param id Database column id SqlType(INTEGER), PrimaryKey
   *  @param name Database column name SqlType(STRING)
   *  @param hex Database column hex SqlType(STRING)
   */
  case class ColorRow(id: Int, name: String, hex: String)
  /** GetResult implicit for fetching ColorRow objects using plain SQL queries */
  implicit def GetResultColorRow(implicit e0: GR[Int], e1: GR[String]): GR[ColorRow] = GR {
    prs =>
      import prs._
      ColorRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table mst_color. Objects of this class serve as prototypes for rows in queries. */
  class Color(_tableTag: Tag) extends Table[ColorRow](_tableTag, "mst_color") {
    def * = (id, name, hex) <> (ColorRow.tupled, ColorRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(hex)).shaped.<>({ r => import r._; _1.map(_ => ColorRow.tupled((_1.get, _2.get, _3.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INTEGER), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(STRING) */
    val name: Rep[String] = column[String]("name")
    /** Database column hex SqlType(STRING) */
    val hex: Rep[String] = column[String]("hex")
  }
  /** Collection-like TableQuery object for table Color */
  lazy val colors = new TableQuery(tag => new Color(tag))
  implicit val colorFormat = Json.format[ColorRow]

  /**
   * Entity class storing rows of table Product
   *  @param id Database column id SqlType(INTEGER), PrimaryKey
   *  @param name Database column name SqlType(STRING)
   *  @param desc Database column desc SqlType(STRING)
   *  @param price Database column price SqlType(BIGINT)
   */
  case class ProductRow(id: Int, name: String, desc: String)
  /** GetResult implicit for fetching ProductRow objects using plain SQL queries */
  implicit def GetResultProductRow(implicit e0: GR[Int], e1: GR[String]): GR[ProductRow] = GR {
    prs =>
      import prs._
      ProductRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table mst_product. Objects of this class serve as prototypes for rows in queries. */
  class Product(_tableTag: Tag) extends Table[ProductRow](_tableTag, "mst_product") {
    def * = (id, name, desc) <> (ProductRow.tupled, ProductRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(desc)).shaped.<>({ r => import r._; _1.map(_ => ProductRow.tupled((_1.get, _2.get, _3.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INTEGER), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(STRING) */
    val name: Rep[String] = column[String]("name")
    /** Database column desc SqlType(STRING) */
    val desc: Rep[String] = column[String]("desc")
  }
  /** Collection-like TableQuery object for table Product */
  lazy val products = new TableQuery(tag => new Product(tag))
  implicit val productFormat = Json.format[ProductRow]

  /**
   * Entity class storing rows of table ProductDetail
   *  @param id Database column id SqlType(INTEGER), PrimaryKey
   *  @param productId Database column product_id SqlType(INTEGER)
   *  @param colorId Database column color_id SqlType(INTEGER)
   *  @param size Database column size SqlType(VARCHAR)
   *  @param stock Database column stock SqlType(INTEGER)
   */
  case class ProductDetailRow(id: Int, productId: Int, colorId: Int, size: String, stock: Int, price: Double)
  /** GetResult implicit for fetching ProductDetailRow objects using plain SQL queries */
  implicit def GetResultProductDetailRow(implicit e0: GR[Int], e1: GR[String]): GR[ProductDetailRow] = GR {
    prs =>
      import prs._
      ProductDetailRow.tupled((<<[Int], <<[Int], <<[Int], <<[String], <<[Int], <<[Double]))
  }
  /** Table description of table mst_product_detail. Objects of this class serve as prototypes for rows in queries. */
  class ProductDetail(_tableTag: Tag) extends Table[ProductDetailRow](_tableTag, "mst_product_detail") {
    def * = (id, productId, colorId, size, stock, price) <> (ProductDetailRow.tupled, ProductDetailRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(productId), Rep.Some(colorId), Rep.Some(size), Rep.Some(stock), Rep.Some(price)).shaped.<>({ r => import r._; _1.map(_ => ProductDetailRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INTEGER), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column product_id SqlType(INTEGER) */
    val productId: Rep[Int] = column[Int]("product_id")
    /** Database column color_id SqlType(INTEGER) */
    val colorId: Rep[Int] = column[Int]("color_id")
    /** Database column size SqlType(VARCHAR) */
    val size: Rep[String] = column[String]("size")
    /** Database column stock SqlType(INTEGER) */
    val stock: Rep[Int] = column[Int]("stock")
    /** Database column price SqlType(BIGINT) */
    val price: Rep[Double] = column[Double]("price")

    /** Foreign key referencing Color (database name mst_color_FK_1) */
    lazy val colorFk = foreignKey("mst_color_FK_1", colorId, colors)(r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
    /** Foreign key referencing Product (database name mst_product_FK_2) */
    lazy val productFk = foreignKey("mst_product_FK_2", productId, products)(r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table ProductDetail */
  lazy val productDetails = new TableQuery(tag => new ProductDetail(tag))
  implicit val productDetailFormat = Json.format[ProductDetailRow]

  case class ProductDetails(id: Int, color: ColorRow, size: String, stock: Int, price: Double)
  implicit val productDetailsFormat = Json.format[ProductDetails]
  
  /**
   * Entity class storing rows of table ProductImage
   *  @param id Database column id SqlType(INTEGER), PrimaryKey
   *  @param productId Database column product_id SqlType(INTEGER)
   *  @param image Database column image SqlType(STRING)
   */
  case class ProductImageRow(id: Int, productId: Int, image: String)
  /** GetResult implicit for fetching ProductImageRow objects using plain SQL queries */
  implicit def GetResultProductImageRow(implicit e0: GR[Int], e1: GR[String]): GR[ProductImageRow] = GR {
    prs =>
      import prs._
      ProductImageRow.tupled((<<[Int], <<[Int], <<[String]))
  }
  /** Table description of table mst_product_image. Objects of this class serve as prototypes for rows in queries. */
  class ProductImage(_tableTag: Tag) extends Table[ProductImageRow](_tableTag, "mst_product_image") {
    def * = (id, productId, image) <> (ProductImageRow.tupled, ProductImageRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(productId), Rep.Some(image)).shaped.<>({ r => import r._; _1.map(_ => ProductImageRow.tupled((_1.get, _2.get, _3.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INTEGER), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column product_id SqlType(INTEGER) */
    val productId: Rep[Int] = column[Int]("product_id")
    /** Database column image SqlType(STRING) */
    val image: Rep[String] = column[String]("image")

    /** Foreign key referencing Product (database name mst_product_FK_1) */
    lazy val productFk = foreignKey("mst_product_FK_1", productId, products)(r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table ProductImage */
  lazy val productImages = new TableQuery(tag => new ProductImage(tag))
  implicit val productImageFormat = Json.format[ProductImageRow]

  case class ProductResult(id: Int, name: String, desc: String, productDetails: Seq[ProductDetails], images: Seq[ProductImageRow])
  implicit val productResultFormat = Json.format[ProductResult]

  /**
   * Entity class storing rows of table ProductCategory
   *  @param id Database column id SqlType(INTEGER), PrimaryKey
   *  @param productId Database column product_id SqlType(INTEGER)
   *  @param categoryId Database column category_id SqlType(INTEGER)
   */
  case class ProductCategoryRow(id: Int, productId: Int, categoryId: Int)
  /** GetResult implicit for fetching ProductCategoryRow objects using plain SQL queries */
  implicit def GetResultProductCategoryRow(implicit e0: GR[Int]): GR[ProductCategoryRow] = GR {
    prs =>
      import prs._
      ProductCategoryRow.tupled((<<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table trx_product_category. Objects of this class serve as prototypes for rows in queries. */
  class ProductCategory(_tableTag: Tag) extends Table[ProductCategoryRow](_tableTag, "trx_product_category") {
    def * = (id, productId, categoryId) <> (ProductCategoryRow.tupled, ProductCategoryRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(productId), Rep.Some(categoryId)).shaped.<>({ r => import r._; _1.map(_ => ProductCategoryRow.tupled((_1.get, _2.get, _3.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INTEGER), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column product_id SqlType(INTEGER) */
    val productId: Rep[Int] = column[Int]("product_id")
    /** Database column category_id SqlType(INTEGER) */
    val categoryId: Rep[Int] = column[Int]("category_id")

    /** Foreign key referencing Category (database name mst_category_FK_1) */
    lazy val categoryFk = foreignKey("mst_category_FK_1", categoryId, categories)(r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
    /** Foreign key referencing Product (database name mst_product_FK_2) */
    lazy val productFk = foreignKey("mst_product_FK_2", productId, products)(r => r.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table ProductCategory */
  lazy val productCategories = new TableQuery(tag => new ProductCategory(tag))
}
