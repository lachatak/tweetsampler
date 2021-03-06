package controllers

import java.util.UUID

import actors.UserActor
import play.api.Logger
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.mvc.{Action, Controller, WebSocket}
import util.TwitterClient

import scala.concurrent.Future

object Index extends Controller {

  val UID = "uid"
  val twitterStreamInstance = TwitterClient.twitterStreamInstance

  def index = Action {
    implicit request => {
      val uid = request.session.get(UID).getOrElse {
        UUID.randomUUID().toString
      }
      Ok(views.html.index()).withSession {
        Logger.debug("creation uid " + uid)
        request.session + (UID -> uid)
      }
    }
  }

  def ws = WebSocket.tryAcceptWithActor[JsValue, JsValue] {
    implicit request =>
      Future.successful(request.session.get(UID) match {
        case None => Left(Forbidden)
        case Some(uid) => Right(UserActor.props(uid = uid, twitterStreamInstance = twitterStreamInstance))
      })
  }
}