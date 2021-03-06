package fi.vm.sade.vt.emailer.config

import fi.vm.sade.groupemailer.{GroupEmailComponent, GroupEmailService}
import fi.vm.sade.vt.emailer.config.Registry.{StubbedExternalDeps, StubbedGroupEmail}
import fi.vm.sade.vt.emailer.valintatulos.{VastaanottopostiComponent, VastaanottopostiService}
import fi.vm.sade.vt.emailer.{Mailer, MailerComponent}


trait Components extends GroupEmailComponent with VastaanottopostiComponent with MailerComponent with ApplicationSettingsComponent {
  val settings: ApplicationSettings

  private def configureGroupEmailService: GroupEmailService = this match {
    case x: StubbedGroupEmail => new FakeGroupEmailService
    case _ => new RemoteGroupEmailService(settings, "valinta-tulos-emailer")
  }

  private def configureVastaanottopostiService: VastaanottopostiService = this match {
    case x: StubbedExternalDeps => new FakeVastaanottopostiService
    case _ => new RemoteVastaanottopostiService
  }

  override val groupEmailService: GroupEmailService = configureGroupEmailService
  override val vastaanottopostiService: VastaanottopostiService = configureVastaanottopostiService

  override val mailer: Mailer = new MailerImpl

  def start() {}

  def stop() {}
}
