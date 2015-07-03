package tmt.tcp

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import tmt.common.Boxes

import scala.util.Try

object Main extends App {

  implicit val system = ActorSystem("TMT")
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  args match {
    case Array("get", "server", host, Int(port))  => new Server(host, port, new ServerProtocol.Get(Boxes.ten)).run()
    case Array("post", "server", host, Int(port)) => new Server(host, port, ServerProtocol.Post).run()
    case Array("bidi", "server", host, Int(port)) => new Server(host, port, ServerProtocol.Bidi).run()

    case Array("get", "client", host, Int(port))  => new Client(host, port, new ClientProtocol(Boxes.single)).run()
    case Array("post", "client", host, Int(port)) => new Client(host, port, new ClientProtocol(Boxes.ten)).run()
    case Array("bidi", "client", host, Int(port)) => new Client(host, port, new ClientProtocol(Boxes.ten)).run()

    case _ => println("Usage: `host port protocol mode` e.g.: 'localhost, 6001, bidi, server'")
  }
}

object Int {
  def unapply(string: String) = Try(string.toInt).toOption
}
