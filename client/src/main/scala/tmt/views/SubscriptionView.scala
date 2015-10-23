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

  def frag = div(cls := "form-group")(
    label("Make Connection"),
    label("Select output server"),
    makeSelection(dataStore.producers, topicName),
    "====>",
    label("Select input server"),
    makeSelection(consumers, serverName),
    button(onclick := {() => addConnection()})("Connect"),
    Rx {
      ul(id := "connections")(
        connectionSet().connections.toSeq.map { c  =>
          li(
            s"${c.topic} ===> ${c.server}",
            button(onclick := {() => removeConnection(c)})("unsubscribe")
          )
        }
      )
    }
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
