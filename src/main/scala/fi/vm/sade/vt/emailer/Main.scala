package fi.vm.sade.vt.emailer

import fi.vm.sade.vt.emailer.config.Registry
import fi.vm.sade.vt.emailer.config.Registry.Registry

object Main extends App {
  val registry: Registry = Registry.fromString(Option(System.getProperty("valintatulos.profile")).getOrElse("default"))
  registry.start

  registry.mailer.sendBatch() match {
    case Some(jobId) => println(s"Job sent succesfully, jobId: $jobId")
    case _ => println("Job failed. More info in logs.")
  }
}
