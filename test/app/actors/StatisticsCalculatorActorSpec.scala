package app.actors

import actors.{Filter, UserActor}
import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import app.actors.Fixtures._
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import play.api.libs.json.Json
import play.api.libs.json.Json._

class StatisticsCalculatorActorSpec extends TestKit(ActorSystem("StatisticsCalculatorActor"))
with WordSpecLike
with BeforeAndAfterAll
with ImplicitSender {

  override def afterAll() {
    system.shutdown()
  }

  "StatisticsCalculatorActor" should {

    "send filter list to twitter actor if valid JsValue arrives" in new scope {

      userActor ! validFilterJsonMessage

      twitterFilterActorProbe.expectMsg(Filter(List("test", "test2")))
    }

  }

  private trait scope {
    val twitterFilterActorProbe = TestProbe()
    val ourProbe = TestProbe()
    val actorFactory: (ActorRefFactory, Props) => ActorRef = (actorRefFactory, props) => twitterFilterActorProbe.ref
    val userActor = system.actorOf(UserActor.props("uid", actorFactory)(ourProbe.ref))
  }

}