package util

import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import com.danielasfregola.twitter4s.{TwitterRestClient, TwitterStreamingClient}
import com.typesafe.config.ConfigFactory

object TwitterClient {

  private val twitterConf = ConfigFactory.load("twitter.conf")

  private val appKey: String = twitterConf.getString("appKey")
  private val appSecret: String = twitterConf.getString("appSecret")
  private val accessKey: String = twitterConf.getString("accessToken")
  private val accessSecret: String = twitterConf.getString("accessTokenSecret")

  val consumerToken = ConsumerToken(key = appKey, secret = appSecret)
  val accessToken = AccessToken(key = accessKey, secret = accessSecret)

  lazy val twitterStreamInstance: TwitterStreamingClient = new TwitterStreamingClient(consumerToken, accessToken)

  lazy val twitterInstance: TwitterRestClient = new TwitterRestClient(consumerToken, accessToken)

}
