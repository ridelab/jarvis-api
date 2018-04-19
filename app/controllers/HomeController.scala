package controllers

import javax.inject._
import play.api.mvc._
import views.html

@Singleton
class HomeController @Inject()(cc: ControllerComponents)(implicit assets: AssetsFinder) extends AbstractController(cc) {

  def index = Action {
    Ok(html index ())
  }

}
