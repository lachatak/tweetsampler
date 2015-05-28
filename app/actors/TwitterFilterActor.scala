package actors

import akka.actor._
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import util.TwitterClient
import twitter4j.{Status, _}

import scala.concurrent.duration._

class TwitterFilterActor(client: ActorRef) extends Actor with ActorLogging with StatusListener {

  implicit val timeout: Timeout = 5 second

  val twitterStreamInstance = TwitterClient.twitterStreamInstance
  val hashTagStatisticsCalculatorActor = context.system.actorOf(StatisticsCalculatorActor.props(client, (filters: List[String], text: String) => text.replace(',', ' ').replace('\n', ' ').replaceAll("\\w#", "\\w #").split(" ").filter(_.startsWith("#")).toList, "hashTag"))
  val filterStatisticsCalculatorActor = context.system.actorOf(StatisticsCalculatorActor.props(client, (filters: List[String], text: String) => filters.filter(f => text.toLowerCase.indexOf(f.trim.toLowerCase) > -1), "filter"))

  override def preStart(): Unit = {
    twitterStreamInstance.addListener(this)
    context watch client
    log.info("Filter actor created!")
  }

  override def receive = LoggingReceive {
    case Filter(array) =>
      hashTagStatisticsCalculatorActor ? ClearStats()
      filterStatisticsCalculatorActor ? ClearStats(array)
      twitterStreamInstance.cleanUp()
      twitterStreamInstance.filter(new FilterQuery().track(array.toArray).language(Array("en")))
    case Terminated(_) =>
      log.info("Terminating...")
      twitterStreamInstance.cleanUp()
      twitterStreamInstance.shutdown()
      context.stop(self)
  }

  override def onStatus(status: Status): Unit = {
    val tweet = Tweet(status.getId, status.getCreatedAt.getTime, status.getUser, status.getText)
    client ! tweet
    hashTagStatisticsCalculatorActor ! tweet
    filterStatisticsCalculatorActor ! tweet
  }

  override def onStallWarning(stallWarning: StallWarning): Unit = {}

  override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {}

  override def onScrubGeo(l: Long, l1: Long): Unit = {}

  override def onTrackLimitationNotice(i: Int): Unit = {}

  def onException(ex: Exception) {
    log.error(ex, "ERROR")
  }

}

object TwitterFilterActor {
  def props(client: ActorRef) = Props(classOf[TwitterFilterActor], client)
}
