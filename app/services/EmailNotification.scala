package services

import org.apache.commons.mail._
import play.api.Play.current

object EmailNotification {

	val smtpsHost = current.configuration.getString("smtps.host").getOrElse("none")
	val smtpsPort = current.configuration.getString("smtps.port").getOrElse("25")
	val smtpsPortInt = Integer.valueOf(smtpsPort).intValue
	val smtpsSsl = current.configuration.getBoolean("smtps.ssl").getOrElse(false)
	val smtpsEmail = current.configuration.getString("smtps.email").getOrElse("props@nurun.com")
	
	def send( fromEmails: List[String], toEmails: List[String], subject: String, content: String) {
	
		val email = new HtmlEmail()
		email.setHostName(smtpsHost)
		email.setSslSmtpPort(smtpsPort)
		email.setSmtpPort(smtpsPortInt)
		email.setSSLOnConnect(smtpsSsl)

		try {
			email.setFrom(smtpsEmail)
      toEmails foreach { toEmail =>
        email.addTo(toEmail)
      }
      fromEmails foreach { fromEmail =>
        email.addCc(fromEmail)
      }
			email.setSubject(subject)
			email.setHtmlMsg(content)
			email.send()
			println("Props sent from [" + fromEmails.mkString(", ") + "] to ["  + toEmails.mkString(", ") + "]")
		} catch {
			case e: Exception =>
        println("oops")
        throw e
		}
	}
}
