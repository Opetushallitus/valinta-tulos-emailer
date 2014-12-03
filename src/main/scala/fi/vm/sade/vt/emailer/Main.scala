package fi.vm.sade.vt.emailer

import fi.vm.sade.vt.emailer.config.Registry
import fi.vm.sade.vt.emailer.config.Registry.Registry
import fi.vm.sade.vt.emailer.util.Timer

object Main extends App {
  val registry: Registry = Registry.fromString(Option(System.getProperty("valintatulos.profile")).getOrElse("default"))
  registry.start
  Timer.timed("Batch send") {
   val ids =  registry.mailer.sendMail
    if (ids.nonEmpty) {
      println(s"Job sent succesfully, jobId: $ids")
    } else {
      println("Job failed. More info in logs.")
    }
  }
}
