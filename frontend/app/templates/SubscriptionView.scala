package templates

import tmt.shared.models.RoleMappings

import scalatags.Text.all._
import Helpers._

class SubscriptionView(roleMappings: RoleMappings) extends View {
  def frag = div(
    serverSelection("role1", "server1"),
    label("Subscribed by"),
    serverSelection("role2", "server2"),
    button(id := "subscribe", `type` := "button")("Connect")
  )

  private def serverSelection(roleId: String, serverId: String) = Seq(
    select(id := roleId)(
      optionHint("select role"),
      makeOptions(roleMappings.roles)
    ),
    div(id := serverId)
  )
}
