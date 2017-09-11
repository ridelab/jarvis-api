package entities

import scala.xml._

case class TextMessage(
    receiver: String = "",
    sender: String = "",
    createTime: Long = 0L,
    messageId: String = "",
    content: String = "",
) extends BaseMessage {

  override def messageType: String = TextMessage.Type

  def toXml: Elem = <xml>
    <ToUserName>{receiver}</ToUserName>
    <FromUserName>{sender}</FromUserName>
    <CreateTime>{createTime}</CreateTime>
    <MsgType>{messageType}</MsgType>
    <Content>{content}</Content>
  </xml>

}

object TextMessage {

  val Type = "text"

  def fromXml(xml: NodeSeq): TextMessage = apply(
    receiver = (xml \ "ToUserName").text,
    sender = (xml \ "FromUserName").text,
    createTime = (xml \ "CreateTime").text.toLong,
    messageId = (xml \ "MsgId").text,
    content = (xml \ "Content").text,
  )

}
