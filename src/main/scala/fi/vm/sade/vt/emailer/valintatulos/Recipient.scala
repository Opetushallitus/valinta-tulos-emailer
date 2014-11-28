package fi.vm.sade.vt.emailer.valintatulos

import org.joda.time.DateTime

case class Recipient(hakemusOid: String, hakijaOid: String, asiointikieli: String, etunimi: String, email: String,
                     deadline: DateTime, hakukohteet: List[String])
