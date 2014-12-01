package fi.vm.sade.vt.emailer.ryhmasahkoposti

trait GroupEmailService {
  def send(email: GroupEmail): Option[String]
}