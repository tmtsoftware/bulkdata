package tmt.wavefront

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import tmt.common.{ActorConfigs, Types}
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

object RouteFactory {
  val ImageRoute = "images"
  val FilteredImagesRoute = "filtered-images"
  val CopiedImagesRoute = "copied-images"
  val ImageMetricsRoute = "image-metrics"
  val CumulativeMetricsRoute = "cumulative-metrics"
  val PerSecMetricsRoute = "per-sec-metrics"
}
