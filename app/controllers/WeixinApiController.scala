package controllers

import javax.inject._

import entities._
import play.api._
import play.api.mvc._
import services._

import scala.async.Async._
import scala.concurrent._
import scala.util._

@Singleton
class WeixinApiController @Inject()(
    cc: ControllerComponents,
    unitApiService: UnitApiService,
    doubanApiService: DoubanApiService,
    weixinMessageService: WeixinMessageService,
    weixinVerifyTokenService: WeixinVerifyTokenService,
)(implicit executor: ExecutionContext)
    extends AbstractController(cc)
    with PrettyJsonResult {

  def utterance = (Action async parse.xml) { implicit request =>
    weixinMessageService fromXml request.body map {
      case message: TextMessage =>
        async {
          Logger trace s"incoming weixin text message / $message"
          val reply = await(unitApiService utterance (message.content, message.sender))
          val content = if (unitApiService isSatisfied reply) {
            val words = unitApiService selectWords reply
            val movies = await(doubanApiService searchMovie (words mkString " "))
            movies sortBy (-_.rating.average) take 5 map { movie =>
              s"""${movie.title}${if (movie.originalTitle.nonEmpty) s" (${movie.originalTitle})" else ""}
                 |评分: ${movie.rating.average}
                 |类型: ${movie.genres mkString " "}
                 |导演: ${movie.directors map (_.name) mkString " "}
                 |主演: ${movie.casts map (_.name) mkString " "}
                 |年代: ${movie.year}
                 |${movie.alt}
               """.stripMargin.trim
            } mkString "\n----------\n"
          } else {
            unitApiService extractContent reply
          }
          TextMessage(
            receiver = message.sender,
            sender = message.receiver,
            createTime = message.createTime,
            content = content,
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
