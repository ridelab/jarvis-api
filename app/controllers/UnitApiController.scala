package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import services._

import scala.concurrent._

@Singleton
class UnitApiController @Inject()(
    cc: ControllerComponents,
    unitApiService: UnitApiService,
)(implicit executor: ExecutionContext)
    extends AbstractController(cc)
    with PrettyJsonResult {

  def utterance = (Action async parse.json) { implicit request =>
    val reply = for {
      query <- (request.body \ "query").asOpt[String]
      sessionId <- (request.body \ "sessionId").asOpt[String]
    } yield {
      Logger trace s"incoming unit utterance / sessionId = '$sessionId', query = '$query'"
      unitApiService utterance (query, sessionId)
    }
    reply match {
      case Some(r) =>
        r map (Ok(_))
      case None =>
        Future successful BadRequest
    }
  }

}
