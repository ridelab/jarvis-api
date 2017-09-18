package services

import javax.inject._

import play.api._
import play.api.libs.json._
import play.api.libs.ws._

import scala.async.Async._
import scala.concurrent._
import scala.util._

/**
  * unit-api: `https://ai.baidu.com/docs#/unit-api/top`
  * play-ws: `https://www.playframework.com/documentation/latest/ScalaWS`
  */
@Singleton
class UnitApiService @Inject()(
    ws: WSClient,
    config: Configuration,
    unitAuthService: UnitAuthService,
)(implicit executor: ExecutionContext) {

  private val sceneId = config.get[Int]("unit.scene-id")

  def isSatisfied(reply: JsValue): Boolean =
    Try {
      val actions = (reply \ "result" \ "action_list").as[Seq[JsValue]]
      val actionTypes = actions flatMap { action =>
        (action \ "action_type" \ "act_type").asOpt[String]
      }
      actionTypes contains "satisfy"
    } getOrElse false

  def selectWords(reply: JsValue): Seq[String] =
    Try {
      val intentCandidates = (reply \ "result" \ "qu_res" \ "intent_candidates").as[Seq[JsValue]]
      val slots = intentCandidates flatMap { intentCandidate =>
        (intentCandidate \ "slots").as[Seq[JsValue]]
      }
      slots map { slot =>
        // val slotType = (slot \ "type").as[String]
        (slot \ "original_word").as[String].trim
      }
    } getOrElse Seq()

  def extractContent(reply: JsValue): String =
    Try {
      Logger trace s"unit reply / ${Json prettyPrint reply}"
      val actions = (reply \ "result" \ "action_list").as[Seq[JsValue]]
      actions flatMap { action =>
        val say = (action \ "say").as[String]
        val hints = (action \ "hint_list").as[Seq[JsValue]]
        val hintQueries = hints map (hint => (hint \ "hint_query").as[String])
        say +: hintQueries
      } mkString "\n"
    } getOrElse ""

  def utterance(
      query: String,
      sessionId: String,
  ): Future[JsValue] = async {
    await(fetchUtterance(query, sessionId, sceneId, await(unitAuthService.accessToken)))
  }

  def fetchUtterance(
      query: String,
      sessionId: String,
      sceneId: Int,
      accessToken: String,
  ): Future[JsValue] = async {
    val url = "https://aip.baidubce.com/rpc/2.0/solution/v1/unit_utterance"
    val data = Json obj (
      "scene_id" -> sceneId,
      "query" -> query,
      "session_id" -> sessionId,
    )
    val response = await(
      ws url url
        addQueryStringParameters ("access_token" -> accessToken)
        post data
    )
    response.json
  }

}
