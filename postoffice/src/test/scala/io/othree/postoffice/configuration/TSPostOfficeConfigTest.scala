package io.othree.postoffice.configuration

import com.typesafe.config.ConfigFactory
import io.othree.aok.BaseTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TSPostOfficeConfigTest extends BaseTest {

  var postOfficeConfig : PostOfficeConfig = _

  override protected def beforeAll(): Unit = {
    postOfficeConfig = new TSPostOfficeConfig(ConfigFactory.load())
  }

  "PostOfficeConfig" when {
    "asked for the fromEmailAddress" must {
      "return the configured value" in {
        val fromEmailAddress = postOfficeConfig.fromEmailAddress

        fromEmailAddress shouldBe "info@o3.cr"
      }
    }
  }
}
