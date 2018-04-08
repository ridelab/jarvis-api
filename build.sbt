organization := "io.es"
name := "onenet-rc"
version := "0.0.0"

scalaVersion := "2.12.5"
scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked")

lazy val root = (project in file(".")) enablePlugins PlayScala

libraryDependencies ++= Seq(
  guice,
)
