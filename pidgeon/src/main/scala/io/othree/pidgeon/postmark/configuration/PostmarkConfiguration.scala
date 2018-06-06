package io.othree.pidgeon.postmark.configuration

trait PostmarkConfiguration {
  def serverToken: String

  def uri: String
}
