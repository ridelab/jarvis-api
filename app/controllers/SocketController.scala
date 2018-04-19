package controllers

import actors._
import akka.actor._
import akka.stream._
import javax.inject._
import play.api.libs.json._
import play.api.libs.streams._
import play.api.mvc._

class SocketController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
    extends AbstractController(cc) {

  def socket = WebSocket accept [JsValue, JsValue] { implicit request =>
    ActorFlow actorRef (out => WebSocketActor props out)
  }

}
