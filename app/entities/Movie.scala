package entities

import entities.Movie._
import play.api.libs.json._

case class Movie(
    id: String,
    title: String,
    rating: Rating,
    genres: Seq[String],
    casts: Seq[Celebrity],
    directors: Seq[Celebrity],
    images: Images,
    year: String,
    subtype: String,
    alt: String,
)

object Movie {

  implicit val jsonFormatForImages: OFormat[Images] = Json.format[Images]
  implicit val jsonFormatForRating: OFormat[Rating] = Json.format[Rating]
  implicit val jsonFormatForCelebrity: OFormat[Celebrity] = Json.format[Celebrity]
  implicit val jsonFormat: OFormat[Movie] = Json.format[Movie]

  case class Images(small: String, large: String, medium: String)

  case class Rating(average: Double, stars: String, min: Int, max: Int)

  case class Celebrity(id: Option[String], name: String, avatars: Option[Images], alt: Option[String])

}
