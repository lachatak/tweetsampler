package app.actors

import java.util.Date

import actors.{Filter, Tweet, WordOccurrence, WordOccurrences}
import org.joda.time.DateTime
import play.api.libs.json.Json
import twitter4j.{RateLimitStatus, Status, URLEntity, User}

object Fixtures {

  val validFilterJsonMessage = Json.obj("filter" -> "test test2")
  val invalidFilterJsonMessage = Json.obj("invalid" -> "test test2")

  val validFilterList = List("test", "test2")
  val validFilterMessage = Filter(validFilterList)
  val validTweetId = 1L
  val validTweetText = "text"

  def validWordOccurrence(word: String = "word", date: Long = DateTime.now().getMillis, count: Int = 1) = WordOccurrence(word, date, count)

  def validWordOccurrences(statType: String = "test", words: List[WordOccurrence] = List(validWordOccurrence())) = WordOccurrences(statType, words)

  def validTweet(id: Long = validTweetId, date: Long = DateTime.now().getMillis, user: User = new StubUser(), tweet: String = "tweet") = Tweet(id, date, user, tweet)

  class StubUser extends User {

    override def getId: Long = 1

    override def getBiggerProfileImageURL: String = ???

    override def isProtected: Boolean = ???

    override def isTranslator: Boolean = ???

    override def getProfileLinkColor: String = ???

    override def getProfileImageURL: String = ???

    override def getProfileBannerIPadRetinaURL: String = ???

    override def getMiniProfileImageURLHttps: String = ???

    override def getProfileSidebarFillColor: String = ???

    override def getScreenName: String = ???

    override def getListedCount: Int = ???

    override def getOriginalProfileImageURLHttps: String = ???

    override def isProfileBackgroundTiled: Boolean = ???

    override def isProfileUseBackgroundImage: Boolean = ???

    override def getUtcOffset: Int = ???

    override def getProfileSidebarBorderColor: String = ???

    override def isContributorsEnabled: Boolean = ???

    override def getTimeZone: String = ???

    override def getName: String = ???

    override def getCreatedAt: Date = ???

    override def getDescriptionURLEntities: Array[URLEntity] = ???

    override def getWithheldInCountries: Array[String] = ???

    override def getURL: String = ???

    override def getLang: String = ???

    override def getProfileImageURLHttps: String = ???

    override def getStatus: Status = ???

    override def isDefaultProfileImage: Boolean = ???

    override def getMiniProfileImageURL: String = ???

    override def isDefaultProfile: Boolean = ???

    override def getDescription: String = ???

    override def getProfileBannerRetinaURL: String = ???

    override def getFollowersCount: Int = ???

    override def isGeoEnabled: Boolean = ???

    override def getURLEntity: URLEntity = ???

    override def getProfileBackgroundColor: String = ???

    override def isFollowRequestSent: Boolean = ???

    override def getProfileBannerMobileURL: String = ???

    override def getFavouritesCount: Int = ???

    override def getProfileBannerURL: String = ???

    override def getProfileBackgroundImageUrlHttps: String = ???

    override def getProfileBackgroundImageURL: String = ???

    override def isVerified: Boolean = ???

    override def getLocation: String = ???

    override def getFriendsCount: Int = ???

    override def getProfileBannerMobileRetinaURL: String = ???

    override def getProfileTextColor: String = ???

    override def getStatusesCount: Int = ???

    override def isShowAllInlineMedia: Boolean = ???

    override def getProfileBannerIPadURL: String = ???

    override def getOriginalProfileImageURL: String = "http://image"

    override def getBiggerProfileImageURLHttps: String = ???

    override def getAccessLevel: Int = ???

    override def getRateLimitStatus: RateLimitStatus = ???

    override def compareTo(o: User): Int = ???
  }

}
