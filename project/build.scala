import sbt._
import Keys._
import com.github.retronym.SbtOneJar._

object ValintatulosEmailerBuild extends Build {
  val Organization = "fi.vm.sade"
  val Name = "valinta-tulos-emailer"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.4"
  val ScalatraVersion = "2.3.0"

  lazy val project = Project(
    "valinta-tulos-emailer",
    file("."),
    settings = Defaults.coreDefaultSettings ++ oneJarSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      libraryDependencies ++= Seq(
        "com.github.scopt" %% "scopt" % "3.3.0",
        "fi.vm.sade" %% "scala-group-emailer" % "0.1.0",
        "org.slf4j" % "slf4j-log4j12" % "1.7.7",
        "junit" % "junit" % "4.11" % "test",
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test" excludeAll(
            ExclusionRule(organization = "org.specs2")
        ),
        "org.scalatra" %% "scalatra-json" % ScalatraVersion % "test",
        "org.specs2" %% "specs2" % "2.4.16" % "test" excludeAll(
            ExclusionRule(organization = "org.scala-lang.modules")
        )
      ),
      mainClass in oneJar := Some("fi.vm.sade.vt.emailer.Main"),
      artifact in oneJar <<= moduleName(Artifact(_)),
      artifact in oneJar ~= { (art: Artifact) =>
       art.copy(name = art.name + "-complete", `type` = "jar", extension = "jar")
      },
      resolvers += Classpaths.typesafeReleases,
      resolvers += "oph-sade-artifactory-releases" at "https://artifactory.oph.ware.fi/artifactory/oph-sade-release-local",
      resolvers += "oph-sade-artifactory-snapshots" at "https://artifactory.oph.ware.fi/artifactory/oph-sade-snapshot-local",
      testFrameworks := Seq(TestFrameworks.Specs2),
      parallelExecution in Test := false,
      testOptions in Test := Seq(Tests.Filter(s => s.endsWith("Test") || s.endsWith("Spec"))),
      testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "junitxml", "console")
    )
  )
  .disablePlugins(plugins.JUnitXmlReportPlugin)
}