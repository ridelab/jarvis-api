package entities

import scala.xml._

case class OtherMessage(
    sender: String = "",
    receiver: String = "",
    createTime: Long = 0L,
    messageType: String = "",
    source: NodeSeq = NodeSeq.Empty,
) extends BaseMessage
