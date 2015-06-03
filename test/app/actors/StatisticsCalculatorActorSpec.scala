package app.actors

import actors.{Filter, StatisticsCalculatorActor}
import akka.actor.ActorSystem
import akka.testkit._
import app.actors.Fixtures._
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class StatisticsCalculatorActorSpec extends TestKit(ActorSystem("StatisticsCalculatorActor"))
with WordSpecLike
with BeforeAndAfterAll
with ImplicitSender {

  override def afterAll() {
    system.shutdown()
  }

  "StatisticsCalculatorActor" should {

//    "send filter list to twitter actor if valid JsValue arrives" in new scope {
//
//      statisticsActor ! validTweet()
//
//      client.expectMsg(Filter(List("test", "test2")))
//    }

  }

  private trait scope {
    val client = TestProbe()
    val statisticsActor = TestActorRef(new StatisticsCalculatorActor(client.ref, (filters: List[String], text: String) => filters.filter(f => text.toLowerCase.indexOf(f.trim.toLowerCase) > -1), "filter"))
  }

}