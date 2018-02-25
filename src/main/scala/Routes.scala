import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import models.Service

trait Routes extends JsonSupport {

  val route: Route =
    pathPrefix("jug") {
      pathPrefix("keys") {
        pathEnd {
          post {
            entity(as[Service]) { service =>
              // FIXME
              complete(Service(service.name, service.key, service.tags))
            }
          } ~
            parameter('tag.*) { tag =>
              get {
                complete(List(
                  Service("name", "key", List("tag"))
                ))
              }
            }
        } ~
          path(Segment) { name =>
            get {
              // FIXME
              complete(Service(name, "key", List("tag")))
            }
          }
      }
    }
}
