import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import models.{Error, Service, Tag}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val serviceFormat: RootJsonFormat[Service] = jsonFormat4(Service)
  implicit val tagFormat: RootJsonFormat[Tag] = jsonFormat2(Tag)
  implicit val errorFormat: RootJsonFormat[Error] = jsonFormat1(Error)
}
