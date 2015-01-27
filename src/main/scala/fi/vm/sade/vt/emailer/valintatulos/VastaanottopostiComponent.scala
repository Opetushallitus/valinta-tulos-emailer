package fi.vm.sade.vt.emailer.valintatulos

import fi.vm.sade.utils.http.DefaultHttpClient
import fi.vm.sade.utils.slf4j.Logging
import fi.vm.sade.vt.emailer.config.{ApplicationSettingsComponent, ApplicationSettings}
import fi.vm.sade.vt.emailer.json.JsonFormats
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization

import scalaj.http.HttpOptions

trait VastaanottopostiComponent {
  this: ApplicationSettingsComponent =>

  val vastaanottopostiService: VastaanottopostiService

  class VastaanottopostiService extends JsonFormats with Logging {
    private val httpOptions = Seq(HttpOptions.connTimeout(10000), HttpOptions.readTimeout(90000))

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
}
