package actors

import akka.actor._

class FilterStatisticsCalculatorActor(client: ActorRef) extends StatisticsCalculatorActor(client) with Actor with ActorLogging {

  val statType = "filter"

  def addText(wordOccurrences: List[WordOccurrence], filters: List[String], date: Long, text: String) = wordOccurrences ::: filters.filter(f => text.toLowerCase.indexOf(f.trim.toLowerCase) > -1).map(i => WordOccurrence(i.trim, date)).toList

}

object FilterStatisticsCalculatorActor {
  def props(client: ActorRef) = Props(classOf[FilterStatisticsCalculatorActor], client)
}
