package controllers

import javax.inject._
import play.api._
import play.api.mvc._

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def index() = Action { implicit request: Request[AnyContent] =>
    val username = request.session.get("username")
    if (username.isEmpty) {
      Redirect(routes.AuthController.loginPage)
    } else {
      Ok(views.html.index(username.get))
    }
  }
}
