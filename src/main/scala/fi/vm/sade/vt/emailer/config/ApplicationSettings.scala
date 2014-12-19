package fi.vm.sade.vt.emailer.config

import com.typesafe.config._
import fi.vm.sade.vt.emailer.CommandLineArgs

case class ApplicationSettings(config: Config, commandLineArgs: CommandLineArgs) extends fi.vm.sade.utils.config.ApplicationSettings(config) {
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
}

case class ApplicationSettingsParser(commandLineArgs: CommandLineArgs) extends fi.vm.sade.utils.config.ApplicationSettingsParser[ApplicationSettings] {
  override def parse(config: Config) = ApplicationSettings(config, commandLineArgs)
}

trait ApplicationSettingsComponent {
  val settings: ApplicationSettings
}