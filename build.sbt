organization := "io.jarvis"
name := "jarvis-api"
version := "0.0.0-SNAPSHOT"

scalaVersion := "2.12.3"
scalacOptions ++= Seq("-deprecation")

lazy val root = (project in file(".")) enablePlugins PlayScala

libraryDependencies ++= Seq(
  ws,
  guice,
  "org.scala-lang.modules" %% "scala-async" % "0.9.7",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.1" % Test
)
