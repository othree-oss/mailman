package io.othree.composer

import io.othree.composer.exceptions.TemplateNotFoundException
import io.othree.envelope.models.Envelope

import scala.concurrent.Future

trait TemplateComposer {
  @throws(classOf[TemplateNotFoundException])
  def compose(envelope: Envelope) : Future[String]
}
