package tmt.media

import java.net.InetSocketAddress

import tmt.common.SharedConfigs

object MediaServer extends MediaAssembly("media-server") {
  val address = new InetSocketAddress(SharedConfigs.interface, SharedConfigs.port)
  mediaServer(address).run()
}
