import com.typesafe.config.ConfigFactory
import slick.codegen.SourceCodeGenerator

object ModelGenerator extends App {

  val config = ConfigFactory.load

  SourceCodeGenerator.main(
    Array(
      config.getString("db.slick.driver"),
      config.getString("db.driver"),
      config.getString("db.url"),
      "src/main/scala",
      "models",
      config.getString("db.user"),
      config.getString("db.password")
    )
  )
}
