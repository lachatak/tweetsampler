package app.actors

import actors.{ClearStats, TwitterFilterActor}
import akka.actor._
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import akka.util.Timeout
import app.actors.Fixtures._
import org.joda.time.DateTime
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
    shutdown()
  }

  "TwitterFilterActorSpec" should {

    "clear stats calculators and start a filter if valid Filter(list) message arrives" in new scope {

      twitterFilterActor ! validFilterMessage

      statisticsCalculatorActor.expectMsg(ClearStats())
      statisticsCalculatorActor.expectMsg(ClearStats(validFilterList))

      verify(twitterStreamInstance).addListener(any(classOf[StatusListener]))
      verify(twitterStreamInstance).cleanUp()
      verify(twitterStreamInstance).filter(new FilterQuery().track(validFilterList.toArray).language(Array("en")))
    }

    "stop the twitter client if Terminated message arrives" in new scope {

      watch(twitterFilterActor)
      client.ref ! PoisonPill

      verify(twitterStreamInstance).addListener(any(classOf[StatusListener]))
      verify(twitterStreamInstance).cleanUp()
      verify(twitterStreamInstance).shutdown()

      expectTerminated(twitterFilterActor)
    }

    "deliver status to statistics calculators and the client" in new scope {

      val date = DateTime.now
      val status = mock(classOf[Status])
      val user = new StubUser

      when(status.getId).thenReturn(validTweetId)
      when(status.getCreatedAt).thenReturn(date.toDate)
      when(status.getUser).thenReturn(user)
      when(status.getText).thenReturn(validTweetText)
      twitterFilterActor.underlyingActor.onStatus(status)

      val tweet = validTweet(validTweetId, date.getMillis, user, validTweetText)
      client.expectMsg(tweet)
      statisticsCalculatorActor.expectMsg(tweet)
      statisticsCalculatorActor.expectMsg(tweet)
    }
  }

  private trait scope {
    implicit val timeout: Timeout = 5 second

    val client = TestProbe()
    val statisticsCalculatorActor = TestProbe()
    val twitterStreamInstance = mock(classOf[TwitterStream])

    val actorFactory: (ActorRefFactory, Props) => ActorRef = (actorRefFactory, props) => statisticsCalculatorActor.ref
    val twitterFilterActor = TestActorRef(new TwitterFilterActor(client.ref, twitterStreamInstance, actorFactory))
  }

}