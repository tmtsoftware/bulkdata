package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.shared.models._

import scalatags.JsDom.all._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

class SubscriptionView(roleIndex: RoleIndex, connectionSet: ConnectionSet) {

  val topicName = Var("")
  val serverName = Var("")

  val matchingServers = Rx(roleIndex.compatibleConsumers(topicName()))
  
  val connection = Rx(Connection(serverName(), topicName()))
  val connections = Var(connectionSet.flatConnections)

  def frag = div(
    Rx {
      select(onchange := setValue(topicName))(
        optionHint(s"select output server"),
        makeOptions(roleIndex.producers)
      )
    },
    "====>",
    Rx {
      select(onchange := setValue(serverName))(
        optionHint(s"select input server"),
        makeOptions(matchingServers())
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
