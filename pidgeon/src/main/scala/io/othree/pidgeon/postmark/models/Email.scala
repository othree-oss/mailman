package io.othree.pidgeon.postmark.models

case class Email(from: String,
                 to: String,
                 cc: Option[String],
                 bcc: Option[String],
                 subject: String,
                 htmlBody: String,
                 attachments: Option[Array[Attachment]])
