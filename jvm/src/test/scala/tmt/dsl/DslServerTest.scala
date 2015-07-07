package tmt.dsl

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity.Chunked
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Sink
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.ActorConfigs

import scala.concurrent.duration.DurationInt

class DslServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {
  private  val interface = "localhost"
  private  val port      = 7001

  val actorConfigs = new ActorConfigs("TMT-CLient")
  import actorConfigs._

  val dslServer = new DslServer(interface, port)

  import tmt.common.Utils._

  val binding = await(dslServer.server.run())

  test("chunked bidi bytes") {
    await(bidi(s"http://$interface:$port/images/bytes"))
  }

  test("chunked bidi images") {
    await(bidi(s"http://$interface:$port/images/objects"))
  }

  test("bulk") {
    val listRequest = HttpRequest(uri = s"http://$interface:$port/movies/list")
    val listResponse = Http().singleRequest(listRequest)

    val listEntity = await(listResponse).entity.asInstanceOf[Chunked]
    val movieNames = listEntity.dataBytes.map(_.utf8String).drop(2).take(4)

    val result = movieNames.mapAsync(1) { name =>
      bidi(s"http://$interface:$port/movies/$name")
    }.runWith(Sink.ignore)

    await(result, 3.minute)
  }

  def bidi(uri: String) = {
    val getRequest = HttpRequest(uri = uri)
    val getResponse = Http().singleRequest(getRequest)

    getResponse.flatMap { resp =>
      val getEntity = resp.entity.asInstanceOf[MessageEntity]
      val postRequest = getRequest.copy(method = HttpMethods.POST, entity = getEntity)
      Http().singleRequest(postRequest) map { finalResponse =>
        finalResponse.status mustEqual StatusCodes.OK
        await(finalResponse.entity.toStrict(1.seconds)).data.utf8String mustEqual "copied"
      }
    }
  }

  override protected def afterAll() = {
    await(binding.unbind())
    dslServer.actorConfigs.system.shutdown()
    system.shutdown()
  }
}
