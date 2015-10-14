package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import rx.ops._
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.shared.models._

import scalatags.JsDom.all._

class SubscriptionView(roleIndex: RoleIndex, connectionSet: ConnectionSet) {

  val itemTypeValue = Var("")
  val itemType = Rx(ItemType.withName(itemTypeValue()))

  val topicName = Var("")
  val serverName = Var("")

  val connection = Rx(Connection(serverName(), topicName()))

  val connections = Var(connectionSet.flatConnections)

  def frag = div(
    select(onchange := setValue(itemTypeValue))(
      optionHint(s"select item type"),
      makeOptions(roleIndex.itemTypes.map(_.entryName))
    ), br, br,
    Rx {
      select(onchange := setValue(topicName))(
        optionHint(s"select output server"),
        makeOptions(roleIndex.outputTypeIndex.getServers(itemType()))
      )
    },
    "====>",
    Rx {
      select(onchange := setValue(serverName))(
        optionHint(s"select input server"),
        makeOptions(roleIndex.inputTypeIndex.getServers(itemType()).filterNot(_ == topicName()))
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
    subscribe(connection())
    connections() = (connections() :+ connection()).distinct
  }

  def removeConnection(connection: Connection) = {
    unsubscribe(connection)
    connections() = connections().filterNot(_ == connection)
  }

  def subscribe(connection: Connection) = Ajax.post(s"${connection.server}/subscribe/${connection.topic}")

  def unsubscribe(connection: Connection) = Ajax.post(s"${connection.server}/unsubscribe/${connection.topic}")
}
