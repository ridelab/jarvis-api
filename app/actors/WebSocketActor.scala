package actors

import akka.actor._

class WebSocketActor(out: ActorRef) extends Actor {

  import MessagesHandlerActor._

  def receive: Receive = {
    case m => out forward m
  }

  override def preStart(): Unit =
    (context actorSelection s"/user/${MessagesHandlerActor.name}") ! AddSocket(self)

  override def postStop(): Unit =
    (context actorSelection s"/user/${MessagesHandlerActor.name}") ! RemoveSocket(self)

}

object WebSocketActor {

  def props(out: ActorRef) = Props(new WebSocketActor(out))

}
