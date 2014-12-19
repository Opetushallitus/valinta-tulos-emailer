package fi.vm.sade.vt.emailer

import fi.vm.sade.utils.slf4j.Logging
import fi.vm.sade.vt.emailer.config.Registry
import fi.vm.sade.vt.emailer.config.Registry.{IT, Registry}
import fi.vm.sade.vt.emailer.util.ValintatulosServiceRunner
import org.scalatra.test.HttpComponentsClient
import org.specs2.mutable._

class SmokeTest extends Specification with HttpComponentsClient with Logging {
  lazy val registry: Registry = Registry.fromString(Option(System.getProperty("valintatulos.profile")).getOrElse("it"), CommandLineArgs())

  override def baseUrl: String = "http://localhost:" + ValintatulosServiceRunner.valintatulosPort + "/valinta-tulos-service"

  "Generate test fixtures and retrieve them" should {
    "PUT /util/fixtures/generate" in {
      registry.start
      put("util/fixtures/generate?hakemuksia=3&hakukohteita=2") {
        registry.mailer.sendMail.nonEmpty
        registry.asInstanceOf[IT].lastEmailSize() equals(3)
      }
    }
  }
}
