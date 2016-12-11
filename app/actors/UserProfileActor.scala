package actors

import akka.actor._
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.TwitterRestClient

import scala.concurrent.ExecutionContext.Implicits.global

class UserProfileActor(twitterInstance: TwitterRestClient) extends Actor with ActorLogging {

  def receive: Receive = LoggingReceive {
    case id: Long =>
      val s = sender
      twitterInstance.getUserById(id).foreach {
        s ! _
      }
  }

}

object UserProfileActor {
  def props(twitterInstance: TwitterRestClient) = Props(classOf[UserProfileActor], twitterInstance)
}



