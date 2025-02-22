package forms

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class LoginData(username: String, password: String)

object LoginData {
  def unapply(u: LoginData): Option[(String, String)] = Some((u.username, u.password))
}

object LoginForm {
  val form: Form[LoginData] = Form(
    mapping(
      "username" -> nonEmptyText(3, 20),
      "password" -> nonEmptyText(6, 50)
    )(LoginData.apply)(LoginData.unapply)
  )
}
