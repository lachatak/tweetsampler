package util

import twitter4j.conf.ConfigurationBuilder
import twitter4j.{TwitterStream, TwitterStreamFactory}
import play.api.Play.current

object TwitterClient {

println(System.getenv())
println(System.getProperties)

  val appKey: String =  System.getenv("appKey")
  val appSecret: String =  System.getenv("appSecret")
  val accessToken: String =  System.getenv("accessToken")
  val accessTokenSecret: String =  System.getenv("accessTokenSecret")

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
