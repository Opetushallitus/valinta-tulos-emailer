package fi.vm.sade.vt.emailer.ryhmasahkoposti

import fi.vm.sade.security.cas.{CasClient, CasConfig, CasTicketRequest}
import fi.vm.sade.vt.emailer.DefaultHttpClient
import fi.vm.sade.vt.emailer.config.ApplicationSettings
import org.json4s.NoTypeHints
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization

class GroupEmailService(val settings: ApplicationSettings) {
  private val jsessionPattern = """(^JSESSIONID=[^;]+)""".r
  private lazy val casClient = new CasClient(new CasConfig(settings.casUrl))
  implicit val formats = Serialization.formats(NoTypeHints)

  def send(email: GroupEmail): Option[String] = {
    sessionRequest match {
      case Some(sessionId) => {
        val groupEmailRequest = DefaultHttpClient.httpPost(settings.groupEmailServiceUrl,
          Some(Serialization.write(GroupEmail("oid:123", "sähköposti@example.com", "FI", "Erkki", "1.1.2016"))))
          .header("Cookie", sessionId)
          .header("Content-type", "application/json")

        groupEmailRequest.response() match {
          case Some(json) => (parse(json) \ "id").extractOpt[String]
          case _ => None
        }
      }
      case _ => None
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
          setCookieHeader <- sessionRequest.responseWithHeaders()._2.get("Set-Cookie")
          jsessionidCookie <- setCookieHeader.find(_.startsWith("JSESSIONID"))
          cookieString <- jsessionPattern.findFirstIn(jsessionidCookie)
        } yield cookieString
      }
      case _ => None
    }
  }
}
