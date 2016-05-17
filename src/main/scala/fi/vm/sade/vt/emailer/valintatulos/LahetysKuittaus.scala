package fi.vm.sade.vt.emailer.valintatulos

case class LahetysKuittaus(hakemusOid: String,
                           hakukohteet: List[String],
                           mediat: List[String])

object LahetysKuittaus {
  def apply(recipient: VastaanotettavuusIlmoitus): LahetysKuittaus = {
    new LahetysKuittaus(recipient.hakemusOid, recipient.hakukohteet.map(_.oid), List("email"))
  }
}
