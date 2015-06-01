package app.actors

import actors.UserProfileActor
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import twitter4j._

import scala.concurrent.duration._


class UserProfileActorSpec extends TestKit(ActorSystem("UserProfileActorSpec"))
with WordSpecLike
with BeforeAndAfterAll
with ImplicitSender {

  override def afterAll() {
    system.shutdown()
  }

  "UserProfileActor" should {

    //TODO test this function
    //    "send filter list to twitter actor if valid JsValue arrives" in new scope {
    //
    //      val result = for {
    //        user <- (userProfileActor ? 10L).mapTo[User]
    //      } yield user
    //
    //      println(result)
    //      expectMsg(result)
    //    }

  }

  private trait scope {
    implicit val timeout: Timeout = 5 second

    val twitterInstance = mock(classOf[Twitter])
    val responseList = mock(classOf[ResponseList[User]])
    val user = mock(classOf[User])

    when(responseList.get(0)).thenReturn(user)
    when(twitterInstance.lookupUsers(Array(10L))).thenReturn(responseList)

    val userProfileActor = system.actorOf(UserProfileActor.props(twitterInstance))
  }

}