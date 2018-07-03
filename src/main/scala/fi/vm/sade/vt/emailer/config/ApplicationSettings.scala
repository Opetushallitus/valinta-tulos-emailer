package fi.vm.sade.vt.emailer.config

import com.typesafe.config._
import fi.vm.sade.groupemailer.GroupEmailerSettings
import fi.vm.sade.vt.emailer.CommandLineArgs

case class ApplicationSettings(config: Config, commandLineArgs: CommandLineArgs) extends GroupEmailerSettings(config) {
  val vastaanottopostiUrl: String = config.getString("valinta-tulos-service.vastaanottoposti.url")
  val recipientBatchSize: Int = config.getInt("valinta-tulos-service.batch.size")
  val testMode: Boolean = commandLineArgs.test
  val sendConfirmationRetries: Int = config.getInt("valinta-tulos-service.confirmation.retries")
  val sendConfirmationSleep: Int = config.getInt("valinta-tulos-service.confirmation.sleep.seconds")
}

case class ApplicationSettingsParser(commandLineArgs: CommandLineArgs) extends fi.vm.sade.utils.config.ApplicationSettingsParser[ApplicationSettings] {
  override def parse(config: Config) = ApplicationSettings(config, commandLineArgs)
}

trait ApplicationSettingsComponent {
  val settings: ApplicationSettings
}
