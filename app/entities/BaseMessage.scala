package entities

trait BaseMessage {

  def sender: String

  def receiver: String

  def createTime: Long

  def messageType: String

}
