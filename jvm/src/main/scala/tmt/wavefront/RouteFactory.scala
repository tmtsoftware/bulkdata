package tmt.wavefront

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.scaladsl.Source
import tmt.common.{Types, ActorConfigs}
import tmt.library.SourceExtensions.RichSource

class RouteFactory(actorConfigs: ActorConfigs) {
  import actorConfigs._

  def make[T: Types.Stream](routeName: String, dataSource: Source[T, Any]): Route = {
    val source = dataSource.hot.multicast
    path(routeName) {
      get(complete(source))
    }
  }
}
