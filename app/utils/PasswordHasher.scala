package utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
  def hashPassword(password: String): String =
    BCrypt.hashpw(password, BCrypt.gensalt())

  def checkPassword(password: String, hashed: String): Boolean =
    BCrypt.checkpw(password, hashed)
}
