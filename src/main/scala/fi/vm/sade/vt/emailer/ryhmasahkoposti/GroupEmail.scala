package fi.vm.sade.vt.emailer.ryhmasahkoposti

case class GroupEmail(recipient: List[Recipient], email: EmailInfo)
case class Recipient(oid: String, email: String, languageCode: String, recipientReplacements: List[Replacement], oidType: String = "opiskelija")
case class EmailInfo(callingProcess: String = "omattiedot", templateName: String = "omattiedot_email")
case class Replacement(name: String, value: String)

object Replacement {
  def firstName(name: String) = new Replacement("etunimi", name)
  def deadline(name: String) = new Replacement("deadline", name)
}

object GroupEmail {
  def apply(oid: String, email: String, languageCode: String, firstName: String, dealine: String) = {
    val replacements = List(Replacement.firstName(firstName), Replacement.deadline(dealine))
    new GroupEmail(List(new Recipient(oid, email, languageCode, replacements)), new EmailInfo)
  }
}
