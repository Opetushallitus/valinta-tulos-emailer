package fi.vm.sade.vt.emailer.ryhmasahkoposti

import fi.vm.sade.groupemailer.{Recipient, Replacement}
import fi.vm.sade.vt.emailer.valintatulos
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object VTEmailerReplacement {
  val fmt = DateTimeFormat.forPattern("dd.MM.yyyy")

  def firstName(name: String) = new Replacement("etunimi", name)

  def deadline(date: Option[DateTime]) = new Replacement("deadline", deadlineText(date))

  private def deadlineText(date: Option[DateTime]): String = date match {
    case Some(deadline) => fmt.print(deadline)
    case _ => ""
  }
}

object VTRecipient {
  def apply(valintatulosRecipient: valintatulos.VastaanotettavuusIlmoitus): Recipient = {
    val replacements = List(VTEmailerReplacement.firstName(valintatulosRecipient.etunimi), VTEmailerReplacement.deadline(valintatulosRecipient.deadline))
    new Recipient(Some(valintatulosRecipient.hakijaOid), valintatulosRecipient.email, valintatulosRecipient.asiointikieli, replacements)
  }
}
