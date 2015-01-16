package fi.vm.sade.vt.emailer.config

import java.io.FileInputStream
import java.util.Properties

import fi.vm.sade.vt.emailer.CommandLineArgs
import fi.vm.sade.utils.config.{ConfigTemplateProcessor, ApplicationSettingsLoader}
import fi.vm.sade.vt.emailer.util.ValintatulosServiceRunner
import org.apache.log4j.PropertyConfigurator

object Registry {
  def getProfileProperty() = System.getProperty("vtemailer.profile", "default")

  def fromString(profile: String, commandLineArgs: CommandLineArgs) = {
    println("Using vtemailer.profile=" + profile)
    println("Using test mode=" + commandLineArgs.test)
    profile match {
      case "default" => new Default(commandLineArgs)
      case "templated" => new LocalTestingWithTemplatedVars(commandLineArgs)
      case "dev" => new Dev(commandLineArgs)
      case "it" => new IT(commandLineArgs)
      case "localvt" => new LocalVT(commandLineArgs)
      case name => throw new IllegalArgumentException("Unknown value for vtemailer.profile: " + name)
    }
  }

  /**
   * Default profile, uses ~/oph-configuration/valinta-tulos-emailer.properties
   */
  class Default(val commandLineArgs: CommandLineArgs) extends Registry with ExternalProps

  /**
   * Templated profile, uses config template with vars file located by system property vtemailer.vars
   */
  class LocalTestingWithTemplatedVars(val commandLineArgs: CommandLineArgs, val templateAttributesFile: String = System.getProperty("vtemailer.vars")) extends Registry with TemplatedProps

  /**
   * Dev profile
   */
  class Dev(val commandLineArgs: CommandLineArgs) extends Registry with ExampleTemplatedProps {
    override def start {
      ValintatulosServiceRunner.start
    }
  }

  class LocalVT(val commandLineArgs: CommandLineArgs) extends ExampleTemplatedProps {
    override lazy val settings = ConfigTemplateProcessor.createSettings("valinta-tulos-emailer", templateAttributesFile)
      .withOverride("valinta-tulos-service.vastaanottoposti.url",
        "http://localhost:8097/valinta-tulos-service/vastaanottoposti")
  }

  /**
   *  IT (integration test) profiles. Uses embedded mongo database and stubbed external deps
   */
  class IT(val commandLineArgs: CommandLineArgs) extends ExampleTemplatedProps with StubbedExternalDeps {

    override def start {
      ValintatulosServiceRunner.start
    }

    def lastEmailSize() = groupEmailService match {
      case x: FakeGroupEmailService => x.getLastEmailSize
      case _ => new IllegalAccessError("getLastEmailSize error")
    }

    implicit val settingsParser = ApplicationSettingsParser(commandLineArgs)
    override lazy val settings = ConfigTemplateProcessor.createSettings("valinta-tulos-emailer", templateAttributesFile)
      .withOverride("valinta-tulos-service.vastaanottoposti.url",
        "http://localhost:"+ ValintatulosServiceRunner.valintatulosPort + "/valinta-tulos-service/vastaanottoposti")
  }

  trait ExternalProps extends WithCommandLineArgs {
    def configFile = System.getProperty("user.home") + "/oph-configuration/valinta-tulos-emailer.properties"
    lazy val settings = ApplicationSettingsLoader.loadSettings(configFile)(ApplicationSettingsParser(commandLineArgs))
    def log4jconfigFile = System.getProperty("user.home") + "/oph-configuration/log4j.properties"
    val log4jproperties = new Properties()
    log4jproperties.load(new FileInputStream(log4jconfigFile))
    PropertyConfigurator.configure(log4jproperties)
  }

  trait ExampleTemplatedProps extends Registry with TemplatedProps {
    def templateAttributesFile = "src/main/resources/oph-configuration/dev-vars.yml"
  }

  trait TemplatedProps extends WithCommandLineArgs {
    println("Using template variables from " + templateAttributesFile)
    lazy val settings = loadSettings
    def loadSettings = ConfigTemplateProcessor.createSettings("valinta-tulos-emailer", templateAttributesFile)(ApplicationSettingsParser(commandLineArgs))
    def templateAttributesFile: String
  }
  
  trait WithCommandLineArgs {
    val commandLineArgs: CommandLineArgs
  }

  trait StubbedExternalDeps

  trait Registry extends Components {
    val settings: ApplicationSettings
  }

}
