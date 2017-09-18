package controllers

import javax.inject._

import play.api._
import play.api.libs.json._
import play.api.mvc._
import services._

import scala.concurrent._

@Singleton
class DoubanApiController @Inject()(
    cc: ControllerComponents,
    doubanApiService: DoubanApiService,
)(implicit executor: ExecutionContext)
    extends AbstractController(cc)
    with PrettyJsonResult {

  def searchMovie = (Action async parse.json) { implicit request =>
    val ro = for {
      query <- (request.body \ "query").asOpt[String]
    } yield {
      Logger trace s"incoming unit utterance / query = '$query'"
      doubanApiService searchMovie query
    }
    ro match {
      case Some(r) =>
        r map (Json toJson _) map (Ok(_))
      case None =>
        Future successful BadRequest
    }
  }

}
