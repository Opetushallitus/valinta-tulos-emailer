package fi.vm.sade.vt.emailer

import fi.vm.sade.groupemailer.{EmailInfo, GroupEmail, GroupEmailComponent, Recipient}
import fi.vm.sade.utils.slf4j.Logging
import fi.vm.sade.vt.emailer.config.ApplicationSettingsComponent
import fi.vm.sade.vt.emailer.valintatulos.{VastaanotettavuusIlmoitus, VastaanottopostiComponent}

trait MailerComponent {
  this: GroupEmailComponent with VastaanottopostiComponent with ApplicationSettingsComponent =>

  val mailer: Mailer

  class MailerImpl extends Mailer with Logging {
    def sendMail: List[String] = {
      collectAndSend()
    }

    private def collectAndSend(batchNr: Int = 0, ids: List[String] = List(), batch: List[VastaanotettavuusIlmoitus] = List()): List[String] = {
      def sendAndConfirm(currentBatch: List[VastaanotettavuusIlmoitus]): List[String] = {
        val groupedByLang: Map[String, List[VastaanotettavuusIlmoitus]] = currentBatch.groupBy(v => v.asiointikieli)
        groupedByLang.foreach { case (kieli, ilmoitukset) => logger.info("Kieli: " + kieli + ", ilmoituksia " + ilmoitukset.size) }

        val sentIds : List[String] = groupedByLang.map { case (language, ilmoitukset) => {
          sendBatch(ilmoitukset, language)
        }}.toList.flatten
        ids ++ sentIds
      }
      val newBatch = vastaanottopostiService.fetchRecipientBatch
      if (newBatch.size > 0) {

        val currentBatch = batch ::: newBatch
        if (currentBatch.size >= settings.emailBatchSize) {
          logger.info(s"Sending batch nr. $batchNr")
          val batchIds: List[String] = sendAndConfirm(currentBatch)
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

    private def sendBatch(batch: List[VastaanotettavuusIlmoitus], language: String): Option[String] = {
      val recipients: List[Recipient] = batch.map(ryhmasahkoposti.VTRecipient(_))
      if (!settings.testMode) {
        logger.info(s"Starting to send batch. Batch size ${recipients.size}")
        try {
          groupEmailService.send(new GroupEmail(recipients, new EmailInfo("omattiedot", "omattiedot_email", language))) match {
            case Some(id) => {
              if (vastaanottopostiService.sendConfirmation(batch)) {
                logger.info(s"Succesfully confirmed batch id: $id")
              } else {
                logger.error(s"Could not send confirmation! Batch was still sent, batch id: $id")
              }
              Some(id)
            }
            case _ => None
          }
        } catch {
          case e : Exception => {
            logger.error("Group email sending error "+e)
            None
          }
        }
      } else {
        logger.info(s"Not actually sending anything, test mode was set. Batch size ${recipients.size}")
        Some(s"${recipients.size}")
      }
    }
  }
}

trait Mailer {
  def sendMail: List[String]
}