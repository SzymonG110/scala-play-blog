package controllers

import forms.LoginForm

import javax.inject._
import models.User
import play.api.i18n.{MessagesApi, MessagesProvider}
import play.api.mvc._
import play.api.libs.json._
import repositories.UserRepository
import utils.{JwtHelper, PasswordHasher}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthController @Inject()(cc: ControllerComponents, override val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends AbstractController(cc) with LegacyI18nSupport {
  implicit val userFormat: OFormat[User] = Json.format[User]

  def registerPage(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    implicit val messages: MessagesProvider = messagesApi.preferred(request)
    Ok(views.html.register(LoginForm.loginForm))
  }

  def register: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    LoginForm.loginForm.bindFromRequest.fold(
      formWithErrors => {
        implicit val messages: MessagesProvider = messagesApi.preferred(request)
        Future.successful(BadRequest(views.html.register(formWithErrors)))
      },
      loginData => {
        if (UserRepository.findByUsername(loginData.username).isDefined) {
          Future.successful(Conflict(Json.obj("error" -> "User already exists")))
        } else {
          val hashedPassword = PasswordHasher.hashPassword(loginData.password)
          val user = User(System.currentTimeMillis(), loginData.username, hashedPassword)
          UserRepository.save(user)
          Future.successful(Created(Json.obj("message" -> "User registered successfully")))
        }
      }
    )
  }

  def loginPage(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    implicit val messages: MessagesProvider = messagesApi.preferred(request)
    Ok(views.html.login(LoginForm.loginForm))
  }

  def login: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    LoginForm.loginForm.bindFromRequest.fold(
      formWithErrors => {
        implicit val messages: MessagesProvider = messagesApi.preferred(request)
        Future.successful(BadRequest(views.html.login(formWithErrors)))
      },
      loginData => {
        Future {
          UserRepository.findByUsername(loginData.username) match {
            case Some(user) if PasswordHasher.checkPassword(loginData.password, user.password) =>
              val token = JwtHelper.createToken(loginData.username)
              Ok(Json.obj("token" -> token))
            case _ => Unauthorized(Json.obj("error" -> "Invalid credentials"))
          }
        }
      }
    )
  }
}