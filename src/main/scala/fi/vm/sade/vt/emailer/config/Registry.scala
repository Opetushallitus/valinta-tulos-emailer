package fi.vm.sade.vt.emailer.config

import java.io.FileInputStream
import java.util.Properties

import fi.vm.sade.vt.emailer.util.{Logging, ValintatulosServiceRunner}
import org.apache.log4j.PropertyConfigurator

object Registry {
  def getProfileProperty() = System.getProperty("vtemailer.profile", "default")

  def fromOptionalString(profile: Option[String]) = {
    fromString(profile.getOrElse(getProfileProperty))
  }

  def fromSystemProperty: Registry = {
    fromString(getProfileProperty)
  }

  def fromString(profile: String) = {
    println("Using vtemailer.profile=" + profile)
    profile match {
      case "default" => new Default
      case "templated" => new LocalTestingWithTemplatedVars
      case "dev" => new Dev
      case "it" => new IT
      case name => throw new IllegalArgumentException("Unknown value for vtemailer.profile: " + name);
    }
  }

  /**
   * Default profile, uses ~/oph-configuration/valinta-tulos-emailer.properties
   */
  class Default extends Registry with ExternalProps

  /**
   * Templated profile, uses config template with vars file located by system property vtemailer.vars
   */
  class LocalTestingWithTemplatedVars(val templateAttributesFile: String = System.getProperty("vtemailer.vars")) extends Registry with TemplatedProps

  /**
   * Dev profile
   */
  class Dev extends Registry with ExampleTemplatedProps {
    override def start {
      ValintatulosServiceRunner.start
    }
  }

  /**
   *  IT (integration test) profiles. Uses embedded mongo database and stubbed external deps
   */
  class IT extends ExampleTemplatedProps with StubbedExternalDeps {
    override def start {
      ValintatulosServiceRunner.start
    }

    def lastEmailSize() = groupEmailService match {
      case x: FakeGroupEmailService => x.getLastEmailSize()
      case _ => new IllegalAccessError("getLastEmailSize error")
    }

    override lazy val settings = ConfigTemplateProcessor.createSettings(templateAttributesFile)
      .withOverride("valinta-tulos-service.vastaanottoposti.url",
        "http://localhost:"+ ValintatulosServiceRunner.valintatulosPort + "/valinta-tulos-service/vastaanottoposti")
  }

  trait ExternalProps {
    def configFile = System.getProperty("user.home") + "/oph-configuration/valinta-tulos-emailer.properties"
    lazy val settings = ApplicationSettings.loadSettings(configFile)
    def log4jconfigFile = System.getProperty("user.home") + "/oph-configuration/log4j.properties"
    val log4jproperties = new Properties()
    log4jproperties.load(new FileInputStream(log4jconfigFile))
    PropertyConfigurator.configure(log4jproperties)
  }

  trait ExampleTemplatedProps extends Registry with TemplatedProps {
    def templateAttributesFile = "src/main/resources/oph-configuration/dev-vars.yml"
  }

  trait TemplatedProps {
    println("Using template variables from " + templateAttributesFile)
    lazy val settings = loadSettings
    def loadSettings = ConfigTemplateProcessor.createSettings(templateAttributesFile)
    def templateAttributesFile: String
  }

  trait StubbedExternalDeps

  trait Registry extends Components {
    val settings: ApplicationSettings
  }

}
