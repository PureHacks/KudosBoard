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


  private def mailMessage(senders: String, url: String): String =
    s"Props to you! You have received Kudos from $senders. You can see it <a href='$url'>here</a>."


  def sendNotification(card_id: Int) = {
    CardService.getCard(card_id) map { card =>
      val senders = card.senders.map(_.email)
      val appRoot = current.configuration.getString("appRoot").getOrElse("")
      card.recipients foreach { recipient =>
        val url = s"$appRoot/#/user/${recipient.userName}"
        val subject = "Props to you!"
        val message = mailMessage(senders.mkString(", "), url)
        println(s"$recipient: $message")
        EmailNotification.send(senders, List(recipient.email), subject, message)
      }
    }
  }
}
