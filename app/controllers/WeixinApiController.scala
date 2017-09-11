package controllers

import javax.inject._

import entities._
import play.api._
import play.api.libs.json._
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

    val outgoing = incomingMessageType match {
      case TextMessage.Type =>
        val incoming = TextMessage fromXml request.body
        Logger debug s"weixin incoming message / $incoming"

        unitApiService utterance (incoming.content, incoming.sender) map { reply =>
          Logger debug s"unit reply / ${Json prettyPrint reply}"

          val actions = (reply \ "result" \ "action_list").as[Seq[JsValue]]

          val content = actions flatMap { action =>
            val say = (action \ "say").as[String]
            val hints = (action \ "hint_list").as[Seq[JsValue]]
            say +: (hints map (hint => (hint \ "hint_query").as[String]))
          } mkString "\n"

          TextMessage(
            receiver = incoming.sender,
            sender = incoming.receiver,
            createTime = incoming.createTime,
            content = content,
          )
        }

      case others =>
        val xml = request.body
        Future successful TextMessage(
          receiver = (xml \ "FromUserName").text,
          sender = (xml \ "ToUserName").text,
          createTime = (xml \ "CreateTime").text.toLong,
          content = s"unsupported message type: $others",
        )
    }

    outgoing map (_.toXml) map (Ok(_))
  }

  def verifyToken = Action { implicit request =>
    val parameters = request.queryString mapValues (_.head)
    val result = weixinVerifyTokenService verifyToken parameters
    Logger debug s"weixin verify token / result = $result, parameters = $parameters"
    result map (Ok(_)) getOrElse BadRequest
  }

}
