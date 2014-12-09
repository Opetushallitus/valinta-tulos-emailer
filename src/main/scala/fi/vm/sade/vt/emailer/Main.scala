package fi.vm.sade.vt.emailer

import fi.vm.sade.vt.emailer.config.Registry
import fi.vm.sade.vt.emailer.config.Registry.Registry
import fi.vm.sade.vt.emailer.util.{Logging, Timer}

object Main extends App with Logging {
  logger.info("***** VT-emailer started *****")
  val registry: Registry = Registry.fromString(Option(System.getProperty("vtemailer.profile")).getOrElse("default"))
  registry.start
  Timer.timed("Batch send") {
   val ids =  registry.mailer.sendMail
    if (ids.nonEmpty) {
      logger.info(s"Job sent succesfully, jobId: $ids")
      println(s"Job sent succesfully, jobId: $ids")
    } else {
      println("Nothing was sent. More info in logs.")
    }
  }
  logger.info("***** VT-emailer finished *****")

}
