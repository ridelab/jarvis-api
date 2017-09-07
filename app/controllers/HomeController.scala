package controllers

import javax.inject._

import play.api.mvc._

@Singleton
class HomeController @Inject()(c: ControllerComponents) extends AbstractController(c) {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

}
