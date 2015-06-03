package app.actors

import actors.UserProfileActor
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.Timeout
import org.mockito.Mockito._
import org.scalatest.Matchers._
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import twitter4j._

import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration._

class UserProfileActorSpec extends TestKit(ActorSystem("UserProfileActorSpec"))
with WordSpecLike
with BeforeAndAfterAll
with ImplicitSender {

  override def afterAll() {
    system.shutdown()
  }

  "UserProfileActor" should {

    "send back requested user profile to the sender" in new scope {

      val result = Await.result((userProfileActor ? 10L).mapTo[User], 5 seconds)

      result should equal(user)
      verify(twitterInstance).lookupUsers(Array(10L))
    }

  }

  private trait scope {
    implicit val timeout: Timeout = 5 second

    val twitterInstance = mock(classOf[Twitter])
    val responseList = mock(classOf[ResponseList[User]])
    val user = mock(classOf[User])

    when(responseList.iterator()).thenReturn(List(user).iterator)
    when(twitterInstance.lookupUsers(Array(10L))).thenReturn(responseList)

    val userProfileActor = TestActorRef(new UserProfileActor(twitterInstance))
  }

}