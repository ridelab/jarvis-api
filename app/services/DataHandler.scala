package services

import actors._
import akka.actor._
import javax.inject._
import play.api._
import play.api.libs.json._

import scala.concurrent._
import scala.concurrent.duration._

@Singleton
class DataHandler @Inject()(implicit system: ActorSystem, ec: ExecutionContext) {

  import MessagesHandlerActor.Message

  private val handler = system actorOf (MessagesHandlerActor.props, MessagesHandlerActor.name)

  def handle(value: JsValue) {
    Logger debug (Json prettyPrint value)
    (value \ "msg").toOption match {
      case Some(JsArray(vs)) => vs foreach handleMessage
      case Some(o: JsObject) => handleMessage(o)
      case _                 =>
    }
  }

  def handleMessage(message: JsValue) {
    (message \ "type").asOpt[Int] match {
      case Some(1) => handler ! Message(message)
      case _       =>
    }
  }

  (system.scheduler schedule (0.seconds, 2.seconds)) {
    handler ! Message(
      JsObject(
        Seq(
          "type" -> JsNumber(-1),
          "dev_id" -> JsNumber(1000000),
          "ds_id" -> JsString("data-stream-id"),
          "at" -> JsNumber(System currentTimeMillis ()),
          "value" -> JsString("value")
        )
      )
    )
  }

}
