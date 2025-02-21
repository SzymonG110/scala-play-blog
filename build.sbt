ThisBuild / scalaVersion := "2.13.16"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """test""",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,

      "com.typesafe.play" %% "play-json" % "2.10.5",
      "org.mindrot" % "jbcrypt" % "0.4",
      "com.github.jwt-scala" %% "jwt-play" % "10.0.1",
      "io.github.cdimascio" % "java-dotenv" % "5.2.2"
    )
  )