import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import models.Service
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val serviceFormat: RootJsonFormat[Service] = jsonFormat3(Service)
}
