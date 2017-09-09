package services

import javax.inject._

import akka.actor._
import play.api._
import play.api.cache._
import play.api.libs.ws._

import scala.async.Async._
import scala.concurrent._
import scala.concurrent.duration._

/**
  * auth: `https://ai.baidu.com/docs#/auth/top`
  * cache: `https://playframework.com/documentation/latest/ScalaCache`
  */
@Singleton
class UnitAuthService @Inject()(
    ws: WSClient,
    system: ActorSystem,
    cache: AsyncCacheApi,
    config: Configuration,
)(implicit executor: ExecutionContext) {

  private val apiKey = config.get[String]("unit.api-key")
  private val secretKey = config.get[String]("unit.secret-key")
  private val accessTokenCacheKey = "unit.access-token"
  private val accessTokenCacheExpiration = 20.days
  private val accessTokenUpdateInterval = 10.days

  config.getOptional[String](accessTokenCacheKey) match {
    case Some(accessToken) => setAccessTokenCache(accessToken)
    case None              => updateAccessTokenCache()
  }

  system.scheduler.schedule(accessTokenUpdateInterval, accessTokenUpdateInterval)(updateAccessTokenCache())

  def accessToken: Future[String] =
    cache.getOrElseUpdate[String](accessTokenCacheKey, accessTokenCacheExpiration)(fetchAccessToken())

  private def updateAccessTokenCache() =
    fetchAccessToken() flatMap setAccessTokenCache

  private def setAccessTokenCache(accessToken: String) =
    cache set (accessTokenCacheKey, accessToken, accessTokenCacheExpiration)

  private def fetchAccessToken(): Future[String] = async {
    val url = "https://aip.baidubce.com/oauth/2.0/token"
    val parameters = Seq(
      "grant_type" -> "client_credentials",
      "client_id" -> apiKey,
      "client_secret" -> secretKey,
    )
    val response = await(
      ws url url
        addQueryStringParameters (parameters: _*)
        get ()
    )
    val result = response.json
    val accessToken = (result \ "access_token").as[String]
    val expiration = (result \ "expires_in").as[Long]
    Logger debug s"access-token = '$accessToken', expiration = $expiration seconds"
    accessToken
  }

}
