package io.othree.mailbox.configuration

import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import io.othree.aok.BaseTest

@RunWith(classOf[JUnitRunner])
class TSPostOfficeMailboxConfigurationTest extends BaseTest {

  var postOfficeMailboxConfiguration : PostOfficeMailboxConfiguration = _

  override protected def beforeAll(): Unit = {
    postOfficeMailboxConfiguration = new TSPostOfficeMailboxConfiguration(ConfigFactory.load())
  }

  "PostOfficeMailboxConfiguration" when {
    "asked for the post office uri" must {
      "return the configured value" in {
        val uri = postOfficeMailboxConfiguration.postOfficeURI

        uri shouldBe "akka.tcp://PostOfficeMailboxTest@127.0.0.1:2651/system/receptionist"
      }
    }

    "asked for the actor path" must {
      "return the configured value" in {
        val actorPath = postOfficeMailboxConfiguration.postOfficeActorPath

        actorPath shouldBe "/user/mailman"
      }
    }
  }
}
