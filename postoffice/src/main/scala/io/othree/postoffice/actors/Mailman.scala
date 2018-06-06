package io.othree.postoffice.actors

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import io.othree.composer.TemplateComposer
import io.othree.envelope.models.Envelope
import io.othree.pidgeon.Pidgeon
import io.othree.pidgeon.models.{Package, To}
import io.othree.postoffice.configuration.PostOfficeConfig

import scala.concurrent.ExecutionContext.Implicits.global

class Mailman(config: PostOfficeConfig,
              composer: TemplateComposer,
              pidgeon: Pidgeon)
  extends Actor
    with LazyLogging {
  override def receive: Receive = {
    case envelope: Envelope =>

      val eventualEmailBody = composer.compose(envelope)

      eventualEmailBody.failed.foreach {
        case e: Throwable => logger.error(s"Failed to compose email for template ${envelope.templateId}", e)
      }

      eventualEmailBody flatMap { emailBody =>
        val attachments = envelope.attachments.map(packages => {
          packages.map(p => {
            Package(p.name,
              p.base64Content,
              p.contentType)
          })
        })

        val eventualResponse = pidgeon.sendEmail(config.fromEmailAddress,
          To(envelope.to,
            if(envelope.cc.isEmpty) {
              None
            } else {
              Some(envelope.cc.mkString(","))
            }),
          envelope.subject,
          emailBody,
          isHTML = true,
          attachments)

        eventualResponse.map { response =>
          if (!response) {
            logger.error(s"Failed to send email with template ${envelope.templateId}")
          } else {
            logger.info(s"Email with template ${envelope.templateId} successfully sent")
          }
        }
      }
  }
}
