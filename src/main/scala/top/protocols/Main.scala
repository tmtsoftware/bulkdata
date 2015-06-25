package top.protocols

import akka.actor.ActorSystem

import scala.util.Try

object Main extends App {

  implicit val system = ActorSystem("TMT")

  args match {
    case Array("get", "server", host, Int(port))  => new Server(host, port, new ServerProtocol.Get(Image.ten)).run()
    case Array("post", "server", host, Int(port)) => new Server(host, port, ServerProtocol.Post).run()
    case Array("bidi", "server", host, Int(port)) => new Server(host, port, ServerProtocol.Bidi).run()

    case Array("get", "client", host, Int(port))  => new Client(host, port, new ClientProtocol(Image.single)).run()
    case Array("post", "client", host, Int(port)) => new Client(host, port, new ClientProtocol(Image.ten)).run()
    case Array("bidi", "client", host, Int(port)) => new Client(host, port, new ClientProtocol(Image.ten)).run()

    case _ => println("Usage: `host port protocol mode` e.g.: 'localhost, 6001, bidi, server'")
  }
}

object Int {
  def unapply(string: String) = Try(string.toInt).toOption
}
