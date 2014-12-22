package fi.vm.sade.vt.emailer.config

import fi.vm.sade.groupemailer.{GroupEmailService, GroupEmailComponent}
import fi.vm.sade.vt.emailer.MailerComponent
import fi.vm.sade.vt.emailer.config.Registry.StubbedExternalDeps
import fi.vm.sade.vt.emailer.valintatulos.VastaanottopostiComponent


trait Components extends GroupEmailComponent with VastaanottopostiComponent with MailerComponent with ApplicationSettingsComponent {
  val settings: ApplicationSettings

  private def configureGroupEmailService: GroupEmailService = this match {
    case x: StubbedExternalDeps => new FakeGroupEmailService
    case _ => new RemoteGroupEmailService(settings)
  }

  override val groupEmailService: GroupEmailService = configureGroupEmailService
  override val vastaanottopostiService: VastaanottopostiService = new VastaanottopostiService
  override val mailer: Mailer = new Mailer

  def start {}
  def stop {}
}
