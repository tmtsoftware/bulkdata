package templates

import tmt.shared.models.{ConnectionDataSet, RoleMappings}

import scalatags.Text.all._
import Helpers._

class SubscriptionView(roleMappings: RoleMappings, connectionDataSet: ConnectionDataSet) extends View {

  def frag = div(
    serverSelection("role1", "server1"),
    label("Subscribed by"),
    serverSelection("role2", "server2"),
    button(id := "subscribe", `type` := "button")("Connect"),
    initialConnections
  )

  private def initialConnections = ul(id := "connections") {
    connectionDataSet.flatConnections.map { case (serverName, topic) =>
      li(
        s"$serverName is subscribed to $topic",
        button(data := s"/$serverName/unsubscribe/$topic")("unsubscribe")
      )
    }
  }

  private def serverSelection(roleId: String, serverId: String) = Seq(
    select(id := roleId)(
      optionHint("select role"),
      makeOptions(roleMappings.roles)
    ),
    div(id := serverId)
  )
}
