package tmt.boxes.http

import java.net.InetSocketAddress

import tmt.common.SharedConfigs

object BoxServer extends BoxAssembly("http-server") with App {
  val address = new InetSocketAddress(SharedConfigs.interface, SharedConfigs.port)
  boxServer(address).run()
}
