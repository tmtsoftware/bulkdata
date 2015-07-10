package tmt.common

import scala.concurrent.duration._

object SharedConfigs {
//  val delay = 1.second
  val delay = 1.milli

  val interface = "localhost"
  val port = 6001

  val imageWidth = 1920/2
  val imageHeight = 1080/2
}
