package fi.vm.sade.vt.emailer.ryhmasahkoposti

import fi.vm.sade.groupemailer.{Recipient, Replacement}
import fi.vm.sade.vt.emailer.valintatulos
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

case class Hakukohde(
  oid: String,
  nimi: String,
  tarjoaja: String,
  ehdollisestiHyvaksyttavissa: Boolean
)

object VTEmailerReplacement {
  val fmt = DateTimeFormat.forPattern("dd.MM.yyyy")

  def firstName(name: String) = new Replacement("etunimi", name)

  def deadline(date: Option[DateTime]) = new Replacement("deadline", deadlineText(date))

  def haunNimi(name: String) = new Replacement("haunNimi", name)

  def hakukohteet(hakukohteet: List[Hakukohde]) = new Replacement("hakukohteet", hakukohteet)

  private def deadlineText(date: Option[DateTime]): String = date match {
    case Some(deadline) => fmt.print(deadline)
    case _ => ""
  }
}

object VTRecipient {
  def apply(valintatulosRecipient: valintatulos.VastaanotettavuusIlmoitus, language: String): Recipient = {

    def getTranslation(rawTranslations: Map[String, String]) = {

      def fixKey(key: String) = key.toLowerCase.replace("kieli_", "")

      val translations = rawTranslations.map{case (key, value) => (fixKey(key), value)}

      translations.get(language).orElse(translations.get("fi")).getOrElse(translations.head._2)
    }

    val replacements = List(
      VTEmailerReplacement.firstName(valintatulosRecipient.etunimi),
      VTEmailerReplacement.deadline(valintatulosRecipient.deadline),
      VTEmailerReplacement.haunNimi(getTranslation(valintatulosRecipient.haku.nimi)),
      VTEmailerReplacement.hakukohteet(valintatulosRecipient.hakukohteet.map(hakukohde =>
        Hakukohde(hakukohde.oid, getTranslation(hakukohde.hakukohteenNimet),
          getTranslation(hakukohde.tarjoajaNimet), hakukohde.ehdollisestiHyvaksyttavissa)
      ))
    )
    new Recipient(Some(valintatulosRecipient.hakijaOid), valintatulosRecipient.email, valintatulosRecipient.asiointikieli, replacements)
  }
}