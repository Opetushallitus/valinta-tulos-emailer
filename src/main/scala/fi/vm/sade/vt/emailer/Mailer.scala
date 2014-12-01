package fi.vm.sade.vt.emailer

import fi.vm.sade.vt.emailer.ryhmasahkoposti.{EmailInfo, GroupEmail, GroupEmailComponent}
import fi.vm.sade.vt.emailer.util.Logging
import fi.vm.sade.vt.emailer.valintatulos.{VastaanotettavuusIlmoitus, VastaanOttoPostiComponent}

trait MailerComponent {
  this: GroupEmailComponent with VastaanOttoPostiComponent =>

  val mailer: Mailer

  class Mailer extends Logging {
    def sendBatch(): Option[(String, List[VastaanotettavuusIlmoitus])] = {
      val batch: List[VastaanotettavuusIlmoitus] = vastaanottopostiService.fetchRecipientBatch
      val recipients: List[ryhmasahkoposti.Recipient] = batch.map(ryhmasahkoposti.Recipient(_))
      logger.info(s"Starting to send batch. Batch size ${recipients.size}")
      if (recipients.size > 0) {
        groupEmailService.send(new GroupEmail(recipients, new EmailInfo()))
      } else {
        logger.info("Batch size was 0, stopping")
        None
      }
    }

  }
}