package tmt.dsl

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{FunSuite, MustMatchers}
import tmt.common.{Boxes, Box}

import scala.concurrent.duration._

class AppRoutesTest extends FunSuite with MustMatchers with ScalatestRouteTest with CustomMarshallers {

  implicit val routeTestTimeout = RouteTestTimeout(20.seconds)
  val appRoute = new AppRoute(new BoxService, new ImageService)

  test("get") {

    Get("/images") ~> appRoute.route ~> check {
      entityAs[Source[Box, Any]].runWith(TestSink.probe())
        .request(10)
        .expectNextN((1 to 10).map(x => Box(x.toString)))
        .expectComplete()
    }

  }

  test("post") {

    Post("/images", Boxes.ten) ~> appRoute.route ~> check {
      status mustEqual StatusCodes.OK
    }

  }

  test("bidi") {

    Post("/images/bidi", Boxes.ten) ~> appRoute.route ~> check {
      entityAs[Source[Box, Any]].runWith(TestSink.probe())
        .request(10)
        .expectNextN((1 to 10).map(x => Box(x.toString).updated))
        .expectComplete()
    }

  }
}
