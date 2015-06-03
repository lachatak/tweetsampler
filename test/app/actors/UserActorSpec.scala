package app.actors

import actors.{Filter, UserActor}
import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import app.actors.Fixtures._
import org.joda.time.DateTime
import org.mockito.Mockito
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import play.api.libs.json.Json
import play.api.libs.json.Json._
import twitter4j.TwitterStream

class UserActorSpec extends TestKit(ActorSystem("UserActorSpec"))
with WordSpecLike
with BeforeAndAfterAll
with ImplicitSender {

  override def afterAll() {
    system.shutdown()
  }

  "UserActor" should {

    "send filter list to twitter actor if valid JsValue arrives" in new scope {

      userActor ! validFilterJsonMessage

      twitterFilterActorProbe.expectMsg(Filter(List("test", "test2")))
    }

    "not send filter list to twitter actor if invalid JsValue arrives" in new scope {

      userActor ! invalidFilterJsonMessage

      twitterFilterActorProbe.expectNoMsg()
    }

    "send message to UI if Tweet message arrives" in new scope {

      userActor ! validTweet()

      ourProbe.expectMsg(Json.obj("type" -> "tweet", "id" -> 1, "profileImageUrl" -> "http://image", "text" -> "tweet"))
    }

    "send message to UI if WordOccurrences message arrives" in new scope {

      val dateTime: Long = DateTime.now().getMillis
      val v = validWordOccurrence(date = dateTime)
      userActor ! validWordOccurrences(words = List(v))

      ourProbe.expectMsg(Json.obj("type" -> "stats", "stats" -> Json.obj("wordOccurrencesType" -> "test", "wordOccurrences" -> Json.arr(Json.obj("word" -> "word", "dateTime" -> dateTime, "count" -> 1)))))
    }
  }

  private trait scope {
    val twitterFilterActorProbe = TestProbe()
    val ourProbe = TestProbe()
    val actorFactory: (ActorRefFactory, Props) => ActorRef = (actorRefFactory, props) => twitterFilterActorProbe.ref
    val twitterStream = Mockito.mock(classOf[TwitterStream])
    val userActor = system.actorOf(UserActor.props("uid", actorFactory, twitterStream)(ourProbe.ref))
  }

}