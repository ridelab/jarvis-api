package actors

import akka.actor._
import akka.routing._
import play.api._
import play.api.libs.json._

class MessagesHandlerActor extends Actor {

  import MessagesHandlerActor._

  var router = Router(BroadcastRoutingLogic())

  def receive: Receive = {
    case Message(content) =>
      router route (content, sender)
    case AddSocket(socket) =>
      Logger debug s"add socket $socket"
      router = router addRoutee socket
    case RemoveSocket(socket) =>
      Logger debug s"remove socket $socket"
      router = router removeRoutee socket
  }

}

object MessagesHandlerActor {

  def props = Props[MessagesHandlerActor]

  def name = "messages-handler"

  case class Message(content: JsValue)

  case class AddSocket(socket: ActorRef)

  case class RemoveSocket(socket: ActorRef)

}
