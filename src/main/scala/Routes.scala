import akka.http.javadsl.model.headers.HttpCredentials
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, HttpChallenge}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import models.{Error, Service}
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

trait Routes extends JsonSupport {

  val controller: ServiceController
  private val realm = "Jug"
  private val challenge = HttpChallenge("Basic", Some(realm))

  private def authenticator(credentials: Option[HttpCredentials]): Future[AuthenticationResult[String]] =
    credentials match {
      case Some(BasicHttpCredentials(username, password)) =>
        controller.getUser(username)
          .map { u =>
            if (BCrypt.checkpw(password, u.password)) {
              Right(username)
            } else {
              Left(challenge)
            }
          }
      case _ => Future {
        Left(challenge)
      }
    }

  val route: Route =
    pathPrefix("jug") {
      pathPrefix("keys") {
        authenticateOrRejectWithChallenge(authenticator _) { _ =>
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
