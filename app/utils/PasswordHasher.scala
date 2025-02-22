package utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
  def hash(password: String): String =
    BCrypt.hashpw(password, BCrypt.gensalt())

  def check(password: String, hashed: String): Boolean =
    BCrypt.checkpw(password, hashed)
}
