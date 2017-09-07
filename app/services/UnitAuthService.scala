package services

import javax.inject._

import play.api._
import play.api.libs.ws._

import scala.async.Async._
import scala.concurrent._
import scala.concurrent.duration._

/**
  * auth: `https://ai.baidu.com/docs#/auth/top`
  */
@Singleton
class UnitAuthService @Inject()(
    ws: WSClient,
    config: Configuration,
)(implicit executor: ExecutionContext) {

  private val apiKey = config.get[String]("unit.api-key")
  private val secretKey = config.get[String]("unit.secret-key")

  // TODO
  val accessToken: String = (
    config.getOptional[String]("unit.access-token")
      getOrElse (Await result (fetchAccessToken, 10.seconds))
  )

  private def fetchAccessToken: Future[String] = async {
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
    val expires = (result \ "expires_in").as[Long]
    Logger debug s"access-token = '$accessToken', expires = $expires"
    accessToken
  }

}
