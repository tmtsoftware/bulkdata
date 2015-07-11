package tmt.http

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common._
import tmt.dsl.Assembly
import tmt.library.InetSocketAddressExtensions.RichInetSocketAddress
import Utils._

class HttpServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  val testAssembly = new Assembly("Test")
  import testAssembly.actorConfigs._

  val httpServerAssembly = new Assembly("http-server")
  val address    = new InetSocketAddress("localhost", 7001)
  val httpServer = httpServerAssembly.httpServer(address)

  val binding = await(httpServer.run())

  test("get") {
    val response = await(Http().singleRequest(HttpRequest(uri = address.absoluteUri("/boxes"))))
    val images = response.entity.dataBytes.map(BoxConversions.fromByteString).log("Client-Received")

    images.runWith(TestSink.probe())
      .request(10)
      .expectNextN((1 to 10).map(x => Box(x.toString)))
      .expectComplete()
  }

  test("post") {
    val chunked = HttpEntity.Chunked.fromData(ContentTypes.NoContentType, Boxes.ten.map(BoxConversions.toByteString))

    val response = await(Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = address.absoluteUri("/boxes"), entity = chunked)))

    response.status mustEqual StatusCodes.OK
  }

  test("bidi") {
    val chunked = HttpEntity.Chunked.fromData(ContentTypes.NoContentType, Boxes.ten.map(BoxConversions.toByteString))

    val response = await(Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = address.absoluteUri("/boxes/bidi"), entity = chunked)))
    val images = response.entity.dataBytes.map(BoxConversions.fromByteString).log("Client-Received")

    images.runWith(TestSink.probe())
      .request(10)
      .expectNextN((1 to 10).map(x => Box(x.toString).updated))
      .expectComplete()

  }

  override protected def afterAll() = {
    await(binding.unbind())
    testAssembly.system.shutdown()
    httpServerAssembly.system.shutdown()
  }
}
