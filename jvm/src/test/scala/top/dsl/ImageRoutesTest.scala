package top.dsl

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{FunSuite, MustMatchers}
import top.common.{ImageData, Image}

import scala.concurrent.duration._

class ImageRoutesTest extends FunSuite with MustMatchers with ScalatestRouteTest with CustomMarshallers {

  implicit val routeTestTimeout = RouteTestTimeout(20.seconds)
  val imageRoute = new ImageRoute(new ImageService)

  test("get") {

    Get("/images") ~> imageRoute.route ~> check {
      entityAs[Source[Image, Any]].runWith(TestSink.probe())
        .request(10)
        .expectNextN((1 to 10).map(x => Image(x.toString)))
        .expectComplete()
    }

  }

  test("post") {

    Post("/images", ImageData.ten) ~> imageRoute.route ~> check {
      status mustEqual StatusCodes.OK
    }

  }

  test("bidi") {

    Post("/images/bidi", ImageData.ten) ~> imageRoute.route ~> check {
      entityAs[Source[Image, Any]].runWith(TestSink.probe())
        .request(10)
        .expectNextN((1 to 10).map(x => Image(x.toString).updated))
        .expectComplete()
    }

  }
}
