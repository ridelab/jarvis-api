package services

import javax.inject._
import play.api._
import play.api.libs.json._

@Singleton
class DataHandler @Inject()(processor: DataProcessor) {

  def handle(value: JsValue) {
    Logger debug (Json prettyPrint value)
    (value \ "msg").get match {
      case JsArray(vs) => vs foreach handleMessage
      case o: JsObject => handleMessage(o)
      case _           =>
    }
  }

  def handleMessage(message: JsValue) {
    (message \ "type").as[Int] match {
      case 1 =>
        processor handleData (
          (message \ "value").as[String],
          (message \ "dev_id").as[Long],
          (message \ "ds_id").as[String],
          (message \ "at").as[Long],
        )
      case _ =>
    }
  }

}
