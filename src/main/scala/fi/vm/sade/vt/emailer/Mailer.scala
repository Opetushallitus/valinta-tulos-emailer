package fi.vm.sade.vt.emailer

import fi.vm.sade.vt.emailer.ryhmasahkoposti.{EmailInfo, GroupEmail, GroupEmailComponent}
import fi.vm.sade.vt.emailer.util.Logging
import fi.vm.sade.vt.emailer.valintatulos.{VastaanotettavuusIlmoitus, VastaanottopostiComponent}

trait MailerComponent {
  this: GroupEmailComponent with VastaanottopostiComponent =>

  val mailer: Mailer

  class Mailer extends Logging {
    def sendBatch(): Option[String] = {
      val batch: List[VastaanotettavuusIlmoitus] = vastaanottopostiService.fetchRecipientBatch
      val recipients: List[ryhmasahkoposti.Recipient] = batch.map(ryhmasahkoposti.Recipient(_))
      logger.info(s"Starting to send batch. Batch size ${recipients.size}")
      if (recipients.size > 0) {
        groupEmailService.send(new GroupEmail(recipients, new EmailInfo())) match {
          case Some(id)  => {
            if (vastaanottopostiService.sendConfirmation(batch)) {
              logger.info(s"Succesfully confirmed batch id: $id")
            } else {
              logger.error(s"Could not send confirmation! Batch was still sent, batch id: $id")
            }
            Some(id)
          }
          case _ => None
        }
      } else {
        logger.info("Batch size was 0, stopping")
        None
      }
    }

  }
}