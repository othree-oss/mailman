package io.othree.envelope.models

trait Envelope {
  def content: scala.collection.immutable.Map[String, Any]
  val templateId: String
  val to: String
  val subject: String
  val cc: Array[String] = Array()
  val attachments: Option[Array[Package]] = None
}
