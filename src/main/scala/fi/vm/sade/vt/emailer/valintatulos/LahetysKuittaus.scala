package fi.vm.sade.vt.emailer.valintatulos

case class LahetysKuittausOld(hakemusOid: String,
                           hakukohteet: List[String],
                           mediat: List[String])

object LahetysKuittausOld {
  def apply(recipient: Ilmoitus): LahetysKuittausOld = {
    new LahetysKuittausOld(recipient.hakemusOid, recipient.hakukohteet.map(_.oid), List("email"))
  }
}
