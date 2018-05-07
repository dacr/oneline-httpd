
lazy val commonSettings = Seq(
  name := "oneline-httpd",
  organization := "fr.janalyse",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.6",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature", "-Ybackend-parallelism", "6")
)

lazy val httpd= (project in file("httpd"))
  .settings(commonSettings: _*)

lazy val loadTest = (project in file("loadTest"))
  .settings(commonSettings: _*)

