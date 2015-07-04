package tmt.dsl

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity.Chunked
import akka.http.scaladsl.model._
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}

class DslServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {
  val dslServer = new DslServer("localhost", 7001)

  import dslServer._
  import tmt.common.Utils._

  test("get") {
    val binding = await(server.runnableGraph.run())
    val response = await(Http().singleRequest(HttpRequest(uri = s"http://$interface:$port/images")))
    val chunks = response.entity.asInstanceOf[Chunked].chunks
    val total = chunks.runFold(0)((acc, _) => acc + 1)
    println(await(total))
    binding.unbind()
  }

  override protected def afterAll() = {
    system.shutdown()
  }
}
