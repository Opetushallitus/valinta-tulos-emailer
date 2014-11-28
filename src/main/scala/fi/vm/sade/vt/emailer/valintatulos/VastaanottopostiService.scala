package fi.vm.sade.vt.emailer.valintatulos

import fi.vm.sade.vt.emailer.DefaultHttpClient
import fi.vm.sade.vt.emailer.config.ApplicationSettings
import fi.vm.sade.vt.emailer.util.Logging
import json.JsonFormats
import org.json4s.jackson.JsonMethods.parse

trait VastaanOttoPostiComponent {
  val vastaanottopostiService: VastaanottopostiService

  class VastaanottopostiService(settings: ApplicationSettings) extends JsonFormats with Logging {
    def fetchRecipientBatch: List[Recipient] = {
      val reciepientBatchRequest = DefaultHttpClient.httpGet(settings.vastaanottopostiUrl)
        .param("limit", settings.batchSize.toString)
      reciepientBatchRequest.response() match {
        case Some(jsonString) => parse(jsonString).extract[List[Recipient]]
        case _ => {
          logger.info("Couldn't not connect to "+settings.vastaanottopostiUrl)
          List()
        }
      }
    }
  }
}
