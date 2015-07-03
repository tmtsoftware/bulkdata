package tmt.common

import scala.concurrent.duration._

object Config {
//  val delay = 1.second
  val delay = 1.milli
  val framesDir = "/usr/local/data/frames"
  val interface = "localhost"
  val port = 6001
  val imageWidth = 1920/2
  val imageHeight = 1080/2
}
