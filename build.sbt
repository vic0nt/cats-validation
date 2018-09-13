name := "cats-validator-06"

version := "0.1"

scalaVersion := "2.12.6"

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % versions.CatsVersion,
      "io.circe" %% "circe-core"    % versions.circeVersion,
      "io.circe" %% "circe-generic" % versions.circeVersion,
      "io.circe" %% "circe-parser"  % versions.circeVersion,
      "com.lambdista" %% "money" % "0.6.2",

      "org.scalatest" %% "scalatest" % versions.ScalaTestVersion % Test
    )
  )

lazy val commonSettings =
  Seq(
    scalacOptions ++= Seq("-Ypartial-unification")
  )

lazy val versions = new {
  val CatsVersion       = "1.2.0"
  val ScalaTestVersion  = "3.0.5"
  val circeVersion      = "0.9.3"
}

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
