package top.protocols

import akka.actor.ActorSystem

import scala.util.Try

object Main extends App {

  implicit val system = ActorSystem("TMT")

  args match {
    case Array("bidi", "server", host, Int(port)) => new Server(host, port, BidiProtocol).run()
    case Array("ss", "server", host, Int(port))   => new Server(host, port, new ServerSentProtocol(Image.ten)).run()
    case Array("sr", "server", host, Int(port))   => new Server(host, port, ServerReceivedProtocol).run()

    case Array("bidi", "client", host, Int(port)) => new Client(host, port, new ClientProtocol(Image.ten)).run()
    case Array("ss", "client", host, Int(port))   => new Client(host, port, new ClientProtocol(Image.single)).run()
    case Array("sr", "client", host, Int(port))   => new Client(host, port, new ClientProtocol(Image.ten)).run()

    case _ => println("Use `host port protocol mode` e.g. 'localhost, 6001, bidi, server'")
  }
}

object Int {
  def unapply(string: String) = Try(string.toInt).toOption
}
