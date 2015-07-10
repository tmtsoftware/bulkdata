package tmt.http

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common._

class HttpServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  val actorConfigs = new ActorConfigs("TMT-CLient")
  import actorConfigs._

  private val address    = new InetSocketAddress("localhost", 7001)
  private val httpServer = new HttpServer(address)

  import Utils._
  val binding = await(httpServer.server.run())

  test("get") {
    val response = await(Http().singleRequest(HttpRequest(uri = s"http://${address.getHostName}:${address.getPort}/boxes")))
    val images = response.entity.dataBytes.map(BoxConversions.fromByteString).log("Client-Received")

    images.runWith(TestSink.probe())
      .request(10)
      .expectNextN((1 to 10).map(x => Box(x.toString)))
      .expectComplete()
  }

  test("post") {
    val chunked = HttpEntity.Chunked.fromData(ContentTypes.NoContentType, Boxes.ten.map(BoxConversions.toByteString))

    val response = await(Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"http://${address.getHostName}:${address.getPort}/boxes", entity = chunked)))

    response.status mustEqual StatusCodes.OK
  }

  test("bidi") {
    val chunked = HttpEntity.Chunked.fromData(ContentTypes.NoContentType, Boxes.ten.map(BoxConversions.toByteString))

    val response = await(Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"http://${address.getHostName}:${address.getPort}/boxes/bidi", entity = chunked)))
    val images = response.entity.dataBytes.map(BoxConversions.fromByteString).log("Client-Received")

    images.runWith(TestSink.probe())
      .request(10)
      .expectNextN((1 to 10).map(x => Box(x.toString).updated))
      .expectComplete()

  }

  override protected def afterAll() = {
    await(binding.unbind())
    httpServer.actorConfigs.system.shutdown()
    system.shutdown()
  }
}
