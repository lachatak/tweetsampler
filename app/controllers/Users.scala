package controllers

import play.api.mvc.{Action, Controller}

object Users extends Controller {

  def users(id: Long) = Action {
    implicit request => {
      Ok(s"$id is Loaded!!!")
    }
  }
}