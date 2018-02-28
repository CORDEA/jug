import models.Tables
import org.mindrot.jbcrypt.BCrypt
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object UserGenerator extends App {

  val username = "username"
  val password = "password"

  val salt = BCrypt.gensalt()
  val hashedPass = BCrypt.hashpw(password, salt)
  val db = Database.forConfig("db")

  var users = TableQuery[Tables.Users]

  val action = DBIO.seq(
    users.map(u => (u.name, u.password)) += (username, hashedPass)
  )

  Await.result(db.run(action), Duration.Inf)
}
