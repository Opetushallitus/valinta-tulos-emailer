package fi.vm.sade.vt.emailer.valintatulos

import fi.vm.sade.vt.emailer.DefaultHttpClient
import fi.vm.sade.vt.emailer.config.{ApplicationSettingsComponent, ApplicationSettings}
import fi.vm.sade.vt.emailer.util.Logging
import json.JsonFormats
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization

trait VastaanottopostiComponent {
  this: ApplicationSettingsComponent =>

  val vastaanottopostiService: VastaanottopostiService

  class VastaanottopostiService extends JsonFormats with Logging {
    def fetchRecipientBatch: List[VastaanotettavuusIlmoitus] = {
      val reciepientBatchRequest = DefaultHttpClient.httpGet(settings.vastaanottopostiUrl)
        .param("limit", settings.recipientBatchSize.toString)
      reciepientBatchRequest.response() match {
        case Some(jsonString) => parse(jsonString).extract[List[VastaanotettavuusIlmoitus]]
        case _ => {
          logger.error("Couldn't not connect to "+settings.vastaanottopostiUrl)
          List()
        }
      }
    }

    def sendConfirmation(recipients: List[VastaanotettavuusIlmoitus]): Boolean = {
      val reciepts: List[LahetysKuittaus] = recipients.map(LahetysKuittaus(_))
      val result = DefaultHttpClient.httpPost(settings.vastaanottopostiUrl, Some(Serialization.write(reciepts)))
      result.responseWithHeaders() match {
        case (status, _, _) if status == 200 => true
        case (status, _, body) => {
          logger.error(s"Sending confirmation failed with status: $status and body: $body")
          false
        }
      }
    }
  }
}
