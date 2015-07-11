package tmt.http

import java.net.InetSocketAddress

import tmt.common.SharedConfigs
import tmt.dsl.Assembly

object DataNode extends Assembly("http-server") {
  val address = new InetSocketAddress(SharedConfigs.interface, SharedConfigs.port)
  httpServer(address).run()
}
