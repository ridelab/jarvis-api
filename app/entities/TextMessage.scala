package entities

case class TextMessage(
    sender: String = "",
    receiver: String = "",
    createTime: Long = 0L,
    messageType: String = TextMessage.Type,
    messageId: String = "",
    content: String = "",
) extends BaseMessage

object TextMessage {

  val Type = "text"

}
