package templates

import javax.inject.Singleton

import tmt.shared.models.RoleMappings

@Singleton
class PageFactory {
  def showcase(roleMappings: RoleMappings) = new Page(
    "showcase",
    Seq(
      new ThrottleView(roleMappings).frag
    )
  )
}
