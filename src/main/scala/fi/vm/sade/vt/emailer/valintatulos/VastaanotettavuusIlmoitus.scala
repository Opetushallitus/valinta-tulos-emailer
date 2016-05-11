package fi.vm.sade.vt.emailer.valintatulos

import org.joda.time.DateTime

case class VastaanotettavuusIlmoitus(
  hakemusOid: String,
  hakijaOid: String,
  asiointikieli: String,
  etunimi: String,
  email: String,
  deadline: Option[DateTime],
  hakukohteet: List[Hakukohde],
  haku: Haku
)

case class Hakukohde(
  oid: String,
  ehdollisestiHyvaksyttavissa: Boolean,
  hakukohteenNimet: Map[String, String],
  tarjoajaNimet: Map[String, String]
)

case class Haku(
 oid: String,
 nimi: Map[String, String]
)