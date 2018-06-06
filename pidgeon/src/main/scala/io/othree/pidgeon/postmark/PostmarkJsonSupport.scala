package io.othree.pidgeon.postmark

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import io.othree.pidgeon.postmark.models.{Attachment, Email, SuccessfulEmailResponse}
import spray.json.DefaultJsonProtocol

trait PostmarkJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val attachmentsSupport = jsonFormat(Attachment, "Name", "Content", "ContentType", "ContentId")
  implicit val emailJsonSupport = jsonFormat(Email, "From", "To", "Cc", "Bcc", "Subject", "HtmlBody", "Attachments")
  implicit val successfulEmailResponseJsonSupport = jsonFormat(SuccessfulEmailResponse, "To", "SubmittedAt", "MessageID", "ErrorCode", "Message")
}
