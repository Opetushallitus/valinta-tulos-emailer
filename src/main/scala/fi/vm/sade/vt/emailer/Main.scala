package fi.vm.sade.vt.emailer

import fi.vm.sade.vt.emailer.config.Registry
import fi.vm.sade.vt.emailer.config.Registry.Registry
import fi.vm.sade.vt.emailer.util.{Logging, Timer}

object Main extends App {
  val registry: Registry = Registry.fromString(Option(System.getProperty("vtemailer.profile")).getOrElse("default"))
  registry.start
  new Main(registry).start
}

class Main(registry: Registry) extends Logging {
  def start: Unit = {
    logger.info("***** VT-emailer started *****")
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
}