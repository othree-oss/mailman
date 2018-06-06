package io.othree.pidgeon.postmark

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import io.othree.pidgeon.Pidgeon
import io.othree.pidgeon.models.{Package, To}
import io.othree.pidgeon.postmark.configuration.PostmarkConfiguration
import io.othree.pidgeon.postmark.models.{Attachment, Email, SuccessfulEmailResponse}

import scala.concurrent.{ExecutionContext, Future}

class PostmarkPidgeon(configuration: PostmarkConfiguration)
                     (implicit actorSystem: ActorSystem, ec: ExecutionContext)
  extends Pidgeon
    with PostmarkJsonSupport
    with LazyLogging {

  private implicit val actorMaterializer = ActorMaterializer()

  private val headers = scala.collection.immutable.Seq(
    RawHeader("X-Postmark-Server-Token", configuration.serverToken),
    RawHeader("Accept", "application/json"))

  override def sendEmail(from: String,
                         to: To,
                         subject: String,
                         body: String,
                         isHTML: Boolean,
                         packages: Option[Array[Package]] = None): Future[Boolean] = {
    val attachments = packages.map(packages => {
      packages.map(p => {
        Attachment(p.name, p.base64Content, p.contentType, s"cid:${p.name}")
      })
    })
    val email = Email(
      from,
      to.to,
      to.cc,
      to.bcc,
      subject,
      body,
      attachments
    )

    val eventualResponse = for {
      body <- Marshal(email).to[RequestEntity]
      response <- Http().singleRequest(HttpRequest(method = HttpMethods.POST,
        uri = configuration.uri,
        headers = headers,
        entity = body))
    } yield response

    eventualResponse.flatMap { response =>
      if (response.status == StatusCodes.OK) {
        val eventualEmailResponse = Unmarshal(response.entity).to[SuccessfulEmailResponse]
        eventualEmailResponse.map { emailResponse =>
          logger.info(s"Email successfully sent to: ${emailResponse.to}. Message Id: ${emailResponse.messageID}. Message: ${emailResponse.message}")
          true
        }
      } else {
        val eventualResponseBody = Unmarshal(response.entity).to[String]
        eventualResponseBody.map { body =>
          logger.error(s"Failed to send email: $body")
          false
        }
      }
    }
  }
}
