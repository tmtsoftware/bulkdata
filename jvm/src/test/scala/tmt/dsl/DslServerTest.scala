package tmt.dsl

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity.Chunked
import akka.http.scaladsl.model._
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}

class DslServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {
  val dslServer = new DslServer("localhost", 7001)

  import dslServer._
  import tmt.common.Utils._

  ignore("chunked bidi bytes") {
    bidi(s"http://$interface:$port/images/bytes")
  }

  ignore("chunked bidi images") {
    bidi(s"http://$interface:$port/images/objects")
  }

  def bidi(uri: String) = {
    val binding = await(server.runnableGraph.run())

    val getRequest = HttpRequest(uri = uri)
    val getResponse = Http().singleRequest(getRequest)
    val getEntity = await(getResponse).entity.asInstanceOf[Chunked]

    val postRequest = getRequest.copy(method = HttpMethods.POST, entity = getEntity)
    val postResponse = Http().singleRequest(postRequest)

    await(postResponse)

    binding.unbind()
  }

  override protected def afterAll() = {
    system.shutdown()
  }
}
