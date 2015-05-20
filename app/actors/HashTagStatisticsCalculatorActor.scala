package actors

import akka.actor.Actor.emptyBehavior
import akka.actor._
import akka.event.LoggingReceive
import org.joda.time.DateTime

import scala.concurrent.duration._
import scalaz.Monoid
import scalaz.Scalaz._

class HashTagStatisticsCalculatorActor(client: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher

  val windowInSec = 60
  var scheduleTime = 10 seconds
  var cancellable: Option[Cancellable] = None

  override def preStart(): Unit = {
    context become (receiveTweet())
    context watch client
    log.info("Stat actor created!")
  }

  override def receive: Receive = emptyBehavior

  def receiveTweet(wordOccurrences: List[WordOccurrence] = List.empty): Receive = LoggingReceive {
    case Tweet(_, date, _, text) =>
      cancellable = setCancellable
      val w = filterStats(addText(wordOccurrences, date, text))
      client ! generateMessage(w)
      context become (receiveTweet(w))
    case ClearStats =>
      cancellable = None
      context become (receiveTweet())
      sender ! Cleared
    case SendStats =>
      cancellable = setCancellable
      val w = filterStats(wordOccurrences)
      client ! generateMessage(w)
      context become (receiveTweet(w))
    case Terminated(_) =>
      log.info("Terminating...")
      cancellable.foreach(_.cancel)
      context.stop(self)
  }

  def addText(wordOccurrences: List[WordOccurrence], date: Long, text: String) = wordOccurrences ::: text.replace(',', ' ').replace('\n', ' ').replaceAll("\\w#", "\\w #").split(" ").filter(_.startsWith("#")).map(i => WordOccurrence(i.trim, date)).toList

  def filterStats(wordOccurrences: List[WordOccurrence]) = wordOccurrences.filter(_.dateTime > DateTime.now().minusSeconds(windowInSec).getMillis)

  def generateMessage(wordOccurrences: List[WordOccurrence]) = {
    implicit val wordOccurrenceAsMonoid = new WordOccurrenceAsMonoid
    WordOccurrences(wordOccurrences.foldMap(_.toMap).values.toList)
  }

  private def setCancellable = {
    cancellable.foreach(_.cancel)
    Some(context.system.scheduler.scheduleOnce(scheduleTime, self, SendStats))
  }
}

object HashTagStatisticsCalculatorActor {
  def props(client: ActorRef) = Props(classOf[HashTagStatisticsCalculatorActor], client)
}

case object ClearStats

case object Cleared

case object SendStats

case class WordOccurrences(wordOccurrences: List[WordOccurrence])

case class WordOccurrence(word: String, dateTime: Long, count: Int = 1) {
  val toMap = Map(word -> this)
}

class WordOccurrenceAsMonoid extends Monoid[WordOccurrence] {
  override def zero = WordOccurrence("", 0, 0)

  override def append(i1: WordOccurrence, i2: => WordOccurrence) = if (i1.dateTime > i2.dateTime) WordOccurrence(i1.word, i1.dateTime, i1.count + i2.count) else WordOccurrence(i1.word, i2.dateTime, i1.count + i2.count)
}


object Test extends App {

  case class ItemSummary(text: String, dateTime: DateTime, count: Int = 1) {
    val toMap = Map(text -> this)
  }

  implicit val ItemSummaryAsMonoid = new Monoid[ItemSummary] {
    override def zero = ItemSummary("", new DateTime(0), 0)

    override def append(i1: ItemSummary, i2: => ItemSummary) = if (i1.dateTime.isAfter(i2.dateTime)) ItemSummary(i1.text, i1.dateTime, i1.count + i2.count) else ItemSummary(i1.text, i2.dateTime, i1.count + i2.count)
  }

//  val text = "#Dogging,#Blowjob,#Public,#FootWorship,#Virgin,#Cum #Test Swallow: Buxom Black Sweetie In Stockings Gets Doggy Fucked http://t.co/wbaMXxIKAD"
      val text = " Buxom#Black Sweetie In Stockings Gets Doggy Fucked http://t.co/wbaMXxIKAD"

  val date = DateTime.now
  var wordCountItems: List[ItemSummary] = List(ItemSummary("#Test", date.plusMinutes(1)))
  val tweet = Tweet(600408686117449728L, date.getMillis, null, text)

  println(wordCountItems)
  wordCountItems = wordCountItems ::: tweet.text.replace(',', ' ').replace('\n', ' ').replaceAll("\\w#", "\\w #").split(" ").filter(_.startsWith("#")).map(i => ItemSummary(i.trim, date)).toList
  println(wordCountItems)

  wordCountItems = wordCountItems.filter(_.dateTime.isAfter(DateTime.now().minusSeconds(60)))
  println(wordCountItems)

  println(wordCountItems.foldMap(_.toMap).values)
}
