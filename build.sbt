ThisBuild / scalaVersion := "3.6.3"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """blog""",
    libraryDependencies ++= Seq(
      guice,

      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,

      "com.typesafe.play" %% "play-json" % "2.10.6",

      "org.mindrot" % "jbcrypt" % "0.4",

      jdbc,
      "org.postgresql" % "postgresql" % "42.7.5"
    )
  )