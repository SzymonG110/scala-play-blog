package controllers

import javax.inject._
import models.User
import play.api.mvc._
import play.api.libs.json._
import repositories.UserRepository
import utils.{JwtHelper, PasswordHasher}

@Singleton
class AuthController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  implicit val userFormat: OFormat[User] = Json.format[User]

  def register: Action[JsValue] = Action(parse.json) { request =>
    val username = (request.body \ "username").as[String]
    val password = (request.body \ "password").as[String]

    if (UserRepository.findByUsername(username).isDefined) {
      Conflict(Json.obj("error" -> "User already exists"))
    } else {
      val hashedPassword = PasswordHasher.hashPassword(password)
      val user = User(System.currentTimeMillis(), username, hashedPassword)
      UserRepository.save(user)
      Created(Json.obj("message" -> "User registered successfully"))
    }
  }

  def login: Action[JsValue] = Action(parse.json) { request =>
    val username = (request.body \ "username").as[String]
    val password = (request.body \ "password").as[String]

    UserRepository.findByUsername(username) match {
      case Some(user) if PasswordHasher.checkPassword(password, user.password) =>
        val token = JwtHelper.createToken(username)
        Ok(Json.obj("token" -> token))
      case _ => Unauthorized(Json.obj("error" -> "Invalid credentials"))
    }
  }
}
