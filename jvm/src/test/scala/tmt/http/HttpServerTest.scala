package tmt.http

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.{Box, BoxConversions, Boxes, Utils}

class HttpServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  private val httpServer = new HttpServer("localhost", 7001)

  import Utils._
  import httpServer._

  test("get") {
    val binding = await(server.runnableGraph.run())

    val response = await(Http().singleRequest(HttpRequest(uri = s"http://$interface:$port/images")))
    val images = response.entity.dataBytes.map(BoxConversions.fromByteString).log("Client-Received")

    images.runWith(TestSink.probe())
      .request(10)
      .expectNextN((1 to 10).map(x => Box(x.toString)))
      .expectComplete()

    binding.unbind()
  }

  test("post") {
    val binding = await(server.runnableGraph.run())
    val chunked = HttpEntity.Chunked.fromData(ContentTypes.NoContentType, Boxes.ten.map(BoxConversions.toByteString))

    val response = await(Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"http://$interface:$port/images", entity = chunked)))

    response.status mustEqual StatusCodes.OK

    binding.unbind()
  }

  test("bidi") {
    val binding = await(server.runnableGraph.run())
    val chunked = HttpEntity.Chunked.fromData(ContentTypes.NoContentType, Boxes.ten.map(BoxConversions.toByteString))

    val response = await(Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"http://$interface:$port/images/bidi", entity = chunked)))
    val images = response.entity.dataBytes.map(BoxConversions.fromByteString).log("Client-Received")

    images.runWith(TestSink.probe())
      .request(10)
      .expectNextN((1 to 10).map(x => Box(x.toString).updated))
      .expectComplete()

    binding.unbind()
  }

  override protected def afterAll() = {
    system.shutdown()
  }
}
