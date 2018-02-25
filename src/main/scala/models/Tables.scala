package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Services.schema ++ ServicesTags.schema ++ Tags.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Services
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(45,true)
   *  @param key Database column key SqlType(VARCHAR), Length(45,true) */
  case class ServicesRow(id: Int, name: String, key: String)
  /** GetResult implicit for fetching ServicesRow objects using plain SQL queries */
  implicit def GetResultServicesRow(implicit e0: GR[Int], e1: GR[String]): GR[ServicesRow] = GR{
    prs => import prs._
    ServicesRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table services. Objects of this class serve as prototypes for rows in queries. */
  class Services(_tableTag: Tag) extends profile.api.Table[ServicesRow](_tableTag, Some("jug"), "services") {
    def * = (id, name, key) <> (ServicesRow.tupled, ServicesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(key)).shaped.<>({r=>import r._; _1.map(_=> ServicesRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(45,true) */
    val name: Rep[String] = column[String]("name", O.Length(45,varying=true))
    /** Database column key SqlType(VARCHAR), Length(45,true) */
    val key: Rep[String] = column[String]("key", O.Length(45,varying=true))

    /** Uniqueness Index over (name) (database name name_UNIQUE) */
    val index1 = index("name_UNIQUE", name, unique=true)
  }
  /** Collection-like TableQuery object for table Services */
  lazy val Services = new TableQuery(tag => new Services(tag))

  /** Entity class storing rows of table ServicesTags
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param serviceId Database column service_id SqlType(INT)
   *  @param tagId Database column tag_id SqlType(INT) */
  case class ServicesTagsRow(id: Int, serviceId: Int, tagId: Int)
  /** GetResult implicit for fetching ServicesTagsRow objects using plain SQL queries */
  implicit def GetResultServicesTagsRow(implicit e0: GR[Int]): GR[ServicesTagsRow] = GR{
    prs => import prs._
    ServicesTagsRow.tupled((<<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table services_tags. Objects of this class serve as prototypes for rows in queries. */
  class ServicesTags(_tableTag: Tag) extends profile.api.Table[ServicesTagsRow](_tableTag, Some("jug"), "services_tags") {
    def * = (id, serviceId, tagId) <> (ServicesTagsRow.tupled, ServicesTagsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(serviceId), Rep.Some(tagId)).shaped.<>({r=>import r._; _1.map(_=> ServicesTagsRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column service_id SqlType(INT) */
    val serviceId: Rep[Int] = column[Int]("service_id")
    /** Database column tag_id SqlType(INT) */
    val tagId: Rep[Int] = column[Int]("tag_id")

    /** Foreign key referencing Services (database name service_id) */
    lazy val servicesFk = foreignKey("service_id", serviceId, Services)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Tags (database name tag_id) */
    lazy val tagsFk = foreignKey("tag_id", tagId, Tags)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table ServicesTags */
  lazy val ServicesTags = new TableQuery(tag => new ServicesTags(tag))

  /** Entity class storing rows of table Tags
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(45,true) */
  case class TagsRow(id: Int, name: String)
  /** GetResult implicit for fetching TagsRow objects using plain SQL queries */
  implicit def GetResultTagsRow(implicit e0: GR[Int], e1: GR[String]): GR[TagsRow] = GR{
    prs => import prs._
    TagsRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table tags. Objects of this class serve as prototypes for rows in queries. */
  class Tags(_tableTag: Tag) extends profile.api.Table[TagsRow](_tableTag, Some("jug"), "tags") {
    def * = (id, name) <> (TagsRow.tupled, TagsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name)).shaped.<>({r=>import r._; _1.map(_=> TagsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(45,true) */
    val name: Rep[String] = column[String]("name", O.Length(45,varying=true))

    /** Uniqueness Index over (name) (database name name_UNIQUE) */
    val index1 = index("name_UNIQUE", name, unique=true)
  }
  /** Collection-like TableQuery object for table Tags */
  lazy val Tags = new TableQuery(tag => new Tags(tag))
}
