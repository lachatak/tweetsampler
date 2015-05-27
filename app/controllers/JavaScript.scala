package controllers

import play.api.Routes
import play.api.mvc.{Action, Controller}

object JavaScript extends Controller {

  def jsRoutes(varName: String = "jsRoutes") = Action { implicit request =>
    Ok(
      Routes.javascriptRouter(varName)(
        controllers.routes.javascript.Users.users
      )
    ).as(JAVASCRIPT)
  }
}
