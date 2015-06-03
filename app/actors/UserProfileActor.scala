package actors

import akka.actor._
import akka.event.LoggingReceive
import twitter4j.Twitter

import scala.collection.JavaConversions._

class UserProfileActor(twitterInstance: Twitter) extends Actor with ActorLogging {

  def receive: Receive = LoggingReceive {
    case id: Long => sender ! twitterInstance.lookupUsers(Array(id)).toList.head
  }

}

object UserProfileActor {
  def props(twitterInstance: Twitter) = Props(classOf[UserProfileActor], twitterInstance)
}



