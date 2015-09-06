package tmt.boxes.tcp

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{BeforeAndAfterAll, MustMatchers, FunSuite}
import tmt.boxes.http.Boxes
import tmt.boxes.tcp.ServerProtocol.Get
import tmt.common.models.Box

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class ServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  implicit val system = ActorSystem("TMT")
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val host = "localhost"
  val port = 7001

  def await[T](f: Future[T]) = Await.result(f, 20.seconds)

  test("get") {
    val server = new Server(host, port, new Get(Boxes.ten))
    val binding = await(server.runnableGraph.run())

    val client = new Client(host, port, new ClientProtocol(Boxes.single))

    client.imagesFromServer
      .runWith(TestSink.probe())
      .request(10)
      .expectNextN((1 to 10).map(x => Box(x.toString)))
      .expectComplete()

    binding.unbind()
  }

  test("post") {
    val server = new Server(host, port, ServerProtocol.Post)
    val binding = await(server.runnableGraph.run())

    val client = new Client(host, port, new ClientProtocol(Boxes.ten))

    client.imagesFromServer
      .runWith(TestSink.probe())
      .request(1)
      .expectComplete()

    binding.unbind()
  }

  test("bidi") {
    val server = new Server(host, port, ServerProtocol.Bidi)
    val binding = await(server.runnableGraph.run())

    val client = new Client(host, port, new ClientProtocol(Boxes.ten))

    client.imagesFromServer
      .runWith(TestSink.probe())
      .request(10)
      .expectNextN((1 to 10).map(x => Box(x.toString).updated))
      .expectComplete()

    binding.unbind()
  }

  override protected def afterAll() = {
    system.shutdown()
  }
}
