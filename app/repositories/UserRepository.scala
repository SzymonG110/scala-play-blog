package repositories

import models.User
import scala.collection.mutable

object UserRepository {
  private val users = mutable.Map[String, User]()

  def findByUsername(username: String): Option[User] =
    users.get(username)

  def save(user: User): Unit =
    users.put(user.username, user)
}
