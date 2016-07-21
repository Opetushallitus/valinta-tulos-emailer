package fi.vm.sade.vt.emailer

import fi.vm.sade.vt.emailer.config.Registry
import fi.vm.sade.vt.emailer.config.Registry.IT
import org.specs2.mutable.Specification

class MailerTest extends Specification {
  val registry: IT = Registry.fromString("it", CommandLineArgs()).asInstanceOf[IT]
  registry.start()

  "Mailer divides batch correctly" should {
    "divides job into 4 batches and confirms all of them" in {
      val batches = registry.mailer.sendMail
      batches.size mustEqual 4 // 3 languages and 1 extra ilmoitus over batch size
      registry.lastConfirmedAmount mustEqual registry.maxResults
    }
  }
}
