package fi.vm.sade.vt.emailer.ryhmasahkoposti

import fi.vm.sade.vt.emailer.valintatulos
import org.joda.time.format.DateTimeFormat

case class GroupEmail(recipient: List[Recipient], email: EmailInfo)
case class Recipient(oid: String, email: String, languageCode: String, recipientReplacements: List[Replacement], oidType: String = "opiskelija")
case class EmailInfo(callingProcess: String = "omattiedot", templateName: String = "omattiedot_email")
case class Replacement(name: String, value: String)

object Replacement {
  def firstName(name: String) = new Replacement("etunimi", name)
  def deadline(name: String) = new Replacement("deadline", name)
}

object Recipient {
  val fmt = DateTimeFormat.forPattern("dd.MM.yyyy")

  def apply(valintatulosRecipient: valintatulos.VastaanotettavuusIlmoitus): Recipient = {
    val replacements = List(Replacement.firstName(valintatulosRecipient.etunimi), Replacement.deadline(fmt.print(valintatulosRecipient.deadline)))
    new Recipient(valintatulosRecipient.hakijaOid, valintatulosRecipient.email, "FI", replacements)
  }
}
