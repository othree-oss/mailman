package io.othree.pidgeon.models

case class To(to: String,
              cc: Option[String] = None,
              bcc: Option[String] = None)
