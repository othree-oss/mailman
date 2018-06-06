package io.othree.mailbox.configuration

import com.typesafe.config.Config
import io.othree.dna.TSConfigurationProvider

class TSPostOfficeMailboxConfiguration(config: Config)
  extends TSConfigurationProvider(config)
    with PostOfficeMailboxConfiguration {
  override def postOfficeURI: String = getProperty("mailman.mailbox.post-office.uri")

  override def postOfficeActorPath: String = getProperty("mailman.mailbox.post-office.actor-path")
}
