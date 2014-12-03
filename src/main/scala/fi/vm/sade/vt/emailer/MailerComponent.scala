package fi.vm.sade.vt.emailer

import fi.vm.sade.vt.emailer.config.ApplicationSettings
import fi.vm.sade.vt.emailer.ryhmasahkoposti.{EmailInfo, GroupEmail, GroupEmailComponent}
import fi.vm.sade.vt.emailer.util.Logging
import fi.vm.sade.vt.emailer.valintatulos.{VastaanotettavuusIlmoitus, VastaanottopostiComponent}

trait MailerComponent {
  this: GroupEmailComponent with VastaanottopostiComponent =>

  val mailer: Mailer

  class Mailer(settings: ApplicationSettings) extends Logging {
    def sendMail: List[String] = {
      collectAndSend()
    }

    private def collectAndSend(batchNr: Int = 0, ids: List[String] = List(), batch: List[VastaanotettavuusIlmoitus] = List()): List[String] = {
      def sendAndConfirm(currentBatch: List[VastaanotettavuusIlmoitus]): List[String] = {
        sendBatch(currentBatch) match {
          case Some(id) => ids :+ id
          case _ => ids
        }
      }
      val newBatch = vastaanottopostiService.fetchRecipientBatch
      if (newBatch.size > 0 && batchNr < 2) {
        val currentBatch = batch ::: newBatch
        if (currentBatch.size >= settings.emailBatchSize) {
          val batchIds: List[String] = sendAndConfirm(currentBatch)
          logger.info(s"collecting for batch nr. $batchNr")
          collectAndSend(batchNr + 1, batchIds)
        } else {
          collectAndSend(batchNr, ids, currentBatch)
        }
      } else {
        if (batch.size > 0) {
          logger.info("Last batch")
          sendAndConfirm(batch)
        } else {
          logger.info("Batch size was 0, stopping")
          ids
        }
      }
    }

    private def sendBatch(batch: List[VastaanotettavuusIlmoitus]): Option[String] = {
      val recipients: List[ryhmasahkoposti.Recipient] = batch.map(ryhmasahkoposti.Recipient(_))
      logger.info(s"Starting to send batch. Batch size ${recipients.size}")
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
    }
  }
}