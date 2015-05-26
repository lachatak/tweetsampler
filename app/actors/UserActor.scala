package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import play.api.libs.json.Json._
import play.api.libs.json.{JsValue, Json}
import twitter4j._

class UserActor(uid: String, out: ActorRef) extends Actor with ActorLogging {

  implicit val wordOccurrenceFormat = Json.format[WordOccurrence]
  implicit val wordOccurrencesFormat = Json.format[WordOccurrences]

  val twitterFilterActor = context.system.actorOf(TwitterFilterActor.props(self))

  log.info("User session actor created!")

  def receive = LoggingReceive {
    case js: JsValue => (js \ "filter").validate[String] map (f => twitterFilterActor ! Filter(f.split(" ").toList))
    case Tweet(_, _, user, text) => out ! Json.obj("type" -> "tweet", "id" -> user.getId, "profileImageUrl" -> user.getOriginalProfileImageURL, "text" -> text)
    case w@WordOccurrences(_, _) => out ! Json.obj("type" -> "stats", "stats" -> w)
  }

}

object UserActor {
  def props(uid: String)(out: ActorRef) = Props(classOf[UserActor], uid, out)
}

case class Filter(filter: List[String])

case class Tweet(id: Long, createdAt: Long, user: User, text: String)





