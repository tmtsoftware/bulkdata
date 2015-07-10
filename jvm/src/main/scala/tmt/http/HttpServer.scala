package tmt.http

import tmt.common.{ActorConfigs, Config, Server}
import tmt.library.RequestHandlerExtensions.RichRequestHandler

class HttpServer(interface: String, port: Int) {
  val actorConfigs = new ActorConfigs("TMT")
  import actorConfigs._

  val handler = new Handler()
  val server  = new Server(interface, port, handler.requestHandler.toFlow)
}

object HttpServer extends HttpServer(Config.interface, Config.port) with App {
  server.run()
}
