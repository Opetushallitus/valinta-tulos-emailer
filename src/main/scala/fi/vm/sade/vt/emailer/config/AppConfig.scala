package fi.vm.sade.vt.emailer.config

import fi.vm.sade.vt.emailer.Logging

object AppConfig extends Logging {
  def getProfileProperty() = System.getProperty("vtemailer.profile", "default")

  def fromOptionalString(profile: Option[String]) = {
    fromString(profile.getOrElse(getProfileProperty))
  }

  def fromSystemProperty: AppConfig = {
    fromString(getProfileProperty)
  }

  def fromString(profile: String) = {
    logger.info("Using vtemailer.profile=" + profile)
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
  class Default extends AppConfig with ExternalProps

  /**
   * Templated profile, uses config template with vars file located by system property vtemailer.vars
   */
  class LocalTestingWithTemplatedVars(val templateAttributesFile: String = System.getProperty("vtemailer.vars")) extends AppConfig with TemplatedProps

  /**
   * Dev profile
   */
  class Dev extends AppConfig with ExampleTemplatedProps

  /**
   *  IT (integration test) profiles. Uses embedded mongo database and stubbed external deps
   */
  class IT extends ExampleTemplatedProps with StubbedExternalDeps

  trait ExternalProps {
    def configFile = System.getProperty("user.home") + "/oph-configuration/valinta-tulos-emailer.properties"
    lazy val settings = ApplicationSettings.loadSettings(configFile)
  }

  trait ExampleTemplatedProps extends AppConfig with TemplatedProps {
    def templateAttributesFile = "src/main/resources/oph-configuration/dev-vars.yml"
  }

  trait TemplatedProps {
    logger.info("Using template variables from " + templateAttributesFile)
    lazy val settings = loadSettings
    def loadSettings = ConfigTemplateProcessor.createSettings(templateAttributesFile)
    def templateAttributesFile: String
  }

  trait StubbedExternalDeps

  trait AppConfig {
    def start {}
    def stop {}

    def settings: ApplicationSettings

    def properties: Map[String, String] = settings.toProperties
  }

}
