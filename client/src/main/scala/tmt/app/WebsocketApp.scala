package tmt.app

import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax
import prickle._
import rx._
import tmt.shared.models.{ConnectionSet, HostMappings, RoleIndex}
import tmt.views.{StreamView, SubscriptionView, ThrottleView}

import scala.async.Async._
import scala.concurrent.duration._
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

object WebsocketApp extends JSApp {
  
  val delay = 5.seconds

  @JSExport
  override def main() = async {
    val poller = new Poller()
    poller.schedule(delay)

    val roleIndex = Unpickle[RoleIndex].fromString(await(Ajax.get("/mappings")).responseText).get
    val connectionSet = Unpickle[ConnectionSet].fromString(await(Ajax.get("/connections")).responseText).get
    val hostMappings = Unpickle[HostMappings].fromString(await(Ajax.get("/hosts")).responseText).get

    val subscriptionDiv = new SubscriptionView(poller.roleIndex, poller.connectionSet).frag.render
    val streamDiv = new StreamView(roleIndex, hostMappings).frag.render
    val throttleDiv = new ThrottleView(poller.roleIndex).frag.render

    document.body.appendChild(throttleDiv)
    document.body.appendChild(subscriptionDiv)
    document.body.appendChild(streamDiv)
  }
}


class Poller {
  private val scheduler = monifu.concurrent.Implicits.globalScheduler

  val roleIndex = Var(RoleIndex.empty)
  val connectionSet = Var(ConnectionSet.empty)
  val hostMappings = Var(HostMappings.empty)

  def schedule(delay: FiniteDuration) = {
    scheduler.scheduleWithFixedDelay(0.seconds, delay) {
      async {
        val rIndex = Unpickle[RoleIndex].fromString(await(Ajax.get("/mappings")).responseText).get
        val cSet = Unpickle[ConnectionSet].fromString(await(Ajax.get("/connections")).responseText).get
        val hMappings = Unpickle[HostMappings].fromString(await(Ajax.get("/hosts")).responseText).get
        roleIndex() = rIndex
        connectionSet() = cSet
        hostMappings() = hMappings
      }
    }
  }
}
