package controllers

import javax.inject._

import entities._
import play.api._
import play.api.mvc._
import services._

import scala.concurrent._

@Singleton
class WeixinApiController @Inject()(
    cc: ControllerComponents,
    unitApiService: UnitApiService,
    weixinVerifyTokenService: WeixinVerifyTokenService,
)(implicit executor: ExecutionContext)
    extends AbstractController(cc)
    with PrettyJsonResult {

  def utterance = (Action async parse.xml) { implicit request =>
    val incomingMessageType = (request.body \ "MsgType").text

    val reply = incomingMessageType match {
      case TextMessage.Type =>
        val incoming = TextMessage fromXml request.body
        Logger debug s"weixin incoming message / $incoming"
        Future successful TextMessage(
          receiver = incoming.sender,
          sender = incoming.receiver,
          createTime = incoming.createTime,
          content = s"re: ${incoming.content}",
        )

      case others =>
        val xml = request.body
        Future successful TextMessage(
          receiver = (xml \ "FromUserName").text,
          sender = (xml \ "ToUserName").text,
          createTime = (xml \ "CreateTime").text.toLong,
          content = s"unsupported message type: $others",
        )
    }

    reply map (_.toXml) map (Ok(_))
  }

  def verifyToken = Action { implicit request =>
    val parameters = request.queryString mapValues (_.head)
    val result = weixinVerifyTokenService verifyToken parameters
    Logger debug s"weixin verify token / result = $result, parameters = $parameters"
    result map (Ok(_)) getOrElse BadRequest
  }

}
