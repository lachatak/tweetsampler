package util

import twitter4j.conf.ConfigurationBuilder
import twitter4j.{TwitterStream, TwitterStreamFactory}
import play.api.Play.current

object TwitterClient {

println(System.getenv())
println(current.configuration.entrySet)

  val appKey: String = current.configuration.getString("appKey").get
  val appSecret: String = current.configuration.getString("appSecret").get
  val accessToken: String = current.configuration.getString("accessToken").get
  val accessTokenSecret: String = current.configuration.getString("accessTokenSecret").get

  lazy val instance = apply()

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
