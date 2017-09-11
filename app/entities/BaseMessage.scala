package entities

trait BaseMessage {

  def receiver: String

  def sender: String

  def createTime: Long

  def messageType: String

}
