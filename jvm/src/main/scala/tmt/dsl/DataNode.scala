package tmt.dsl

import java.net.InetSocketAddress

import tmt.common.SharedConfigs

object DataNode extends Assembly("data-node") {
  val address = new InetSocketAddress(SharedConfigs.interface, SharedConfigs.port)
  mediaServer(address).run()
}
