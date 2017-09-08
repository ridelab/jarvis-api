package services

import javax.inject._

import play.api._
import play.api.libs.json._
import play.api.libs.ws._

import scala.async.Async._
import scala.concurrent._

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
