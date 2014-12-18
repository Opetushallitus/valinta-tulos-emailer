package fi.vm.sade.vt.emailer.config

import java.io.File

import com.typesafe.config._
import fi.vm.sade.vt.emailer.CommandLineArgs
import fi.vm.sade.vt.emailer.util.Logging

import scala.collection.JavaConversions._

object ApplicationSettings extends Logging {
  def loadSettings(fileLocation: String, commandLineArgs: CommandLineArgs): ApplicationSettings = {
    val configFile = new File(fileLocation)
    if (configFile.exists()) {
      logger.info("Using configuration file " + configFile)
      val settings: Config = ConfigFactory.load(ConfigFactory.parseFile(configFile))
      val applicationSettings = new ApplicationSettings(settings, commandLineArgs)
      applicationSettings
    } else {
      throw new RuntimeException("Configuration file not found: " + fileLocation)
    }
  }
}

class ApplicationSettings(config: Config, commandLineArgs: CommandLineArgs) {
  val casUrl = config.getString("cas.url")
  val groupEmailCasUrl = config.getString("ryhmasahkoposti.cas.service")
  val groupEmailCasUsername = config.getString("ryhmasahkoposti.cas.username")
  val groupEmailCasPassword = config.getString("ryhmasahkoposti.cas.password")
  val groupEmailSessionUrl = config.getString("ryhmasahkoposti.service.session.url")
  val groupEmailServiceUrl = config.getString("ryhmasahkoposti.service.email.url")
  val vastaanottopostiUrl = config.getString("valinta-tulos-service.vastaanottoposti.url")
  val emailBatchSize = config.getInt("ryhmasahkoposti.service.batch.size")
  val recipientBatchSize = config.getInt("valinta-tulos-service.batch.size")
  val testMode = commandLineArgs.test

  def withOverride(keyValuePair : (String, String)) = {
    new ApplicationSettings(config.withValue(keyValuePair._1, ConfigValueFactory.fromAnyRef(keyValuePair._2)), commandLineArgs)
  }

  def toProperties = {
    val keys = config.entrySet().toList.map(_.getKey)
    keys.map { key =>
      (key, config.getString(key))
    }.toMap
  }
}

trait ApplicationSettingsComponent {
  val settings: ApplicationSettings
}