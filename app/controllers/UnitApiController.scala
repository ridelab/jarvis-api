package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import services._

import scala.concurrent._

@Singleton
class UnitApiController @Inject()(
    unitApiService: UnitApiService,
    unitAuthService: UnitAuthService,
    cc: ControllerComponents,
)(implicit executor: ExecutionContext)
    extends AbstractController(cc) {

  def utterance = Action async { implicit request =>
    val sceneId = 8232
    val query = (request getQueryString "query") getOrElse "电影推荐"
    val sessionId = (request getQueryString "sessionId") getOrElse request.connection.remoteAddressString

    Logger trace s"unit-utterance / sessionId = '$sessionId', query = '$query'"

    unitApiService utterance (sceneId, query, sessionId, unitAuthService.accessToken) map (Ok(_))
  }

}
