package tmt.wavefront

import tmt.media.MediaAssembly

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {
  val role = args.head
  new RunningServer(role)
}

class RunningServer(role: String) {
  val assembly = new MediaAssembly(role)
  val server = assembly.serverFactory.make()
  val binding = Await.result(server.run(), 1.second)
  
  def stop() = {
    Await.result(binding.unbind(), 1.second)
    assembly.system.shutdown()
  }
}
