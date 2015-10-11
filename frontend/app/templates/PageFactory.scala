package templates

import javax.inject.Singleton

import models.HostMappings
import tmt.shared.models.{ConnectionSet, RoleMappings}

@Singleton
class PageFactory {
  def showcase(roleMappings: RoleMappings, hostMappings: HostMappings, connectionDataSet: ConnectionSet) = new Page(
    "showcase",
    Seq(
      new ThrottleView(roleMappings).frag,
      new StreamView(roleMappings, hostMappings).frag
    )
  )
}
