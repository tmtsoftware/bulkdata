package tmt.common

import scala.concurrent.duration._

object Config {
//  val delay = 1.second
  val delay = 1.milli

  val framesInputDir = "/usr/local/data/tmt/frames/input"
  val framesOutputDir = "/usr/local/data/tmt/frames/output"

  val moviesInputDir = "/usr/local/data/tmt/movies/input"
  val moviesOutputDir = "/usr/local/data/tmt/movies/output"

  val interface = "localhost"
  val port = 6001

  val imageWidth = 1920/2
  val imageHeight = 1080/2
}
