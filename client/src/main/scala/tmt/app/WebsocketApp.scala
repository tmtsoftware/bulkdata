package tmt.app

import org.scalajs.dom._
import tmt.views.{StreamView, SubscriptionView, ThrottleView}

import scala.async.Async._
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object WebsocketApp extends JSApp {
  
  val refreshDelay = 5.seconds
  val ec = JSExecutionContext.runNow
  val scheduler = monifu.concurrent.Implicits.globalScheduler

  @JSExport
  override def main() = async {
    val dataStore = new DataStore()
    dataStore.schedule(refreshDelay)

    val subscriptionDiv = new SubscriptionView(dataStore.data).frag.render
    val streamDiv = new StreamView(dataStore.data).frag.render
    val throttleDiv = new ThrottleView(dataStore.data).frag.render

    document.body.appendChild(throttleDiv)
    document.body.appendChild(subscriptionDiv)
    document.body.appendChild(streamDiv)
  }
}
