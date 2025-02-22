package controllers

import javax.inject._
import models.User
import play.api.mvc._
import repositories.UserRepository
import utils.PasswordHasher
import forms.LoginForm

@Singleton
class AuthController @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {
  def registerPage(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.register(LoginForm.form))
  }

  def register: Action[AnyContent] = Action { implicit request =>
    LoginForm.form.bindFromRequest.fold(
      registerFormError => {
        BadRequest(views.html.register(registerFormError))
      },

      registerForm => {
        val userOpt = UserRepository.findByUsername(registerForm.username)
        if (userOpt.isDefined) {
          Redirect(routes.AuthController.registerPage).flashing("error" -> "User already exists")
        } else {
          val passwordHash = PasswordHasher.hash(registerForm.password)
          val user = User(0, registerForm.username, passwordHash)
          UserRepository.create(user)
          Redirect(routes.HomeController.index()).withSession("username" -> user.username)
        }
      }
    )
  }


  def loginPage(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.login(LoginForm.form))
  }

  def login: Action[AnyContent] = Action { implicit request =>
    LoginForm.form.bindFromRequest.fold(
      loginFormError => {
        BadRequest(views.html.login(loginFormError))
      },

      loginForm => {
        val userOpt = UserRepository.findByUsername(loginForm.username)
        if (userOpt.isEmpty) {
          Redirect(routes.AuthController.loginPage).flashing("error" -> "Invalid login or password")
        } else {
          val user = userOpt.get
          if (!PasswordHasher.check(loginForm.password, user.password)) {
            Redirect(routes.AuthController.loginPage).flashing("error" -> "Invalid login or password")
          } else {
            Redirect(routes.HomeController.index()).withSession("username" -> user.username)
          }
        }
      }
    )
  }

  def logoutPage: Action[AnyContent] = Action { _ =>
    Redirect(routes.AuthController.loginPage).withNewSession
  }
}