package tmt.app

import org.scalajs.dom.ext.Ajax
import prickle._
import rx._
import tmt.shared.models._

import scala.async.Async._
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

class DataStore {
  private val scheduler = monifu.concurrent.Implicits.globalScheduler

  private val roleIndex = Var(RoleIndex.empty)
  private val connectionSet = Var(ConnectionSet.empty)
  private val hostMappings = Var(HostMappings.empty)

  val data = new ViewData(roleIndex, connectionSet, hostMappings)

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
