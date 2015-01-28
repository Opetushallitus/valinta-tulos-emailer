package fi.vm.sade.vt.emailer.config

import fi.vm.sade.groupemailer.{GroupEmailService, GroupEmailComponent}
import fi.vm.sade.vt.emailer.{Mailer, MailerComponent}
import fi.vm.sade.vt.emailer.config.Registry.{StubbedGroupEmail, StubbedExternalDeps}
import fi.vm.sade.vt.emailer.valintatulos.{VastaanottopostiService, VastaanottopostiComponent}


trait Components extends GroupEmailComponent with VastaanottopostiComponent with MailerComponent with ApplicationSettingsComponent {
  val settings: ApplicationSettings

  private def configureGroupEmailService: GroupEmailService = this match {
    case x: StubbedGroupEmail => new FakeGroupEmailService
    case _ => new RemoteGroupEmailService(settings)
  }

  private def configureVastaanottopostiService: VastaanottopostiService = this match {
    case x: StubbedExternalDeps => new FakeVastaanottopostiService
    case _ => new RemoteVastaanottopostiService
  }

  override val groupEmailService: GroupEmailService = configureGroupEmailService
  override val vastaanottopostiService: VastaanottopostiService = configureVastaanottopostiService

  override val mailer: Mailer = new MailerImpl

  def start {}
  def stop {}
}
