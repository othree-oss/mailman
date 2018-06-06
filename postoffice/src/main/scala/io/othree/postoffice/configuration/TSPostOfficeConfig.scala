package io.othree.postoffice.configuration

import com.typesafe.config.Config
import io.othree.dna.TSConfigurationProvider

class TSPostOfficeConfig(config: Config)
  extends TSConfigurationProvider(config)
    with PostOfficeConfig {

  override def fromEmailAddress: String = getProperty("mailman.post-office.from-email-address")
}
