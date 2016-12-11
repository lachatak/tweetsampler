package actors

import akka.actor._
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.enums.Language
import com.danielasfregola.twitter4s.entities.{Tweet => DSTweet}

import scala.concurrent.duration._

class TwitterFilterActor(client: ActorRef, twitterStreamInstance: TwitterStreamingClient, actorFactory: (ActorRefFactory, Props) => ActorRef) extends Actor with ActorLogging {

  implicit val timeout: Timeout = 5 second

  val hashTagStatisticsCalculatorActor = actorFactory(context, StatisticsCalculatorActor.props(client, (filters: List[String], text: String) => text.replace(',', ' ').replace('\n', ' ').replaceAll("\\w#", "\\w #").split(" ").filter(_.startsWith("#")).toList, "hashTag"))
  val filterStatisticsCalculatorActor = actorFactory(context, StatisticsCalculatorActor.props(client, (filters: List[String], text: String) => filters.filter(f => text.toLowerCase.indexOf(f.trim.toLowerCase) > -1), "filter"))

  override def preStart(): Unit = {
    context watch client
    log.info("Filter actor created!")
  }

  override def receive = LoggingReceive {
    case Filter(list) =>
      hashTagStatisticsCalculatorActor ? ClearStats()
      filterStatisticsCalculatorActor ? ClearStats(list)
      twitterStreamInstance.getStatusesFilter(track = list, language = Seq(Language.English)) {
        case tweet: DSTweet => onStatus(tweet)
      }
      //TODO stream clean up?
    //      twitterStreamInstance.cleanUp()
    //      twitterStreamInstance.filter(new FilterQuery().track(list.toArray).language(Array("en")))
    case Terminated(_) =>
      log.info("Terminating...")
      //TODO stream clean up?
      //      twitterStreamInstance.cleanUp()
      //      twitterStreamInstance.shutdown()
      context.stop(self)
  }

  private def onStatus(status: DSTweet): Unit = {
    //TODO .get is not nice in user
    val tweet = Tweet(status.id, status.created_at.getTime, status.user.get, status.text)
    client ! tweet
    hashTagStatisticsCalculatorActor ! tweet
    filterStatisticsCalculatorActor ! tweet
  }

}

object TwitterFilterActor {
  def props(client: ActorRef, twitterStreamInstance: TwitterStreamingClient, actorFactory: (ActorRefFactory, Props) => ActorRef = (actorRefFactory, props) => actorRefFactory.actorOf(props)) = Props(classOf[TwitterFilterActor], client, twitterStreamInstance, actorFactory)
}
