package io.othree.postoffice.actors

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.testkit.{ImplicitSender, TestKit}
import io.othree.akkaok.BaseAkkaTest
import io.othree.aok.matchers.AnyMatcher
import io.othree.composer.TemplateComposer
import io.othree.composer.exceptions.TemplateNotFoundException
import io.othree.pidgeon.Pidgeon
import io.othree.pidgeon.models.{Package, To}
import io.othree.postoffice.configuration.PostOfficeConfig
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.{eq => mockEq, _}
import org.mockito.Mockito._
import org.scalatest.junit.JUnitRunner

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import io.othree.envelope.models.{Envelope, Package => PackageEnvelope}

@RunWith(classOf[JUnitRunner])
class MailmanTest
  extends BaseAkkaTest(ActorSystem("mailman"))
    with ImplicitSender {

  case class TestEmail(something: String, override val templateId: String = "validTemplate", override val attachments: Option[Array[PackageEnvelope]] = None) extends Envelope {
    override val to: String = "email@o3.cr"
    override val subject: String = "Awesome email"

    override def content: Map[String, Any] = {
      Map(("something", something))
    }
  }

  var mailmanActor: ActorRef = _
  var pidgeon: Pidgeon = _

  before {
    val config = mock[PostOfficeConfig]
    when(config.fromEmailAddress).thenReturn("info@o3.cr")

    val composer = mock[TemplateComposer]
    when(composer.compose(AnyMatcher[Envelope](x => x != null && x.templateId == "validTemplate"))).thenReturn(Future {"<marquee>Template!</marquee>"})
    when(composer.compose(AnyMatcher[Envelope](x => x != null && x.templateId == "validAttachments"))).thenReturn(Future {"<marquee>Template!</marquee>"})
    when(composer.compose(AnyMatcher[Envelope](x => x != null && x.templateId == "invalidTemplate"))).thenReturn(Future { throw new TemplateNotFoundException("invalidTemplate", "error!") })

    pidgeon = mock[Pidgeon]
    when(pidgeon.sendEmail(any(), any(), any(), any(), any(), any())).thenReturn(Future {
      true
    })

    mailmanActor = system.actorOf(Props(new Mailman(config, composer, pidgeon)))
  }

  after {
    mailmanActor ! PoisonPill
  }

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Mailman" when {
    "asked to send a valid email" must {
      "send it" in {

        mailmanActor ! TestEmail("value")

        expectNoMessage(500 milliseconds)

        verify(pidgeon, times(1)).sendEmail("info@o3.cr", To("email@o3.cr"), "Awesome email", "<marquee>Template!</marquee>", isHTML = true, None)

      }
    }

    "asked to send a valid email with attachments" must {
      "send it" in {

        mailmanActor ! TestEmail("value", "validAttachments", Some(Array(PackageEnvelope("image", "base64Content", ".png"))))

        expectNoMessage(500 milliseconds)

        verify(pidgeon, times(1)).sendEmail(
          mockEq("info@o3.cr"),
          mockEq(To("email@o3.cr")),
          mockEq("Awesome email"),
          mockEq("<marquee>Template!</marquee>"),
          mockEq(true),
          AnyMatcher[Option[Array[Package]]](packages => {
            packages shouldBe defined

            packages.get(0) shouldBe Package("image","base64Content",".png")
            true
          })
        )
      }
    }

    "asked to send an invalid email" must {
      "not send it" in {

        mailmanActor ! TestEmail("something", "invalidTemplate")

        expectNoMessage(500 milliseconds)

        verifyNoMoreInteractions(pidgeon)
      }
    }
  }
}
