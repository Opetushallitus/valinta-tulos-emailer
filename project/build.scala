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
        "junit" % "junit" % "4.11" % "test",
        "com.typesafe" % "config" % "1.2.1",
        "org.json4s" %% "json4s-jackson" % "3.2.10",
        "org.json4s" %% "json4s-ext" % "3.2.10",
        "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.4.1",
        "fi.vm.sade" %% "scala-security" % "0.1.0-SNAPSHOT"
      ),
      mainClass in oneJar := Some("fi.vm.sade.vt.emailer.Main"),
      resolvers += Classpaths.typesafeReleases,
      resolvers += "oph-sade-artifactory-releases" at "http://penaali.hard.ware.fi/artifactory/oph-sade-release-local",
      resolvers += "oph-sade-artifactory-snapshots" at "http://penaali.hard.ware.fi/artifactory/oph-sade-snapshot-local",
      parallelExecution in Test := false,
      testOptions in Test := Seq(Tests.Filter(s => s.endsWith("Test") || s.endsWith("Spec"))),
      testOptions in Test += Tests.Argument("junitxml", "console")
    )
  )
}