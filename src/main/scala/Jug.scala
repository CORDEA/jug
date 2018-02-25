import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Jug extends App with Routes {
  val config = ConfigFactory.load()
  implicit val system: ActorSystem = ActorSystem("jug", config)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  val db = Database.forConfig("db")

  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
