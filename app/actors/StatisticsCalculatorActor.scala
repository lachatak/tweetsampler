package actors

import akka.actor.Actor.emptyBehavior
import akka.actor._
import akka.event.LoggingReceive
import org.joda.time.DateTime

import scala.concurrent.duration._
import scalaz.Monoid
import scalaz.Scalaz._

class StatisticsCalculatorActor(client: ActorRef, extractor: (List[String], String) => List[String], statType: String) extends Actor with ActorLogging {

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

  def receiveTweet(wordOccurrences: List[WordOccurrence] = List.empty, filters: List[String] = List.empty): Receive = LoggingReceive {
    case Tweet(_, date, _, text) =>
      cancellable = setCancellable
      val words = filterStats(addOccurrences(wordOccurrences, extractor(filters, text), date))
      client ! generateMessage(words)
      context become (receiveTweet(words, filters))
    case ClearStats(filters) =>
      cancellable = None
      context become (receiveTweet(filters = filters))
      sender ! Cleared
    case SendStats =>
      cancellable = setCancellable
      val words = filterStats(wordOccurrences)
      client ! generateMessage(words)
      context become (receiveTweet(words, filters))
    case Terminated(_) =>
      log.info("Terminating...")
      cancellable.foreach(_.cancel)
      context.stop(self)
  }

  def addOccurrences(wordOccurrences: List[WordOccurrence], extracted: List[String], createdAt: Long) = wordOccurrences ::: extracted.map(i => WordOccurrence(i.trim, createdAt)).toList

  def filterStats(wordOccurrences: List[WordOccurrence]) = wordOccurrences.filter(_.dateTime > DateTime.now().minusSeconds(windowInSec).getMillis)

  def generateMessage(wordOccurrences: List[WordOccurrence]) = {
    implicit val wordOccurrenceAsMonoid = new WordOccurrenceAsMonoid
    WordOccurrences(statType, wordOccurrences.foldMap(_.toMap).values.toList)
  }

  private def setCancellable = {
    cancellable.foreach(_.cancel)
    Some(context.system.scheduler.scheduleOnce(scheduleTime, self, SendStats))
  }
}

case class ClearStats(filters: List[String] = List.empty)

case object Cleared

case object SendStats

case class WordOccurrences(wordOccurrencesType: String, wordOccurrences: List[WordOccurrence])

case class WordOccurrence(word: String, dateTime: Long, count: Int = 1) {
  val toMap = Map(word -> this)
}

class WordOccurrenceAsMonoid extends Monoid[WordOccurrence] {
  override def zero = WordOccurrence("", 0, 0)

  override def append(i1: WordOccurrence, i2: => WordOccurrence) = if (i1.dateTime > i2.dateTime) WordOccurrence(i1.word, i1.dateTime, i1.count + i2.count) else WordOccurrence(i1.word, i2.dateTime, i1.count + i2.count)
}

object StatisticsCalculatorActor {
  def props(client: ActorRef, extractor: (List[String], String) => List[String], statType: String) = Props(classOf[StatisticsCalculatorActor], client, extractor, statType)
}

//object Test extends App {
//
//  case class ItemSummary(text: String, dateTime: DateTime, count: Int = 1) {
//    val toMap = Map(text -> this)
//  }
//
//  implicit val ItemSummaryAsMonoid = new Monoid[ItemSummary] {
//    override def zero = ItemSummary("", new DateTime(0), 0)
//
//    override def append(i1: ItemSummary, i2: => ItemSummary) = if (i1.dateTime.isAfter(i2.dateTime)) ItemSummary(i1.text, i1.dateTime, i1.count + i2.count) else ItemSummary(i1.text, i2.dateTime, i1.count + i2.count)
//  }
//
////  val text = "#Dogging,#Blowjob,#Public,#FootWorship,#Virgin,#Cum #Test Swallow: Buxom Black Sweetie In Stockings Gets Doggy Fucked http://t.co/wbaMXxIKAD"
//      val text = " Buxom#Black Sweetie In Stockings Gets Doggy Fucked http://t.co/wbaMXxIKAD"
//
//  val date = DateTime.now
//  var wordCountItems: List[ItemSummary] = List(ItemSummary("#Test", date.plusMinutes(1)))
//  val tweet = Tweet(600408686117449728L, date.getMillis, null, text)
//
//  println(wordCountItems)
//  wordCountItems = wordCountItems ::: tweet.text.replace(',', ' ').replace('\n', ' ').replaceAll("\\w#", "\\w #").split(" ").filter(_.startsWith("#")).map(i => ItemSummary(i.trim, date)).toList
//  println(wordCountItems)
//
//  wordCountItems = wordCountItems.filter(_.dateTime.isAfter(DateTime.now().minusSeconds(60)))
//  println(wordCountItems)
//
//  println(wordCountItems.foldMap(_.toMap).values)
//}
