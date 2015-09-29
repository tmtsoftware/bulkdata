package templates

import javax.inject.Singleton

import models.HostMappings
import tmt.common.models.RoleMappings

@Singleton
class PageFactory {
  def showcase(roleMappings: RoleMappings, hostMappings: HostMappings) = new Page(
    "showcase",
    Seq(
      new SubscriptionView(roleMappings).frag,
      new ThrottleView(roleMappings).frag,
      new StreamView(roleMappings, hostMappings).frag
    )
  )
}
