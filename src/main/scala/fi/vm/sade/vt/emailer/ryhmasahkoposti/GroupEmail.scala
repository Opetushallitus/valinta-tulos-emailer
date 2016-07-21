package fi.vm.sade.vt.emailer.ryhmasahkoposti

import fi.vm.sade.groupemailer.{Recipient, Replacement}
import fi.vm.sade.vt.emailer.valintatulos
import fi.vm.sade.vt.emailer.valintatulos.LahetysSyy._
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

  def firstName(name: String) = Replacement("etunimi", name)

  def deadline(date: Option[DateTime]) = Replacement("deadline", deadlineText(date))

  def haunNimi(name: String) = Replacement("haunNimi", name)

  def hakukohteet(hakukohteet: List[Hakukohde]) = Replacement("hakukohteet", hakukohteet)

  def hakukohde(hakukohde: String) = Replacement("hakukohde", hakukohde)

  private def deadlineText(date: Option[DateTime]): String = date match {
    case Some(deadline) => fmt.print(deadline)
    case _ => ""
  }
}

object VTRecipient {
  def apply(valintatulosRecipient: valintatulos.Ilmoitus, language: String): Recipient = {

    def getTranslation(rawTranslations: Map[String, Option[String]]) = {

      def fixKey(key: String) = key.toLowerCase.replace("kieli_", "")

      val translations = rawTranslations
        .filter { case (key, value) => value.isDefined && !value.get.isEmpty }
        .map { case (key, value) => (fixKey(key), value) }

      translations.get(language.toLowerCase).orElse(translations.get("fi")).getOrElse(translations.head._2).get
    }

    def getHakukohtees: Replacement = {
      val hakukohteet = valintatulosRecipient.hakukohteet
      val lahetysSyy: LahetysSyy = hakukohteet.head.lahetysSyy
      if (hakukohteet.size == 1 && (lahetysSyy.equals(ehdollisen_periytymisen_ilmoitus) || lahetysSyy.equals(sitovan_vastaanoton_ilmoitus))) {
        VTEmailerReplacement.hakukohde(getTranslation(hakukohteet.head.hakukohteenNimet))
      } else if (lahetysSyy.equals(vastaanottoilmoitus)) {
        VTEmailerReplacement.hakukohteet(hakukohteet.map(hakukohde =>
          Hakukohde(hakukohde.oid, getTranslation(hakukohde.hakukohteenNimet),
            getTranslation(hakukohde.tarjoajaNimet), hakukohde.ehdollisestiHyvaksyttavissa)
        ))
      } else {
        throw new IllegalArgumentException("Failed to add hakukohde information to recipient. Hakemus " + valintatulosRecipient.hakemusOid +
          ". LahetysSyy was " + hakukohteet.head.lahetysSyy + " and there was " + hakukohteet.size + "hakukohtees")
      }
    }

    val replacements = List(
      VTEmailerReplacement.firstName(valintatulosRecipient.etunimi),
      VTEmailerReplacement.deadline(valintatulosRecipient.deadline),
      VTEmailerReplacement.haunNimi(getTranslation(valintatulosRecipient.haku.nimi)),
      getHakukohtees
    )
    Recipient(Some(valintatulosRecipient.hakijaOid), valintatulosRecipient.email, valintatulosRecipient.asiointikieli, replacements)
  }
}