package actors

import akka.actor.Actor.emptyBehavior
import akka.actor._
import akka.event.LoggingReceive
import org.joda.time.DateTime
import play.libs.Akka

class UserAdministratorActor extends Actor with ActorLogging {

  val twitterFilterActor = context.system.actorOf(TwitterFilterActor.props(self))
  
  override def preStart(): Unit ={
    log.info("Administrator actor created!")
    context become (twitterActorRequestHandler())
  }

  override def receive: Receive = emptyBehavior

  def twitterActorRequestHandler(twitterActors : Map[String, (DateTime, ActorRef)] = Map.empty): Receive = LoggingReceive {
    case TwitterFilterActorRequest(uid) => sender
//      users += sender
  }
}

object UserAdministratorActor {
  lazy val administrator = Akka.system().actorOf(Props[UserAdministratorActor])
  def apply() = administrator
}


case class TwitterFilterActorRequest(uid: String)



