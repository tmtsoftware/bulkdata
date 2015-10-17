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

  val connections = Var(Seq.empty[Connection])

  Obs(dataStore.connectionSet) {
    connections() = dataStore.connectionSet().connections.toSeq.distinct
  }

  def frag = div(
    "Make Connection",
    Rx {
      select(onchange := setValue(topicName))(
        optionHint(s"select output server"),
        makeOptions(dataStore.producers(), topicName())
      )
    },
    "====>",
    Rx {
      select(onchange := setValue(serverName))(
        optionHint(s"select input server"),
        makeOptions(consumers(), serverName())
      )
    },
    button(onclick := {() => addConnection()})("Connect"),
    Rx {
      ul(id := "connections")(
        connections().map { c  =>
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
      case x if x.status == 202 =>  connections() = (connections() :+ connection()).distinct
    }
  }

  def removeConnection(connection: Connection) = {
    unsubscribe(connection)
    connections() = connections().filterNot(_ == connection)
  }

  def subscribe(connection: Connection) = Ajax.post(s"${connection.server}/subscribe/${connection.topic}")

  def unsubscribe(connection: Connection) = Ajax.post(s"${connection.server}/unsubscribe/${connection.topic}")
}
