import models.{Service, Tables, Tag}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class ServiceController(db: Database) {

  def getService(name: String): Future[Service] = {
    val services = TableQuery[Tables.Services]
    val tags = TableQuery[Tables.Tags]
    val servicesTags = TableQuery[Tables.ServicesTags]
    val query = for {
      (s, t) <- (services filter (_.name === name)) joinLeft (servicesTags
        join tags on (_.tagId === _.id)) on (_.id === _._1.serviceId)
    } yield (s, t)

    db.run(query
      .result
      .map { r =>
        Service(
          r.head._1.id,
          r.head._1.name,
          r.head._1.key,
          r.map(_._2.map(_._2)).filter(_.orNull != null).map(_.get).map { t => Tag(t.id, t.name) }.toList
        )
      }
    )
  }

  def findServicesByTag(tag: Iterable[String]): Future[Iterable[Service]] = {
    val services = TableQuery[Tables.Services]
    val tags = TableQuery[Tables.Tags]
    val servicesTags = TableQuery[Tables.ServicesTags]
    val query = for {
      ((((_, _), s), _), t) <- ((((tags filter (_.name inSet tag))
        join servicesTags on (_.id === _.tagId))
        join services on (_._2.serviceId === _.id))
        join servicesTags on (_._2.id === _.serviceId)) join tags on (_._2.tagId === _.id)
    } yield (s, t)

    db.run(query
      .result
      .map(_.groupBy(_._1.id)
        .map { pair =>
          Service(
            pair._2.head._1.id,
            pair._2.head._1.name,
            pair._2.head._1.key,
            pair._2.map(_._2).map { t => Tag(t.id, t.name) }.toList
          )
        }
      )
    )
  }

  def saveService(service: Service): Future[Unit] = {
    val serviceQuery = TableQuery[Tables.Services]
    val tagQuery = TableQuery[Tables.Tags]
    val servicesTagsQuery = TableQuery[Tables.ServicesTags]

    val tags = Await.result(db.run(tagQuery.filter(_.name inSet service.tags.map(_.name)).result), Duration.Inf)

    val action = for {
      serviceId <- ((serviceQuery returning serviceQuery.map(_.id))
        into ((s, id) => s.copy(id = id)) += Tables.ServicesRow(0, service.name, service.key)).map(_.id)
      tag <- ((tagQuery returning tagQuery.map(_.id))
        into ((t, id) => t.copy(id = id)) ++= service.tags
        .filterNot { t =>
          tags.map(_.id).contains(t.id)
        }.map { tag => Tables.TagsRow(0, tag.name) })
      _ <- ((servicesTagsQuery returning servicesTagsQuery.map(_.id))
        into ((s, id) => s.copy(id = id)) ++= (tag ++ tags)
        .map { tag => Tables.ServicesTagsRow(0, serviceId, tag.id) })
    } yield ()

    db.run(action)
  }

  def deleteService(service: Service): Future[Unit] = {
    val serviceQuery = TableQuery[Tables.Services]
    val tagQuery = TableQuery[Tables.Tags]
    val servicesTagsQuery = TableQuery[Tables.ServicesTags]

    val aloneTags = Await.result(
      db.run(servicesTagsQuery.filter(_.tagId inSet service.tags.map(_.id)).result),
      Duration.Inf
    ).groupBy(_.tagId).filter(_._2.lengthCompare(1) == 0).keys

    val action = for {
      _ <- servicesTagsQuery.filter(_.serviceId === service.id).delete
      _ <- serviceQuery.filter(_.id === service.id).delete
      _ <- tagQuery.filter(_.id inSet aloneTags).delete
    } yield ()

    db.run(action)
  }
}

object ServiceController {
  def apply(db: Database): ServiceController = {
    new ServiceController(db)
  }
}
