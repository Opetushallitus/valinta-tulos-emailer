package fi.vm.sade.vt.emailer.util

import java.nio.file.{Files, Paths}

import fi.vm.sade.utils.slf4j.Logging
import fi.vm.sade.utils.tcp.PortChecker

object ValintatulosServiceRunner extends Logging {
  import scala.sys.process._

  val valintatulosPort = sys.props.getOrElse("valintatulos.port", PortChecker.findFreeLocalPort.toString).toInt

  val searchPaths = List("./valinta-tulos-service/valinta-tulos-service", "../valinta-tulos-service/valinta-tulos-service")
  var currentRunner: Option[scala.sys.process.Process] = None

  def start = this.synchronized {
    if (currentRunner == None && PortChecker.isFreeLocalPort(valintatulosPort)) {
      findValintatulosService match {
        case Some(path) => {
          logger.info("Starting valinta-tulos-service from " + path + " on port "+ valintatulosPort)

          val cwd = new java.io.File(path)
          var javaHome = System.getProperty("JAVA8_HOME", System.getenv("JAVA_HOME"))
          if (javaHome == null || javaHome.contains("{")) {
            javaHome ="";
          }
          val mvn = System.getProperty("mvn", "mvn");
          logger.info("Using java home:" + javaHome);

          val process = Process(List(mvn, "test-compile", "exec:java@local_jetty", "-Dvalintatulos.port=" + valintatulosPort, "-Dvalintatulos.profile=it", "-Dfile.encoding=UTF-8"), cwd, "JAVA_HOME" -> javaHome).run(true)

          for (i <- 0 to 300 if PortChecker.isFreeLocalPort(valintatulosPort)) {
            Thread.sleep(1000)
          }
          if (PortChecker.isFreeLocalPort(valintatulosPort)) {
            throw new RuntimeException("Valinta-tulos-service did not start in 300 seconds")
          }
          currentRunner = Some(process)
          sys.addShutdownHook { ValintatulosServiceRunner.stop }
        }
        case _ =>
          logger.error("******* valinta-tulos-service not found ********")
      }
    } else {
      logger.info("Not starting valinta-tulos-service: seems to be running on port " + valintatulosPort)
    }
  }

  def stop = this.synchronized {
    logger.info("Stoping valinta-tulos-service")
    currentRunner.foreach(_.destroy)
  }

  private def findValintatulosService = {
    searchPaths.find((path) => Files.exists(Paths.get(path)))
  }
}
