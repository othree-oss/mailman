package io.othree.composer.configuration

import com.typesafe.config.ConfigFactory
import io.othree.aok.BaseTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TSMustacheComposerConfigurationTest extends BaseTest {

  var mustacheComposerConfiguration : MustacheComposerConfiguration = _

  override protected def beforeAll(): Unit = {
    mustacheComposerConfiguration = new TSMustacheComposerConfiguration(ConfigFactory.load())
  }

  "MustacheComposerConfiguration" when {
    "getting the buildDir" must {
      "return the configured value" in {
        val buildDir = mustacheComposerConfiguration.buildDir

        buildDir shouldBe "/some/path/for/binaries"
      }
    }

    "getting the templatesDir" must {
      "return the configured value" in {
        val templatesDir = mustacheComposerConfiguration.templatesDir

        templatesDir shouldBe "/the/templates/path"
      }
    }
  }
}
