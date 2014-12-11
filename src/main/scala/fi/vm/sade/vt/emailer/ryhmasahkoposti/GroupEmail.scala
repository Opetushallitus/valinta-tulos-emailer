package fi.vm.sade.vt.emailer.ryhmasahkoposti

import fi.vm.sade.vt.emailer.valintatulos
import fi.vm.sade.vt.emailer.valintatulos.VastaanotettavuusIlmoitus
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

case class GroupEmail(recipient: List[Recipient], email: EmailInfo)
case class Recipient(oid: String, email: String, languageCode: String, recipientReplacements: List[Replacement], oidType: String = "opiskelija")
case class EmailInfo(callingProcess: String = "omattiedot", templateName: String = "omattiedot_email")
case class Replacement(name: String, value: String)

object Replacement {
  val fmt = DateTimeFormat.forPattern("dd.MM.yyyy")

  def firstName(name: String) = new Replacement("etunimi", name)
  def deadline(date: Option[DateTime]) = new Replacement("deadline", deadlineText(date))

  private def deadlineText(date: Option[DateTime]): String = date match {
    case Some(deadline) => fmt.print(deadline)
    case _ => ""
  }
}

object Recipient {

  def apply(valintatulosRecipient: valintatulos.VastaanotettavuusIlmoitus): Recipient = {
    val replacements = List(Replacement.firstName(valintatulosRecipient.etunimi), Replacement.deadline(valintatulosRecipient.deadline))
    new Recipient(valintatulosRecipient.hakijaOid, valintatulosRecipient.email, "FI", replacements)
  }

}
