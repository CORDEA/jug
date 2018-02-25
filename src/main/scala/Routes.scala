import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import models.{Service, Tag}

trait Routes extends JsonSupport {

  val route: Route =
    pathPrefix("jug") {
      pathPrefix("keys") {
        pathEnd {
          post {
            entity(as[Service]) { service =>
              // FIXME
              complete(Service(1, service.name, service.key, service.tags))
            }
          } ~
            parameter('tag.*) { tag =>
              get {
                complete(List(
                  Service(1, "name", "key", List(Tag(1, "tag")))
                ))
              }
            }
        } ~
          path(Segment) { name =>
            get {
              // FIXME
              complete(Service(1, name, "key", List(Tag(1, "tag"))))
            }
          }
      }
    }
}
