package actors

import akka.actor._
import akka.event.LoggingReceive
import play.api.libs.json.Json._
import play.api.libs.json.{JsValue, Json}
import twitter4j._

class UserActor(uid: String, actorFactory: (ActorRefFactory, Props) => ActorRef, twitterStreamInstance: TwitterStream, out: ActorRef) extends Actor with ActorLogging {

  implicit val wordOccurrenceFormat = Json.format[WordOccurrence]
  implicit val wordOccurrencesFormat = Json.format[WordOccurrences]

  val twitterFilterActor = actorFactory(context, TwitterFilterActor.props(client = self, twitterStreamInstance = twitterStreamInstance))

  log.info("User session actor created!")

  def receive = LoggingReceive {
    case js: JsValue => (js \ "filter").validate[String] map (f => twitterFilterActor ! Filter(f.split(" ").toList))
    case Tweet(_, _, user, text) => out ! Json.obj("type" -> "tweet", "id" -> user.getId, "profileImageUrl" -> user.getOriginalProfileImageURL, "text" -> text)
    case w: WordOccurrences => out ! Json.obj("type" -> "stats", "stats" -> w)
  }

}

object UserActor {
  def props(uid: String, actorFactory: (ActorRefFactory, Props) => ActorRef = (actorRefFactory, props) => actorRefFactory.actorOf(props), twitterStreamInstance: TwitterStream)(out: ActorRef) = Props(classOf[UserActor], uid, actorFactory, twitterStreamInstance, out)
}

case class Filter(filter: List[String])

case class Tweet(id: Long, createdAt: Long, user: User, text: String)





