import sbt._
import Keys._
import com.github.retronym.SbtOneJar._

object ValintatulosEmailerBuild extends Build {
  val Organization = "fi.vm.sade"
  val Name = "valinta-tulos-emailer"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.4"

  lazy val project = Project(
    "valinta-tulos-emailer",
    file("."),
    settings = Defaults.coreDefaultSettings ++ oneJarSettings ++
     Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      libraryDependencies ++= Seq(
        "org.json4s" %% "json4s-jackson" % "3.2.10",
        "org.json4s" %% "json4s-ext" % "3.2.10",
        "org.scalaj" %% "scalaj-http" % "0.3.15",
        "org.scalatra.scalate" %% "scalate-core" % "1.7.0",
        "fi.vm.sade" %% "scala-security" % "0.1.0-SNAPSHOT",
        "com.typesafe" % "config" % "1.2.1",
        "org.slf4j" % "slf4j-log4j12" % "1.7.7",
        "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.4.1",
        "junit" % "junit" % "4.11" % "test"
      ),
      mainClass in oneJar := Some("fi.vm.sade.vt.emailer.Main"),
      resolvers += Classpaths.typesafeReleases,
      resolvers += "oph-sade-artifactory-releases" at "https://artifactory.oph.ware.fi/artifactory/oph-sade-release-local",
      resolvers += "oph-sade-artifactory-snapshots" at "https://artifactory.oph.ware.fi/artifactory/oph-sade-snapshot-local",
      parallelExecution in Test := false,
      testOptions in Test := Seq(Tests.Filter(s => s.endsWith("Test") || s.endsWith("Spec"))),
      testOptions in Test += Tests.Argument("junitxml", "console")
    )
  )
}