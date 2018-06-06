package io.othree.mailbox.configuration

trait PostOfficeMailboxConfiguration {
  def postOfficeURI: String

  def postOfficeActorPath: String
}
