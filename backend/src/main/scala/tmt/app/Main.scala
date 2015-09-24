package tmt.app

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  val Array(serverName, env) = args
  new Main(serverName, env)
}

class Main(serverName: String, env: String) {
  val assembly = new MediaAssembly(serverName, env)
  val server = assembly.serverFactory.make()
  val binding = Await.result(server.run(), 1.second)
  
  def stop() = {
    Await.result(binding.unbind(), 1.second)
    assembly.system.terminate()
  }
}
