package tmt.app

import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax
import prickle._
import tmt.shared.models.{ConnectionSet, HostMappings, RoleIndex}
import tmt.views.{StreamView, SubscriptionView, ThrottleView}

import scala.async.Async._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = async {
    val roleIndex = Unpickle[RoleIndex].fromString(await(Ajax.get("/mappings")).responseText).get
    val connectionSet = Unpickle[ConnectionSet].fromString(await(Ajax.get("/connections")).responseText).get
    val hostMappings = Unpickle[HostMappings].fromString(await(Ajax.get("/hosts")).responseText).get

    val subscriptionDiv = new SubscriptionView(roleIndex, connectionSet).frag.render
    val streamDiv = new StreamView(roleIndex, hostMappings).frag.render
    val throttleDiv = new ThrottleView(roleIndex).frag.render

    document.body.appendChild(throttleDiv)
    document.body.appendChild(subscriptionDiv)
    document.body.appendChild(streamDiv)
  }
}
