package services

import javax.inject._

import entities._
import play.api._
import play.api.libs.ws._

import scala.async.Async._
import scala.concurrent._

/**
  * douban movie: `https://developers.douban.com/wiki/?title=movie_v2`
  */
@Singleton
class DoubanApiService @Inject()(
    ws: WSClient,
)(implicit executor: ExecutionContext) {

  private val baseUrl = "https://api.douban.com"

  def searchMovie(
      query: String,
      tag: String = "",
      start: Int = 0,
      count: Int = 20,
  ): Future[Seq[Movie]] = async {
    val url = s"$baseUrl/v2/movie/search"
    val parameters = Seq(
      "q" -> query,
      "tag" -> tag,
      "start" -> start.toString,
      "count" -> count.toString,
    )
    val response = await(
      ws url url
        addQueryStringParameters (parameters: _*)
        get ()
    )
    val result = response.json
    val total = (result \ "total").as[Int]
    val movies = (result \ "subjects").as[Seq[Movie]]
    Logger trace s"douban movie / total = $total, movies = $movies"
    movies
  }

}
