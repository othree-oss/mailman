package io.othree.mailbox

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.cluster.client.ClusterClientReceptionist
import akka.testkit.{TestKit, TestProbe}
import io.othree.akkaok.BaseAkkaTest
import io.othree.envelope.models.{Envelope, Package}
import io.othree.mailbox.configuration.PostOfficeMailboxConfiguration
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PostOfficeMailboxTest extends BaseAkkaTest(ActorSystem("PostOfficeMailboxTest")) {

  case class TestEnvelope(name: String) extends Envelope {
    override def content: Map[String, Any] = {
      Map(("name", name))
    }

    override val templateId: String = "template"
    override val to: String = "info@o3.cr"
    override val subject: String = "subject"
    override val attachments: Option[Array[Package]] = None
  }

  var mailbox : Mailbox = _
  var postOfficeActor : TestProbe = _

  override protected def beforeAll(): Unit = {
    Cluster(system).registerOnMemberUp {
      postOfficeActor = TestProbe()
      ClusterClientReceptionist(system).registerService(postOfficeActor.ref)
    }


    val config = mock[PostOfficeMailboxConfiguration]
    when(config.postOfficeURI).thenReturn("akka.tcp://PostOfficeMailboxTest@127.0.0.1:2651/system/receptionist")
    when(config.postOfficeActorPath).thenReturn(postOfficeActor.ref.path.toStringWithoutAddress)

    mailbox = new PostOfficeMailbox("testMailBox", config)
  }

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "PostOfficeMailbox" when {
    "sending an envelope" must {
      "send the message to the corresponding actor in the cluster" in {

        mailbox.post(TestEnvelope("test"))

        postOfficeActor.expectMsg(TestEnvelope("test"))
      }
    }
  }
}
