package fi.vm.sade.vt.emailer.ryhmasahkoposti

import fi.vm.sade.security.cas.{CasClient, CasConfig, CasTicketRequest}
import fi.vm.sade.utils.http.DefaultHttpClient
import fi.vm.sade.utils.slf4j.Logging
import fi.vm.sade.vt.emailer.config.{ApplicationSettingsComponent, ApplicationSettings}
import fi.vm.sade.vt.emailer.json.JsonFormats
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization

import scalaj.http.HttpOptions


trait GroupEmailComponent {
  this: ApplicationSettingsComponent =>

  val groupEmailService: GroupEmailService

  class RemoteGroupEmailService extends GroupEmailService with JsonFormats with Logging {
    private val jsessionPattern = """(^JSESSIONID=[^;]+)""".r
    private lazy val casClient = new CasClient(new CasConfig(settings.casUrl))
    private val httpOptions = Seq(HttpOptions.connTimeout(10000), HttpOptions.readTimeout(90000))

    def send(email: GroupEmail): Option[String] = {
      sessionRequest match {
        case Some(sessionId) => {
          val groupEmailRequest = DefaultHttpClient.httpPost(settings.groupEmailServiceUrl, Some(Serialization.write(email)), httpOptions: _*)
            .header("Cookie", sessionId)
            .header("Content-type", "application/json")

          logger.info(s"Sending email to ${settings.groupEmailServiceUrl}")
          groupEmailRequest.response() match {
            case Some(json) => {
              val jobId = (parse(json) \ "id").extractOpt[String]
              logger.info(s"Batch sent successfully, jobId: $jobId")
              jobId
            }
            case _ => {
              logger.error(s"Batch sending failed. Service failure.")
              None
            }
          }
        }
        case _ => {
          logger.error(s"Batch sending failed. Failed to get a CAS session going.")
          None
        }
      }
    }

    private def sessionRequest: Option[String] = {
      val ticketRequest = casClient.getServiceTicket(
        new CasTicketRequest(settings.groupEmailCasUrl, settings.groupEmailCasUsername, settings.groupEmailCasPassword)
      )

      ticketRequest match {
        case Some(casTicket) => {
          val sessionRequest = DefaultHttpClient.httpGet(settings.groupEmailSessionUrl).param("ticket", casTicket)
          for {
            setCookieHeader <- {
              val responseWithHeaders: (Int, Map[String, List[String]], String) = sessionRequest.responseWithHeaders()
              responseWithHeaders._2.get("Set-Cookie")
            }
            jsessionidCookie <- setCookieHeader.find(_.startsWith("JSESSIONID"))
            cookieString <- jsessionPattern.findFirstIn(jsessionidCookie)
          } yield cookieString
        }
        case _ => None
      }
    }
  }

  class FakeGroupEmailService extends GroupEmailService with Logging with JsonFormats {
    private var lastEmailSize = 0
    def getLastEmailSize() = lastEmailSize
    override def send(email: GroupEmail): Option[String] = {
      logger.info(s"Sending email: ${Serialization.write(email)}")
      lastEmailSize = email.recipient.size
      Some("Thank you for using fake group email service")
    }
  }
}


