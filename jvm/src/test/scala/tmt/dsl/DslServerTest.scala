package tmt.dsl

import java.io.File
import java.nio.file.Files

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Source
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.{Config, Producer}
import tmt.library.SourceExtensions.RichSource

class DslServerTest extends FunSuite with MustMatchers with BeforeAndAfterAll {
  val dslServer = new DslServer("localhost", 7001)

  import dslServer._
  import tmt.common.Utils._

  test("chunked") {
    val binding = await(server.runnableGraph.run())

    val response = await(Http().singleRequest(HttpRequest(uri = s"http://$interface:$port/images")))

    val byteArrays = response.entity.dataBytes.map(_.toByteBuffer.array())
    val files = Source(() => Producer.numbers()).map(makeFileName)

    val completion = byteArrays.zip(files).runForeach { case (data, file) =>
      println(s"writing $file")
      Files.write(file.toPath, data)
    }

    await(completion)
    binding.unbind()
  }

  def makeFileName(index: Int) = new File(f"${Config.outputDir}/out-image-$index%05d.jpg")

  override protected def afterAll() = {
    system.shutdown()
  }
}
