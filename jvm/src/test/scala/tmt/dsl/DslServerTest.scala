package tmt.dsl

import java.io.File

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity.Chunked
import akka.http.scaladsl.model._
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.Config

class DslServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {
  val dslServer = new DslServer("localhost", 7001)

  import dslServer._
  import tmt.common.Utils._

  test("chunked") {
    val binding = await(server.runnableGraph.run())

    val getRequest = HttpRequest(uri = s"http://$interface:$port/images")
    val getResponse = Http().singleRequest(getRequest)
    val getEntity = await(getResponse).entity.asInstanceOf[Chunked]

    val postRequest = getRequest.copy(method = HttpMethods.POST, entity = getEntity)
    val postResponse = Http().singleRequest(postRequest)

    await(postResponse)

    binding.unbind()
  }

  def makeFileName(index: Int) = new File(f"${Config.outputDir}/out-image-$index%05d.jpg")

  override protected def afterAll() = {
    system.shutdown()
  }
}
