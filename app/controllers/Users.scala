package controllers

import actors.UserProfileActor
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.json.{DefaultWrites, Json, Writes}
import play.api.mvc.{Action, Controller}
import play.libs.Akka
import twitter4j.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object Users extends Controller {

  implicit val timeout: Timeout = 5 second
  val userProfileActor = Akka.system().actorOf(Props[UserProfileActor])

  implicit object UserWriter extends Writes[User] with DefaultWrites {
    def writes(user: User) = Json.obj(
      "id" -> user.getId,
      "screenName" -> user.getScreenName,
      "name" -> user.getName,
      "biggerProfileImageURL" -> user.getBiggerProfileImageURL,
      "followersCount" -> user.getFollowersCount,
      "favouritesCount" -> user.getFavouritesCount,
      "friendsCount" -> user.getFriendsCount,
      "createdAt" -> user.getCreatedAt
    )
  }

  def users(id: Long) = Action.async {
    val futureUser: Future[User] = (userProfileActor ? id).mapTo[User]
    futureUser.map(u => Ok(Json.toJson(u)))
  }

}