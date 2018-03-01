import models.Tables
import org.mindrot.jbcrypt.BCrypt
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object UserGenerator extends App {

  if (args.length < 2) {
    throw new IllegalArgumentException()
  }

  val username = args(0)
  val password = args(1)

  val salt = BCrypt.gensalt()
  val hashedPass = BCrypt.hashpw(password, salt)
  val db = Database.forConfig("db")

  var users = TableQuery[Tables.Users]

  val action = DBIO.seq(
    users.map(u => (u.name, u.password)) += (username, hashedPass)
  )

  Await.result(db.run(action), Duration.Inf)
}
