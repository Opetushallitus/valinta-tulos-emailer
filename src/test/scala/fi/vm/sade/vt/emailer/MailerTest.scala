package fi.vm.sade.vt.emailer

import fi.vm.sade.vt.emailer.config.Registry
import fi.vm.sade.vt.emailer.config.Registry.IT
import org.specs2.mutable.Specification

class MailerTest extends Specification {
  val registry: IT = Registry.fromString("it", CommandLineArgs()).asInstanceOf[IT]
  registry.start

  "Mailer" should {
    "divide job into 2 batches and confirm all of them" in {
      val batches = registry.mailer.sendMail
      batches.size equals 2
      registry.lastConfirmedAmount equals registry.maxResults
    }
  }
}
