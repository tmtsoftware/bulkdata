package tmt.http

import java.net.InetSocketAddress

import tmt.common.{ActorConfigs, Config, Server}
import tmt.library.RequestHandlerExtensions.RichRequestHandler

class HttpServer(address: InetSocketAddress) {
  val actorConfigs = new ActorConfigs("TMT")
  import actorConfigs._

  val handler = new Handler()
  val server  = new Server(address, handler.requestHandler.toFlow)
}

object HttpServer extends HttpServer(new InetSocketAddress(Config.interface, Config.port)) with App {
  server.run()
}
