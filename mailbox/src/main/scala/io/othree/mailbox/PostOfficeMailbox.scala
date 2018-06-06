package io.othree.mailbox

import akka.actor.{ActorPath, ActorSystem}
import akka.cluster.client.ClusterClient.Send
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import io.othree.envelope.models.Envelope
import io.othree.mailbox.configuration.PostOfficeMailboxConfiguration

class PostOfficeMailbox(mailboxName: String,
                        config: PostOfficeMailboxConfiguration)
                       (implicit actorSystem: ActorSystem)
  extends Mailbox {

  private val clusterSeed = Set(ActorPath.fromString(config.postOfficeURI))
  val clusterClient = actorSystem.actorOf(ClusterClient.props(ClusterClientSettings(actorSystem).withInitialContacts(clusterSeed)), mailboxName)

  override def post(envelope: Envelope): Unit = {
    clusterClient ! Send(config.postOfficeActorPath, envelope, true)
  }
}
