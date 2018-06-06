package io.othree.pidgeon

import io.othree.pidgeon.models.{Package, To}

import scala.concurrent.Future

trait Pidgeon {
  def sendEmail(from: String, to: To, subject: String, body: String, isHTML: Boolean, attachments: Option[Array[Package]] = None): Future[Boolean]
}
