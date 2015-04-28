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
    private val httpOptions = Seq(HttpOptions.connTimeout(10000), HttpOptions.readTimeout(300000))

    def fetchRecipientBatch: List[VastaanotettavuusIlmoitus] = {
      val reciepientBatchRequest = DefaultHttpClient.httpGet(settings.vastaanottopostiUrl, httpOptions: _*)
        .param("limit", settings.recipientBatchSize.toString)

      reciepientBatchRequest.responseWithHeaders() match {
        case (status, _, body) if status >= 200 && status < 300 => parse(body).extract[List[VastaanotettavuusIlmoitus]]
        case (status, _, body)  => {
          logger.error(s"Couldn't not connect to: ${settings.vastaanottopostiUrl}")
          logger.error(s"Fetching recipient batch failed with status: $status and body: $body")
          List()
        }
      }
    }

    def sendConfirmation(recipients: List[VastaanotettavuusIlmoitus]): Boolean = {
      val reciepts: List[LahetysKuittaus] = recipients.map(LahetysKuittaus(_))
      val result = DefaultHttpClient.httpPost(settings.vastaanottopostiUrl, Some(Serialization.write(reciepts))).header("Content-type", "application/json")
      result.responseWithHeaders() match {
        case (status, _, _) if status >= 200 && status < 300 => true
        case (status, _, body) => {
          logger.error(s"Sending confirmation failed with status: $status and body: $body")
          false
        }
      }
    }
  }

  class FakeVastaanottopostiService extends VastaanottopostiService {
    private[this] var _confirmAmount: Int = 0
    private var sentAmount = 0
    val maxResults: Int = settings.emailBatchSize + 1
    val recipients = List.fill(maxResults)(randomVastaanotettavuusIlmoitus)

    def fetchRecipientBatch: List[VastaanotettavuusIlmoitus] = {
      if(sentAmount < maxResults) {
        val guys = recipients.slice(sentAmount, sentAmount + settings.recipientBatchSize)
        sentAmount += guys.size
        guys
      } else {
        List()
      }
    }

    def sendConfirmation(recipients: List[VastaanotettavuusIlmoitus]): Boolean = {
      _confirmAmount += recipients.size
      true
    }

    def randomVastaanotettavuusIlmoitus = new VastaanotettavuusIlmoitus(randomOid, randomOid, randomLang, randomFirstName, randomEmailAddress, Some(randomDateAfterNow), randomOidList)
    def randomOidList = List.fill(Random.nextInt(10) +1)(randomOid)
    def confirmAmount: Int = _confirmAmount
  }
}

trait VastaanottopostiService {
  def fetchRecipientBatch: List[VastaanotettavuusIlmoitus]
  def sendConfirmation(recipients: List[VastaanotettavuusIlmoitus]): Boolean
}
