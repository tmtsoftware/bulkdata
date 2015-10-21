package tmt.app

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  args match {
    case Array(role, serverName, env, seedName) => new Main(role, serverName, env, Some(seedName))
    case Array(role, serverName, env)           => new Main(role, serverName, env, None)
  }
}

class Main(role: String, serverName: String, env: String, seedName: Option[String]) {
  val assembly = new Assembly(role, serverName, env, seedName)
  val server = assembly.serverFactory.make()
  val binding = Await.result(server.run(), 1.second)
  assembly.nodeInfoPublisher.publish(binding.localAddress.getPort)

  def stop() = {
    Await.result(binding.unbind(), 1.second)
    assembly.system.terminate()
  }
}
