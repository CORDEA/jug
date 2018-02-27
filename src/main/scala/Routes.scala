import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import models.{Error, Service}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

trait Routes extends JsonSupport {

  val controller: ServiceController
  private val realm = "Jug"

  private def authenticator(credentials: Credentials): Future[Option[Unit]] =
    credentials match {
      case p@Credentials.Provided(_) =>
        Future {
          if (p.verify("")) Some()
          else None
        }
      case _ => Future.successful(None)
    }

  val route: Route =
    pathPrefix("jug") {
      pathPrefix("keys") {
        authenticateBasicAsync(realm, authenticator) { _ =>
          pathEnd {
            post {
              entity(as[Service]) { service =>
                onComplete(controller.saveService(service)) {
                  case Success(s) => complete(service)
                  case Failure(it) => complete {
                    Error(it.getMessage)
                  }
                }
              }
            }
          } ~
            parameter('id) { id =>
              delete {
                onComplete(controller.deleteService(id.toInt)) {
                  case Success(_) => complete(None)
                  case Failure(it) => complete {
                    Error(it.getMessage)
                  }
                }
              }
            } ~
            parameter('tag.*) { tag =>
              get {
                onComplete(controller.findServicesByTag(tag)) {
                  case Success(s) => complete(s)
                  case Failure(it) => complete {
                    Error(it.getMessage)
                  }
                }
              }
            } ~
            path(Segment) { name =>
              get {
                onComplete(controller.getService(name)) {
                  case Success(s) => complete(s)
                  case Failure(it) => complete {
                    Error(it.getMessage)
                  }
                }
              }
            }
        }
      }
    }
}
