package fi.vm.sade.vt.emailer.valintatulos


import fi.vm.sade.vt.emailer.valintatulos.LahetysSyy.LahetysSyy
import org.joda.time.DateTime

case class Ilmoitus(
                     hakemusOid: String,
                     hakijaOid: String,
                     asiointikieli: String,
                     etunimi: String,
                     email: String,
                     deadline: Option[DateTime],
                     hakukohteet: List[Hakukohde],
                     haku: Haku,
                     lahetysSyy: Option[LahetysSyy]
                   )

case class Hakukohde(
                      oid: String,
                      lahetysSyy: Option[LahetysSyy],
                      ehdollisestiHyvaksyttavissa: Boolean,
                      hakukohteenNimet: Map[String, Option[String]],
                      tarjoajaNimet: Map[String, Option[String]]
                    )

case class Haku(
                 oid: String,
                 nimi: Map[String, Option[String]]
               )

case class LahetysKuittaus(
                            hakemusOid: String,
                            hakukohteet: List[String],
                            mediat: List[String],
                            lahetysSyy: Option[LahetysSyy]
                          )

object LahetysKuittaus {
  def apply(recipient: Ilmoitus): LahetysKuittaus = {
    new LahetysKuittaus(recipient.hakemusOid, recipient.hakukohteet.map(_.oid), List("email"), recipient.lahetysSyy)
  }
}

object LahetysSyy {
  type LahetysSyy = String
  val vastaanottoilmoitus: LahetysSyy = "VASTAANOTTOILMOITUS"
  val ehdollisen_periytymisen_ilmoitus: LahetysSyy = "EHDOLLISEN_PERIYTYMISEN_ILMOITUS"
  val sitovan_vastaanoton_ilmoitus: LahetysSyy = "SITOVAN_VASTAANOTON_ILMOITUS"
}
