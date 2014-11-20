package fi.vm.sade.vt.emailer

import fi.vm.sade.vt.emailer.config.AppConfig
import fi.vm.sade.vt.emailer.config.AppConfig.AppConfig
import fi.vm.sade.vt.emailer.ryhmasahkoposti.{GroupEmail, GroupEmailService}

object Main extends App {
  val appConfig: AppConfig = AppConfig.fromString(Option(System.getProperty("valintatulos.profile")).getOrElse("dev"))
  // Usage --
  // val id = new GroupEmailService(appConfig.settings).send(GroupEmail("oid:123", "sähköposti@example.com", "FI", "Erkki", "1.1.2016"))
  // println(id)
}
