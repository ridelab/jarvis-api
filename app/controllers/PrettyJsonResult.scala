package controllers

import akka.util._
import play.api.http._
import play.api.libs.json._

trait PrettyJsonResult {

  implicit def prettyWriteJson: Writeable[JsValue] =
    Writeable(x => ByteString(Json prettyPrint x))

}
