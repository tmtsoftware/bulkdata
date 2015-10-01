package tmt.actors

import javax.inject.Singleton

import akka.actor.PoisonPill
import akka.cluster.singleton.{ClusterSingletonManagerSettings, ClusterSingletonManager}
import com.google.inject.Inject
import tmt.app.ActorConfigs

@Singleton
class SingletonManager @Inject()(actorConfigs: ActorConfigs) {

  import actorConfigs._

  system.actorOf(
    ClusterSingletonManager.props(
      ConnectionStore.props(),
      PoisonPill,
      ClusterSingletonManagerSettings(system)
    ),
    "subscriptionState"
  )
}
