package utils

import io.github.cdimascio.dotenv.Dotenv
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import play.api.libs.json.Json
import java.time.Clock
import scala.util.{Failure, Success}

object JwtHelper {
  private val dotenv = Dotenv.load()
  private val secretKey = dotenv.get("JWT_SECRET_KEY")
  private val algorithm = JwtAlgorithm.HS256

  def createToken(username: String): String = {
    val clock: Clock = Clock.systemUTC()

    val claim = JwtClaim(
      Json.obj("username" -> username).toString()
    ).issuedNow(clock).expiresIn(3600)(clock)

    Jwt.encode(claim, secretKey, algorithm)
  }

  def validateToken(token: String): Option[String] = {
    Jwt.decode(token, secretKey, Seq(algorithm)) match {
      case Success(claim) =>
        (Json.parse(claim.content) \ "username").asOpt[String]
      case Failure(_) => None
    }
  }
}
