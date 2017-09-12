package filters

import javax.inject._

import akka.stream._
import play.api.http._
import play.api.mvc._

import scala.concurrent._

@Singleton
class ContentTypeFilter @Inject()(implicit val mat: Materializer) extends Filter {

  override def apply(next: RequestHeader => Future[Result])(header: RequestHeader): Future[Result] = {
    val replacedContentType = header.contentType collect {
      case "text/xml" => "application/xml"
    }
    next(
      replacedContentType
        map (contentType => Headers(HeaderNames.CONTENT_TYPE -> contentType))
        map (header withHeaders _)
        getOrElse header
    )
  }

}
