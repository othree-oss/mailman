package io.othree.composer.configuration

import com.typesafe.config.Config
import io.othree.dna.TSConfigurationProvider

class TSMustacheComposerConfiguration(config: Config)
  extends TSConfigurationProvider(config)
    with MustacheComposerConfiguration {
  override def buildDir: String = getProperty("mailman.composer.mustache.build-dir")

  override def templatesDir: String = getProperty("mailman.composer.mustache.templates-dir")
}
