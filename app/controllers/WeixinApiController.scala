package controllers

import javax.inject._

import entities._
import play.api._
import play.api.mvc._
import services._

import scala.concurrent._
import scala.util._

@Singleton
class WeixinApiController @Inject()(
    cc: ControllerComponents,
    unitApiService: UnitApiService,
    weixinMessageService: WeixinMessageService,
    weixinVerifyTokenService: WeixinVerifyTokenService,
)(implicit executor: ExecutionContext)
    extends AbstractController(cc)
    with PrettyJsonResult {

  def utterance = (Action async parse.xml) { implicit request =>
    weixinMessageService fromXml request.body map {
      case message: TextMessage =>
        Logger trace s"incoming weixin text message / $message"
        unitApiService utterance (message.content, message.sender) map { reply =>
          TextMessage(
            receiver = message.sender,
            sender = message.receiver,
            createTime = message.createTime,
            content = unitApiService extractContent reply,
          )
        }
      case message =>
        Future successful TextMessage(
          receiver = message.sender,
          sender = message.receiver,
          createTime = message.createTime,
          content = s"unsupported message type: ${message.messageType}",
        )
    } match {
      case Success(r) =>
        r map (weixinMessageService toXml _) map (Ok(_))
      case Failure(e) =>
        Future successful BadRequest(e.getMessage)
    }
  }

  def verifyToken = Action { implicit request =>
    val parameters = request.queryString mapValues (_.head)
    val result = weixinVerifyTokenService verifyToken parameters
    Logger trace s"incoming weixin verify token / result = $result, parameters = $parameters"
    result map (Ok(_)) getOrElse BadRequest
  }

}
