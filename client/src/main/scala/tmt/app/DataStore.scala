package tmt.app

import monifu.concurrent.Scheduler
import org.scalajs.dom.ext.Ajax
import prickle._
import rx._
import tmt.common.Constants
import tmt.shared.Topics
import tmt.shared.models._

import scala.async.Async._
import scala.concurrent.duration._

class DataStore(implicit scheduler: Scheduler) {

  scheduleRefresh(Constants.RefreshRate)

  private val nodeSet = Var(NodeSet.empty)
  private val connectionSet = Var(ConnectionSet.empty)

  val data = new ViewData(nodeSet, connectionSet)

  def scheduleRefresh(delay: FiniteDuration) = {
    scheduler.scheduleWithFixedDelay(0.seconds, delay) {
      async {
        val nSet = Unpickle[NodeSet].fromString(await(Ajax.get(Topics.Nodes)).responseText).get
        val cSet = Unpickle[ConnectionSet].fromString(await(Ajax.get(Topics.Connections)).responseText).get
        nodeSet() = nSet
        connectionSet() = cSet
      }
    }
  }
}
