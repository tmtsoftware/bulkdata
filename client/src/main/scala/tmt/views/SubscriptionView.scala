package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.shared.models._

import scalatags.JsDom.all._

class SubscriptionView(roleIndex: RoleIndex, connectionSet: ConnectionSet) {

  val itemTypeValue = Var("")
  val itemType = Rx(ItemType.withName(itemTypeValue()))

  val serverName = Var("")
  val topicName = Var("")

  val connection = Rx(Connection(serverName(), topicName()))

  val connections = Var(connectionSet.flatConnections)
  val sortedConnections = Rx(connections().toSeq.sortBy(c => (c.server, c.topic)))

  def frag = div(
    select(onchange := setValue(itemTypeValue))(
      optionHint(s"select item type"),
      makeOptions(roleIndex.itemTypes.map(_.entryName))
    ),
    Rx {
      select(onchange := setValue(topicName))(
        optionHint(s"select output server"),
        makeOptions(roleIndex.outputTypeIndex.getServers(itemType()))
      )
    },
    Rx {
      select(onchange := setValue(serverName))(
        optionHint(s"select input server"),
        makeOptions(roleIndex.inputTypeIndex.getServers(itemType()))
      )
    },
    button(onclick := {() => addConnection()})("Connect"),
    Rx {
      ul(id := "connections")(
        sortedConnections().map { c  =>
          li(
            s"${c.server} is subscribed to ${c.topic}",
            button(onclick := {() => removeConnection(c)})("unsubscribe")
          )
        }
      )
    }
  )

  def addConnection() = {
    subscribe(connection())
    connections() = connections() + connection()
  }

  def removeConnection(connection: Connection) = {
    unsubscribe(connection)
    connections() = connections() - connection
  }

  def subscribe(connection: Connection) = Ajax.post(s"${connection.server}/subscribe/${connection.topic}")

  def unsubscribe(connection: Connection) = Ajax.post(s"${connection.server}/unsubscribe/${connection.topic}")
}
