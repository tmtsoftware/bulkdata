package tmt.common.api

import tmt.common.models.RoleMappings

import scala.concurrent.Future

trait Api {
  def getRoleMappings(): Future[RoleMappings]
}
