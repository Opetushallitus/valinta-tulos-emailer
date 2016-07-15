package fi.vm.sade.vt.emailer.valintatulos

import org.joda.time.DateTime

case class VastaanotettavuusIlmoitus(
  hakemusOid: String,
  hakijaOid: String,
  asiointikieli: String,
  etunimi: String,
  email: String,
  deadline: Option[DateTime],
  hakukohteet: List[HakukohdeOld],
  haku: HakuOld
)

case class HakukohdeOld(
  oid: String,
  ehdollisestiHyvaksyttavissa: Boolean,
  hakukohteenNimet: Map[String, Option[String]],
  tarjoajaNimet: Map[String, Option[String]]
)

case class HakuOld(
 oid: String,
 nimi: Map[String, Option[String]]
)