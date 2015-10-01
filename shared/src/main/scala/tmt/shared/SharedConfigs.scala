package tmt.shared

import scala.concurrent.duration._

object SharedConfigs {
//  val delay = 1.second
  val delay = 1.milli

  val interface = "localhost"
  val port = 8000

  val imageWidth = 1920/2
  val imageHeight = 1080/2
}
