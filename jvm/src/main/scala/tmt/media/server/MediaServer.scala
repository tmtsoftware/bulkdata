package tmt.media.server

import java.net.InetSocketAddress

import tmt.common.SharedConfigs
import tmt.media.MediaAssembly

object MediaServer extends MediaAssembly("media-server") with App {
  val address = new InetSocketAddress(SharedConfigs.interface, SharedConfigs.port)
  mediaServer(address).run()
}
