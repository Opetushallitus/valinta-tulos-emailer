package fi.vm.sade.vt.emailer

import fi.vm.sade.vt.emailer.config.Registry
import fi.vm.sade.vt.emailer.config.Registry.Registry
import fi.vm.sade.vt.emailer.util.Logging
import org.scalatra.test.HttpComponentsClient
import org.specs2.mutable._
import org.json4s._
import org.json4s.jackson.JsonMethods._


class SmokeTest extends Specification with HttpComponentsClient with Logging {

  val registry: Registry = Registry.fromString(Option(System.getProperty("valintatulos.profile")).getOrElse("dev"))
  registry.start

  override def baseUrl: String = "http://localhost:" + Registry.port.toString + "/valinta-tulos-service"

  "Generate test fixtures and retrieve them" should {
    "PUT /util/fixtures/generate" in {
      put("util/fixtures/generate") {
        putJSON("util/fixtures/generate?hakemuksia=17&hakukohteita=3", "") {
          status must_== 200
          get("vastaanottoposti") {
            val json = parse(body)
            json.children.length must_== 17
          }
        }
      }
    }
  }

  def putJSON[T](path: String, body: String, headers: Map[String, String] = Map.empty)(block: => T): T = {
    put(path, body.getBytes("UTF-8"), headers + ("Content-type" -> "application/json"))(block)
  }

  implicit class JValueExtended(value: JValue) {
    def has(childString: String): Boolean = {
      (value \ childString) != JNothing
    }
  }

}