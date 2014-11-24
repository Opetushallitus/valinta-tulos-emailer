package fi.vm.sade.vt.emailer.valintatulos

import fi.vm.sade.vt.emailer.DefaultHttpClient
import fi.vm.sade.vt.emailer.config.ApplicationSettings
import json.JsonFormats
import org.json4s.jackson.JsonMethods.parse

trait VastaanOttoPostiComponent {
  val vastaanottopostiService: VastaanottopostiService

  class VastaanottopostiService(settings: ApplicationSettings) extends JsonFormats {
    def fetchRecipientBatch: List[Recipient] = {
      val reciepientBatchRequest = DefaultHttpClient.httpGet(settings.vastaanottopostiUrl)
      reciepientBatchRequest.response() match {
        case Some(jsonString) => parse(jsonString).extract[List[Recipient]]
        case _ => List()
      }
    }
  }
}
