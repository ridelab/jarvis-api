package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import services._

@Singleton
class OneNetApiController @Inject()(handler: DataHandler, cc: ControllerComponents) extends AbstractController(cc) {

  def receive = Action(parse.json) { implicit request =>
    handler handle request.body
    Ok
  }

  def verifyToken = Action { implicit request =>
    val parameters = request.queryString mapValues (_.head)
    Logger trace s"incoming verify token / parameters = $parameters"
    parameters get "msg" map (Ok(_)) getOrElse BadRequest
  }

}
