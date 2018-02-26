import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import models.{Error, Service}

import scala.util.{Failure, Success}

trait Routes extends JsonSupport {

  val controller: ServiceController

  val route: Route =
    pathPrefix("jug") {
      pathPrefix("keys") {
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
