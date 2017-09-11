package services

import javax.inject._

import play.api._

@Singleton
class WeixinVerifyTokenService @Inject()(
    config: Configuration,
) {

  private val token = config.get[String]("weixin.token")

  def verifyToken(parameters: Map[String, String]): Option[String] =
    for {
      signature <- parameters get "signature"
      timestamp <- parameters get "timestamp"
      nonce <- parameters get "nonce"
      echo <- parameters get "echostr"
      hashcode = sha1(Seq(token, timestamp, nonce).sorted.mkString)
      if hashcode == signature
    } yield echo

  private def sha1(input: String) = {
    import java.security.MessageDigest
    val processor = MessageDigest getInstance "SHA-1"
    val bytes = processor digest input.getBytes
    bytes map ("%02x" format _) mkString ""
  }

}
