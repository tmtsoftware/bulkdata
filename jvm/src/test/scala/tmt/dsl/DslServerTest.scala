package tmt.dsl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity.Chunked
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.{Box, BoxConversions, Boxes}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class DslServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  implicit val system = ActorSystem("Test")
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val host = "localhost"
  val port = 7001
  val server  = new tmt.dsl.Server(host, port, new AppRoute(new BoxService, new ImageService).route)

  def await[T](f: Future[T]) = Await.result(f, 20.seconds)

  test("get") {
    val binding = await(server.runnableGraph.run())
    val response = await(Http().singleRequest(HttpRequest(uri = s"http://$host:$port/images")))
    val chunks = response.entity.asInstanceOf[Chunked].chunks
    val total = chunks.runFold(0)((acc, _) => acc + 1)
    println(await(total))
    binding.unbind()
  }

  override protected def afterAll() = {
    system.shutdown()
  }
}
