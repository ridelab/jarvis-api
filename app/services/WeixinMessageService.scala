package services

import javax.inject._

import entities._

import scala.util._
import scala.xml._

@Singleton
class WeixinMessageService @Inject()() {

  def fromXml(xml: NodeSeq): Try[BaseMessage] = Try {
    val sender = (xml \ "FromUserName").text
    val receiver = (xml \ "ToUserName").text
    val createTime = (xml \ "CreateTime").text.toLong
    val messageType = (xml \ "MsgType").text
    messageType match {
      case TextMessage.Type =>
        TextMessage(
          sender = sender,
          receiver = receiver,
          createTime = createTime,
          messageType = messageType,
          messageId = (xml \ "MsgId").text,
          content = (xml \ "Content").text,
        )
      case _ =>
        OtherMessage(
          sender = sender,
          receiver = receiver,
          createTime = createTime,
          messageType = messageType,
          source = xml,
        )
    }
  }

  def toXml(message: BaseMessage): Elem =
    <xml>
      <FromUserName>{message.sender}</FromUserName>
      <ToUserName>{message.receiver}</ToUserName>
      <CreateTime>{message.createTime}</CreateTime>
      <MsgType>{message.messageType}</MsgType>
      {message match {
        case m: TextMessage =>
          <Content>{m.content}</Content>
        case _ =>
          ???
      }}
    </xml>

}
