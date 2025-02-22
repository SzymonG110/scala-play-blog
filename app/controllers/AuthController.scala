package controllers

import javax.inject._
import models.User
import play.api.mvc._
import play.api.db.Database
import repositories.UserRepository
import utils.PasswordHasher
import forms.loginForm

@Singleton
class AuthController @Inject()(cc: MessagesControllerComponents, db: Database, userRepository: UserRepository)
  extends MessagesAbstractController(cc) {

  def registerPage(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.register(loginForm))
  }

  def register: Action[AnyContent] = Action { implicit request =>
    loginForm.bindFromRequest().fold(
      registerFormError => {
        BadRequest(views.html.register(registerFormError))
      },

      registerForm => {
        val userOpt = userRepository.findByUsername(registerForm.username)
        if (userOpt.isDefined) {
          Redirect(routes.AuthController.registerPage()).flashing("error" -> "User already exists")
        } else {
          val passwordHash = PasswordHasher.hash(registerForm.password)
          val user = User(registerForm.username, passwordHash)
          userRepository.create(user)
          Redirect(routes.HomeController.index()).withSession("username" -> user.username)
        }
      }
    )
  }

  def loginPage(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def login: Action[AnyContent] = Action { implicit request =>
    loginForm.bindFromRequest().fold(
      loginFormError => {
        BadRequest(views.html.login(loginFormError))
      },

      loginForm => {
        val userOpt = userRepository.findByUsername(loginForm.username)
        if (userOpt.isEmpty) {
          Redirect(routes.AuthController.loginPage()).flashing("error" -> "Invalid login or password")
        } else {
          val user = userOpt.get
          if (!PasswordHasher.check(loginForm.password, user.password)) {
            Redirect(routes.AuthController.loginPage()).flashing("error" -> "Invalid login or password")
          } else {
            Redirect(routes.HomeController.index()).withSession("username" -> user.username)
          }
        }
      }
    )
  }

  def logoutPage: Action[AnyContent] = Action { _ =>
    Redirect(routes.AuthController.loginPage()).withNewSession
  }
}
