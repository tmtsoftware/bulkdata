package top.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import top.common.{BoxConversions, Boxes, Box}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class ServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  implicit val system = ActorSystem("Test")
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val host = "localhost"
  val port = 7001
  val server  = new Server(host, port, new Handler().requestHandler)
//  val server  = new top.dsl.Server(host, port, new ImageRoute(new ImageService).route)

  def await[T](f: Future[T]) = Await.result(f, 20.seconds)

  test("get") {
    val binding = await(server.runnableGraph.run())

    val response = await(Http().singleRequest(HttpRequest(uri = s"http://$host:$port/images")))
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

    val response = await(Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"http://$host:$port/images", entity = chunked)))

    response.status mustEqual StatusCodes.OK

    binding.unbind()
  }

  test("bidi") {
    val binding = await(server.runnableGraph.run())
    val chunked = HttpEntity.Chunked.fromData(ContentTypes.NoContentType, Boxes.ten.map(BoxConversions.toByteString))

    val response = await(Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"http://$host:$port/images/bidi", entity = chunked)))
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
