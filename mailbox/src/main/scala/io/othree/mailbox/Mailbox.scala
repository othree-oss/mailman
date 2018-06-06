package io.othree.mailbox

import io.othree.envelope.models.Envelope

trait Mailbox {
  def post(envelope: Envelope) : Unit
}
