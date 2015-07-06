package tmt.dsl

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity.{Default, Chunked}
import akka.http.scaladsl.model._
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}

import scala.concurrent.duration.DurationInt

class DslServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {
  val dslServer = new DslServer("localhost", 7001)

  import dslServer._
  import tmt.common.Utils._

  import system.dispatcher

  ignore("chunked bidi bytes") {
    val binding = await(server.runnableGraph.run())
    await(bidi(s"http://$interface:$port/images/bytes"))
    binding.unbind()
  }

  test("chunked bidi images") {
    val binding = await(server.runnableGraph.run())
    await(bidi(s"http://$interface:$port/images/objects"))
    binding.unbind()
  }

  test("bulk") {
    val binding = await(server.runnableGraph.run())

    val listRequest = HttpRequest(uri = s"http://$interface:$port/movies/list")
    val listResponse = Http().singleRequest(listRequest)

    val listEntity = await(listResponse).entity.asInstanceOf[Chunked]
    val movieNames = listEntity.dataBytes.map(_.utf8String).drop(2).take(3)

    val result = movieNames.mapAsync(1) { name =>
      println("processing movie--------------------->", name)
      bidi(s"http://$interface:$port/movies/$name")
    }.runForeach(x => println(x.status))

    await(result, 2.minute)
    binding.unbind()
  }

  def bidi(uri: String) = {
    val getRequest = HttpRequest(uri = uri)
    val getResponse = Http().singleRequest(getRequest)

    getResponse.flatMap { resp =>
      val getEntity = resp.entity.asInstanceOf[Chunked]
      val postRequest = getRequest.copy(method = HttpMethods.POST, entity = getEntity)
      Http().singleRequest(postRequest)
    }
  }

  override protected def afterAll() = {
    system.shutdown()
  }
}
