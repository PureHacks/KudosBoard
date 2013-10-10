package services

import org.apache.commons.mail.{Email, EmailException, SimpleEmail}
import play.api.Play.current

object EmailNotification {

	val smtpsHost = current.configuration.getString("smtps.host").getOrElse("none")
	val smtpsPort = current.configuration.getString("smtps.port").getOrElse("25")
	val smtpsPortInt = Integer.valueOf(smtpsPort).intValue
	val smtpsSsl = current.configuration.getBoolean("smtps.ssl").getOrElse(false)
	val smtpsEmail = current.configuration.getString("smtps.email").getOrElse("props@nurun.com")
	
	def send( fromEmail: String, toEmail: String, subject: String, content: String) {
	
		val email = new SimpleEmail()
		email.setHostName(smtpsHost)
		email.setSslSmtpPort(smtpsPort)
		email.setSmtpPort(smtpsPortInt)
		email.setSSLOnConnect(smtpsSsl)

		try {
			email.setFrom(smtpsEmail)
			email.addTo(toEmail)
			email.addCc(fromEmail)
			email.setSubject(subject)
			email.setMsg(content)
			email.send()
			println("Props sent from [" + fromEmail + "] to ["  + toEmail + "]")
		} catch {
			case ee: EmailException =>
		}
	}
}
