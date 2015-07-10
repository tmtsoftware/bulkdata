package tmt.http

import java.net.InetSocketAddress

import tmt.common.{ActorConfigs, SharedConfigs, Server}
import tmt.library.RequestHandlerExtensions.RichRequestHandler

class HttpServer(address: InetSocketAddress)(implicit val actorConfigs: ActorConfigs) {
  import actorConfigs._

  val handler = new Handler()
  val server  = new Server(address, handler.requestHandler.toFlow)
}

object HttpServer extends HttpServer(new InetSocketAddress(SharedConfigs.interface, SharedConfigs.port))(ActorConfigs.from("http-server")) with App {
  server.run()
}
