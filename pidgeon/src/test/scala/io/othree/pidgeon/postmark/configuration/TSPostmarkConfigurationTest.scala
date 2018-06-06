package io.othree.pidgeon.postmark.configuration

import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import io.othree.aok.BaseTest

@RunWith(classOf[JUnitRunner])
class TSPostmarkConfigurationTest extends BaseTest {

  var configuration : PostmarkConfiguration = _

  override protected def beforeAll(): Unit = {
    configuration = new TSPostmarkConfiguration(ConfigFactory.load())
  }

  "TSPostmarkConfiguration" when {
    "asked for the server token" must {
      "return the configured value" in {
        val serverToken = configuration.serverToken

        serverToken shouldBe "POSTMARK_API_TEST"
      }
    }

    "asked for the uri" must {
      "return the configured value" in {
        val uri = configuration.uri

        uri shouldBe "https://api.postmarkapp.com/email"
      }
    }
  }
}
