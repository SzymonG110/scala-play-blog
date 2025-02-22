package repositories

import models.User
import play.api.db._
import javax.inject._
import java.sql.Connection

@Singleton
class UserRepository @Inject()(db: Database) {

  def findByUsername(username: String): Option[User] = {
    val connection: Connection = db.getConnection()
    try {
      val stmt = connection.prepareStatement("SELECT username, password FROM users WHERE username = ?")
      stmt.setString(1, username)
      val rs = stmt.executeQuery()

      if (rs.next()) {
        Some(User(rs.getString("username"), rs.getString("password")))
      } else {
        None
      }
    } finally {
      connection.close()
    }
  }

  def create(user: User): Unit = {
    val connection: Connection = db.getConnection()
    try {
      val stmt = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")
      stmt.setString(1, user.username)
      stmt.setString(2, user.password)
      stmt.executeUpdate()
    } finally {
      connection.close()
    }
  }
}
