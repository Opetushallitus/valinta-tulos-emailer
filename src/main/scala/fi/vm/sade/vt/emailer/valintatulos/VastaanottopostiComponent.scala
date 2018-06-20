package fi.vm.sade.vt.emailer.valintatulos

import fi.vm.sade.utils.http.DefaultHttpClient
import fi.vm.sade.utils.slf4j.Logging
import fi.vm.sade.vt.emailer.config.ApplicationSettingsComponent
import fi.vm.sade.vt.emailer.json.JsonFormats
import fi.vm.sade.vt.emailer.util.RandomDataGenerator._
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization

import scala.util.Random
import scalaj.http.HttpOptions

trait VastaanottopostiComponent {
  this: ApplicationSettingsComponent =>

  val vastaanottopostiService: VastaanottopostiService

  class RemoteVastaanottopostiService extends VastaanottopostiService with JsonFormats with Logging {
    private val httpOptions = Seq(HttpOptions.connTimeout(10 * 1000), HttpOptions.readTimeout(8 * 60 * 60 * 1000))
    private val retryCounter = 1

    def fetchRecipientBatch: List[Ilmoitus] = {
      val reciepientBatchRequest = DefaultHttpClient.httpGet(settings.vastaanottopostiUrl, httpOptions: _*)
        .param("limit", settings.recipientBatchSize.toString)

      reciepientBatchRequest.responseWithHeaders() match {
        case (status, _, body) if status >= 200 && status < 300 =>
          logger.info(s"Received from valinta-tulos-service: $body")
          parse(body).extract[List[Ilmoitus]]
        case (status, _, body) =>
          logger.error(s"Couldn't not connect to: ${settings.vastaanottopostiUrl}")
          logger.error(s"Fetching recipient batch failed with status: $status and body: $body")
          List()
      }
    }




    def sendConfirmation(sendConfirmationRetries: Int, recipients: List[Ilmoitus]): Boolean = {
      val receipts: List[LahetysKuittaus] = recipients.map(LahetysKuittaus(_))
      val result = DefaultHttpClient.httpPost(settings.vastaanottopostiUrl, Some(Serialization.write(receipts))).header("Content-type", "application/json")
      result.responseWithHeaders() match {
        case (status, _, _) if status >= 200 && status < 300 =>
          true
        case (status, _, body) if retryCounter <= sendConfirmationRetries =>
          Thread.sleep(1000 * settings.sendConfirmationSleep * retryCounter)
          logger.error(s"Retrying to send confirmation since it failed with status: $status and body: $body")
          sendConfirmation(retryCounter + 1, recipients)
        case (status, _, body) =>
          logger.error(s"Sending confirmation failed with status: $status and body: $body")
          false
      }
    }
  }

  class FakeVastaanottopostiService extends VastaanottopostiService {
    private[this] var _confirmAmount: Int = 0
    private var sentAmount = 0
    val maxResults: Int = settings.emailBatchSize + 1
    val recipients = List.fill(maxResults)(randomIlmoitus)

    def fetchRecipientBatch: List[Ilmoitus] = {
      if (sentAmount < maxResults) {
        val guys = recipients.slice(sentAmount, sentAmount + settings.recipientBatchSize)
        sentAmount += guys.size
        guys
      } else {
        List()
      }
    }

    def sendConfirmation(sendConfirmationRetries: Int, recipients: List[Ilmoitus]): Boolean = {
      _confirmAmount += recipients.size
      true
    }

    def randomIlmoitus = Ilmoitus(randomOid, randomOid, None, randomLang,
      randomFirstName, randomEmailAddress, Some(randomDateAfterNow), randomHakukohdeList,
      Haku(randomOid, Map("kieli_fi" -> Some("Testihaku"))))

    def randomHakukohdeList = List.fill(Random.nextInt(10) + 1)(randomOid).map(oid => Hakukohde(
      oid, ehdollisestiHyvaksyttavissa = false, Map("kieli_fi" -> Some("Testihakukohde")), Map("fi" -> Some("Testitarjoaja")),
      LahetysSyy.vastaanottoilmoitusKk))

    def confirmAmount: Int = _confirmAmount
  }

}

trait VastaanottopostiService {
  def fetchRecipientBatch: List[Ilmoitus]

  def sendConfirmation(sendConfirmationRetries: Int, recipients: List[Ilmoitus]): Boolean
}
