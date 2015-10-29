package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.shared.models._

import scala.concurrent.ExecutionContext
import scalatags.JsDom.all._

class SubscriptionView(dataStore: ViewData)(implicit ec: ExecutionContext) extends View {

  val topicName = Var("")
  val serverName = Var("")
  val consumers = dataStore.consumersOf(topicName)

  val connection = Rx(Connection(serverName(), topicName()))

  val connectionSet = Var(ConnectionSet.empty)

  Obs(dataStore.connectionSet) {
    connectionSet() = dataStore.connectionSet()
  }

  val noData = Rx(
    topicName().isEmpty
      || serverName().isEmpty
      || consumers().isEmpty
      || dataStore.producers().isEmpty
      || !consumers().contains(serverName())
  )

  val disabledStyle = Rx(if(noData()) "disabled" else "")

  def viewTitle = h5("Connect")

  def viewContent = div(
    label("Output server"),
    makeSelection(dataStore.producers, topicName),

    label("Input server"),
    makeSelection(consumers, serverName)
  )

  def viewAction = div(
    a(cls := Rx(s"btn-floating waves-effect waves-light ${disabledStyle()}"))(
      onclick := { () => addConnection() },
      i(cls := "material-icons")("add")
    ),
    Rx {
      val rows = connectionSet().connections.toSeq.map(makeRow)
      table(tbody(rows))
    }
  )

  def makeRow(c: Connection) = tr(
    td(c.topic),
    td("====>"),
    td(c.server),
    td(
      a(cls := "btn-floating waves-effect waves-light red")(
        onclick := { () => removeConnection(c) },
        i(cls := "material-icons")("remove")
      )
    )
  )

  def addConnection() = {
    subscribe(connection()).onSuccess {
      case x if x.status == 202 =>  connectionSet() = connectionSet().add(connection())
    }
  }

  def removeConnection(connection: Connection) = {
    unsubscribe(connection)
    connectionSet() = connectionSet().remove(connection)
  }

  def subscribe(connection: Connection) = Ajax.post(s"${connection.server}/subscribe/${connection.topic}")

  def unsubscribe(connection: Connection) = Ajax.post(s"${connection.server}/unsubscribe/${connection.topic}")
}
