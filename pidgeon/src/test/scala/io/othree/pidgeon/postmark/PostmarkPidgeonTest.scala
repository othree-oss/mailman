package io.othree.pidgeon.postmark

import akka.actor.ActorSystem
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import io.othree.aok.AsyncBaseTest
import io.othree.pidgeon.Pidgeon
import io.othree.pidgeon.models.{Package, To}
import io.othree.pidgeon.postmark.configuration.PostmarkConfiguration
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class PostmarkPidgeonTest extends AsyncBaseTest {

  var postmarkEmailClient: Pidgeon = _

  var wireMockServer: WireMockServer = _
  implicit var actorSystem: ActorSystem = _

  override protected def beforeAll(): Unit = {
    val port = 9000
    val host = "127.0.0.1"
    wireMockServer = new WireMockServer(options().port(port))
    wireMockServer.start()

    actorSystem = ActorSystem()

    WireMock.configureFor(host, port)

    stubFor(post(urlEqualTo("/email"))
      .withHeader("X-Postmark-Server-Token", equalTo("70k3n"))
      .withHeader("Content-type", equalTo("application/json"))
      .withHeader("Accept", equalTo("application/json"))
      .withRequestBody(equalTo("""{"HtmlBody":"<b>Body</b>","Subject":"title","Bcc":"email@o3.cr","Cc":"email@o3.cr","To":"email@o3.cr","From":"email@o3.cr"}"""))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-type", "application/json")
        .withBody("""{"To":"email@o3.cr","SubmittedAt":"2017-07-27T15:59:42.9832357-04:00","MessageID":"M-id","ErrorCode":1,"Message":"A message"}"""))
    )

    stubFor(post(urlEqualTo("/email"))
      .withHeader("X-Postmark-Server-Token", equalTo("70k3n"))
      .withHeader("Content-type", equalTo("application/json"))
      .withHeader("Accept", equalTo("application/json"))
      .withRequestBody(equalTo("""{"HtmlBody":"<b>Body</b>","Subject":"title","Attachments":[{"Name":"image","Content":"dGV4dA==","ContentType":".png","ContentId":"cid:image"}],"Bcc":"email@o3.cr","Cc":"email@o3.cr","To":"email@o3.cr","From":"email@o3.cr"}"""))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-type", "application/json")
        .withBody("""{"To":"email@o3.cr","SubmittedAt":"2017-07-27T15:59:42.9832357-04:00","MessageID":"M-id","ErrorCode":1,"Message":"A message"}"""))
    )


    stubFor(post(urlEqualTo("/email"))
      .withHeader("X-Postmark-Server-Token", equalTo("70k3n"))
      .withHeader("Content-type", equalTo("application/json"))
      .withHeader("Accept", equalTo("application/json"))
      .withRequestBody(equalTo("""{"HtmlBody":"<b>Body</b>","Subject":"title","Bcc":"email@o3.cr","Cc":"email@o3.cr","To":"bad@o3.cr","From":"email@o3.cr"}"""))
      .willReturn(aResponse()
        .withStatus(400)
        .withHeader("Content-type", "application/json")
        .withBody("""{"To":"bad@o3.cr","SubmittedAt":"2017-07-27T15:59:42.9832357-04:00","MessageID":"M-id","ErrorCode":1,"Message":"A message"}"""))
    )

    val configuration = mock[PostmarkConfiguration]
    when(configuration.uri).thenReturn("http://localhost:9000/email")
    when(configuration.serverToken).thenReturn("70k3n")

    postmarkEmailClient = new PostmarkPidgeon(configuration)
  }

  override protected def afterAll(): Unit = {
    wireMockServer.stop()
    actorSystem.terminate()
  }

  "PostmarkEmailClient" when {

    "sending a valid email with attachments" must {
      "return true" in {
        val eventualResponse = postmarkEmailClient.sendEmail("email@o3.cr", To("email@o3.cr", Some("email@o3.cr"), Some("email@o3.cr")), "title", "<b>Body</b>", true, Some(Array(Package("image", "dGV4dA==", ".png"))))

        eventualResponse.map { result =>
          result shouldBe true
        }
      }
    }

    "sending a valid email" must {
      "return true" in {

        val eventualResponse = postmarkEmailClient.sendEmail("email@o3.cr", To("email@o3.cr", Some("email@o3.cr"), Some("email@o3.cr")), "title", "<b>Body</b>", true, None)

        eventualResponse.map { result =>
          result shouldBe true
        }
      }
    }


    "sending an invalid email" must {
      "return false" in {

        val eventualResponse = postmarkEmailClient.sendEmail("bad@o3.cr", To("email@o3.cr", Some("email@o3.cr"), Some("email@o3.cr")), "title", "<b>Body</b>", true, None)

        eventualResponse.map { result =>
          result shouldBe false
        }
      }
    }
  }
}
