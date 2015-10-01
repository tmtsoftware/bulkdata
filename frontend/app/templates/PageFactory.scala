package templates

import javax.inject.Singleton

import models.HostMappings
import tmt.shared.models.{ConnectionDataSet, RoleMappings}

@Singleton
class PageFactory {
  def showcase(roleMappings: RoleMappings, hostMappings: HostMappings, connectionDataSet: ConnectionDataSet) = new Page(
    "showcase",
    Seq(
      new SubscriptionView(roleMappings, connectionDataSet).frag,
      new ThrottleView(roleMappings).frag,
      new StreamView(roleMappings, hostMappings).frag
    )
  )
}
