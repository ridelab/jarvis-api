organization := "io.es"
name := "onenet-rc"
version := "0.0.0"

scalaVersion := "2.12.5"
scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked")

lazy val root = (project in file(".")) enablePlugins PlayScala

libraryDependencies ++= Seq(
  ws,
  guice,
  ehcache,
  "org.scala-lang.modules" %% "scala-async" % "0.9.7",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
)
