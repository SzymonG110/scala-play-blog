package forms

import play.api.data.Forms._
import play.api.data.Form

case class LoginForm(username: String, password: String)

object LoginForm {
  val form: Form[LoginForm] = Form(
    mapping(
      "username" -> nonEmptyText(3, 20),
      "password" -> nonEmptyText(6, 50)
    )(LoginForm.apply)(LoginForm.unapply)
  )
}