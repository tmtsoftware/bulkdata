package tmt.dsl

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{FlattenStrategy, Sink, Source}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.ActorConfigs
import tmt.common.Utils._

import scala.concurrent.duration.DurationInt

class OneToManyTransferTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  val testConfigs = ActorConfigs.from("Test")
  import testConfigs._

  val source = new InetSocketAddress("localhost", 7001)
  val destination1 = new InetSocketAddress("localhost", 8001)
  val destination2 = new InetSocketAddress("localhost", 8002)

  val sourceServer = new DataNode(source)(ActorConfigs.from("source"))
  val destination1Server = new DataNode(destination1)(ActorConfigs.from("destination1"))
  val conf = ConfigFactory
    .parseString(s"data-location.movies.output=/usr/local/data/tmt/movies/output2")
    .withFallback(ConfigFactory.load())

  val destination2Server = new DataNode(destination2)(new ActorConfigs()(ActorSystem("destination2", conf)))
  val oneToManyTransfer = new OneToManyTransfer(source, Seq(destination1, destination2))(testConfigs)

  val sourceBinding = await(sourceServer.server.run())
  val destination1Binding = await(destination1Server.server.run())
  val destination2Binding = await(destination2Server.server.run())

  test("blob-pipelined") {
    val result = movieNames
      .map(name => s"/movies/$name")
      .via(oneToManyTransfer.flow)
      .map(verifyTransfer)
      .runWith(Sink.ignore)

    await(result, 3.minute)
  }

  def verifyTransfer(response: HttpResponse) = {
    response.status mustEqual StatusCodes.OK
    response.entity.asInstanceOf[HttpEntity.Strict].data.utf8String mustEqual "copied"
  }

  def movieNames = {
    val listRequest = HttpRequest(uri = s"http://${source.getHostString}:${source.getPort}/movies/list")

    Source(Http().singleRequest(listRequest))
      .map(_.entity.dataBytes.map(_.utf8String))
      .flatten(FlattenStrategy.concat)
      .take(4)
  }

  override protected def afterAll() = {
    await(sourceBinding.unbind())
    await(destination1Binding.unbind())
    await(destination2Binding.unbind())
    sourceServer.actorConfigs.system.shutdown()
    destination1Server.actorConfigs.system.shutdown()
    destination2Server.actorConfigs.system.shutdown()
    system.shutdown()
  }
}
