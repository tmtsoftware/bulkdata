package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.shared.models.{Role, Connection, ConnectionSet, RoleMappingSet}

import scalatags.JsDom.all._

class SubscriptionView(roleMappings: RoleMappingSet, connectionSet: ConnectionSet) {

  val serverName = Var("")
  val topicName = Var("")

  val connection = Rx(Connection(serverName(), topicName()))

  val connections = Var(connectionSet.flatConnections)
  val sortedConnections = Rx(connections().toSeq.sortBy(c => (c.server, c.topic)))

  def frag = div(
    selectOf(topicName), label("Subscribed by"), selectOf(serverName),
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

  def selectOf(item: Var[String]) = {
    val role = Var("")
    div(
      select(onchange := setValue(role))(
        optionHint("select role"),
        makeOptions(roleMappings.roles.map(_.entryName))
      ),
      Rx {
        select(onchange := setValue(item))(
          optionHint("select-server"),
          makeOptions(roleMappings.getServersByRole(Role.withName(role())))
        )
      }
    )
  }

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
