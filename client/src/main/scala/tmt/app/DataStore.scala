package tmt.app

import monifu.concurrent.Scheduler
import org.scalajs.dom.ext.Ajax
import prickle._
import rx._
import tmt.common.Constants
import tmt.shared.models._

import scala.async.Async._
import scala.concurrent.duration._

class DataStore(implicit scheduler: Scheduler) {

  scheduleRefresh(Constants.RefreshRate)

  private val roleIndex = Var(Topology.empty)
  private val connectionSet = Var(ConnectionSet.empty)

  val data = new ViewData(roleIndex, connectionSet)

  def scheduleRefresh(delay: FiniteDuration) = {
    scheduler.scheduleWithFixedDelay(0.seconds, delay) {
      async {
        val rIndex = Unpickle[Topology].fromString(await(Ajax.get("/mappings")).responseText).get
        val cSet = Unpickle[ConnectionSet].fromString(await(Ajax.get("/connections")).responseText).get
        roleIndex() = rIndex
        connectionSet() = cSet
      }
    }
  }
}
