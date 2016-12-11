package controllers

import actors.UserProfileActor
import akka.pattern.ask
import akka.util.Timeout
import com.danielasfregola.twitter4s.entities.User
import play.api.libs.json.{DefaultWrites, Json, Writes}
import play.api.mvc.{Action, Controller}
import play.libs.Akka
import util.TwitterClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object Users extends Controller {

  implicit val timeout: Timeout = 5 second
  val twitterInstance = TwitterClient.twitterInstance
  val userProfileActor = Akka.system().actorOf(UserProfileActor.props(twitterInstance))

  implicit object UserWriter extends Writes[User] with DefaultWrites {
    def writes(user: User) = Json.obj(
      "id" -> user.id,
      "screenName" -> user.screen_name,
      "name" -> user.name,
      "profileImageURL" -> user.profile_image_url.replaceAll("_normal", ""),
      "followersCount" -> user.followers_count,
      "favouritesCount" -> user.favourites_count,
      "friendsCount" -> user.friends_count,
      "createdAt" -> user.created_at
    )
  }

  def users(id: Long) = Action.async {
    val futureUser: Future[User] = (userProfileActor ? id).mapTo[User]
    futureUser.map(u => Ok(Json.toJson(u)))
  }

}