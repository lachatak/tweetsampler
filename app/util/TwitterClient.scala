package util

import com.typesafe.config.ConfigFactory
import twitter4j._
import twitter4j.conf.ConfigurationBuilder

object TwitterClient {

  private val twitterConf = ConfigFactory.load("twitter.conf")

  private val appKey: String = twitterConf.getString("appKey")
  private val appSecret: String = twitterConf.getString("appSecret")
  private val accessToken: String = twitterConf.getString("accessToken")
  private val accessTokenSecret: String = twitterConf.getString("accessTokenSecret")

  private val configuration = new ConfigurationBuilder()
    .setDebugEnabled(true)
    .setOAuthConsumerKey(appKey)
    .setOAuthConsumerSecret(appSecret)
    .setOAuthAccessToken(accessToken)
    .setOAuthAccessTokenSecret(accessTokenSecret)
    .build()

  lazy val twitterStreamInstance: TwitterStream = {
    val tsf = new TwitterStreamFactory(configuration)
    tsf.getInstance()
  }

  lazy val twitterInstance: Twitter = {
    val tf = new TwitterFactory(configuration)
    tf.getInstance()
  }
}
