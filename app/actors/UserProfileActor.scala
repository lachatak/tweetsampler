package actors

import akka.actor._
import akka.event.LoggingReceive
import util.TwitterClient
import scala.collection.JavaConversions._

class UserProfileActor extends Actor with ActorLogging {

  val twitterInstance = TwitterClient.twitterInstance

  def receive: Receive = LoggingReceive {
    case id: Long => sender ! twitterInstance.lookupUsers(Array(id)).toList.head
  }

}

object UserProfileActor {
  def props = Props(classOf[UserProfileActor])
}



