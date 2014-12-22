package fi.vm.sade.vt.emailer.config

import com.typesafe.config._
import fi.vm.sade.groupemailer.GroupEmailerSettings
import fi.vm.sade.vt.emailer.CommandLineArgs

case class ApplicationSettings(config: Config, commandLineArgs: CommandLineArgs) extends GroupEmailerSettings(config) {
  val vastaanottopostiUrl = config.getString("valinta-tulos-service.vastaanottoposti.url")
  val recipientBatchSize = config.getInt("valinta-tulos-service.batch.size")
  val testMode = commandLineArgs.test
}

case class ApplicationSettingsParser(commandLineArgs: CommandLineArgs) extends fi.vm.sade.utils.config.ApplicationSettingsParser[ApplicationSettings] {
  override def parse(config: Config) = ApplicationSettings(config, commandLineArgs)
}

trait ApplicationSettingsComponent {
  val settings: ApplicationSettings
}