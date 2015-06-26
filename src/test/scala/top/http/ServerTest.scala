package top.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import top.common.Image

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class ServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  implicit val system = ActorSystem("Test")
  implicit val mat = ActorMaterializer()

  val host = "localhost"
  val port = 7001
  val server  = new Server(host, port)

  def await[T](f: Future[T]) = Await.result(f, 20.seconds)

  test("get") {
    val binding = await(server.runnableGraph.run())
    val response = await(Http().singleRequest(HttpRequest(uri = s"http://$host:$port/images")))
    val images = response.entity.dataBytes.map(Image.fromBytes).log("RECEIVED*********")

    images.runWith(TestSink.probe())
      .request(10)
      .expectNextN((1 to 10).map(x => Image(x.toString)))
      .expectComplete()

    binding.unbind()
  }

  override protected def afterAll() = {
    system.shutdown()
  }
}
