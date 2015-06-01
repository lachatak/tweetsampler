package app.actors

import actors.{ClearStats, TwitterFilterActor}
import akka.actor._
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import akka.util.Timeout
import app.actors.Fixtures._
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import twitter4j._

import scala.concurrent.duration._


class TwitterFilterActorSpec extends TestKit(ActorSystem("TwitterFilterActorSpec"))
with WordSpecLike
with BeforeAndAfterAll
with ImplicitSender {

  override def afterAll() {
    system.shutdown()
  }

  "TwitterFilterActorSpec" should {

    "clear stats calculators and start a filter if valid Filter(list) message arrives" in new scope {

      twitterFilterActor ! validFilterMessage

      statisticsCalculatorActor.expectMsg(ClearStats())
      statisticsCalculatorActor.expectMsg(ClearStats(validFilterList))

      verify(twitterStreamInstance).cleanUp()
      verify(twitterStreamInstance).filter(new FilterQuery().track(validFilterList.toArray).language(Array("en")))
    }


    "stp the twitter client if Terminated message arrives" in new scope {

      client.ref ! PoisonPill

      verify(twitterStreamInstance).addListener(any(classOf[StatusListener]))
      verify(twitterStreamInstance).cleanUp()
      verify(twitterStreamInstance).shutdown()
    }
  }

  private trait scope {
    implicit val timeout: Timeout = 5 second

    val client = TestProbe()
    val statisticsCalculatorActor = TestProbe()
    val twitterStreamInstance = mock(classOf[TwitterStream])

    val actorFactory: (ActorRefFactory, Props) => ActorRef = (actorRefFactory, props) => statisticsCalculatorActor.ref
    val twitterFilterActor = system.actorOf(TwitterFilterActor.props(client.ref, twitterStreamInstance, actorFactory))
  }

}