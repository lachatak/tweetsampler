package util

import com.typesafe.config.ConfigFactory
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{TwitterStream, TwitterStreamFactory}

object TwitterClient {

  val twitterConf = ConfigFactory.load("twitter.conf")

  println(System.getenv())
  println(twitterConf)

  val appKey: String = twitterConf.getString("appKey")
  val appSecret: String = twitterConf.getString("appSecret")
  val accessToken: String = twitterConf.getString("accessToken")
  val accessTokenSecret: String = twitterConf.getString("accessTokenSecret")

  def apply(): TwitterStream = {
    val cb = new ConfigurationBuilder()
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey(appKey)
      .setOAuthConsumerSecret(appSecret)
      .setOAuthAccessToken(accessToken)
      .setOAuthAccessTokenSecret(accessTokenSecret)
    val tsf = new TwitterStreamFactory(cb.build())
    tsf.getInstance()
  }
}
