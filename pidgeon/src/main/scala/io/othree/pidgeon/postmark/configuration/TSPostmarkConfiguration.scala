package io.othree.pidgeon.postmark.configuration

import com.typesafe.config.Config
import io.othree.dna.TSConfigurationProvider

class TSPostmarkConfiguration(config: Config)
  extends TSConfigurationProvider(config)
  with PostmarkConfiguration {

  override def serverToken: String = getProperty("mailman.pidgeon.postmark.server-token")

  override def uri: String = getProperty("mailman.pidgeon.postmark.uri")
}
