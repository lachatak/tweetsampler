package actors

import akka.actor._

class HashTagStatisticsCalculatorActor(client: ActorRef) extends StatisticsCalculatorActor(client) with Actor with ActorLogging {

  val statType = "hashTag"

  def addText(wordOccurrences: List[WordOccurrence], filters: List[String], date: Long, text: String) = wordOccurrences ::: text.replace(',', ' ').replace('\n', ' ').replaceAll("\\w#", "\\w #").split(" ").filter(_.startsWith("#")).map(i => WordOccurrence(i.trim, date)).toList

}

object HashTagStatisticsCalculatorActor {
  def props(client: ActorRef) = Props(classOf[HashTagStatisticsCalculatorActor], client)
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
