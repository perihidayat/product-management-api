name := """product-management-api"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.5" // or "2.10.4"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.scaldi" %% "scaldi-play" % "0.5.15",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "org.xerial" % "sqlite-jdbc" % "3.8.11.2",
  "org.postgresql" % "postgresql" % "9.4.1208"
)

fork in Test := false

lazy val root = (project in file(".")).enablePlugins(PlayScala)