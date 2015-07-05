package tmt.dsl

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.{FunSuite, MustMatchers}
import tmt.common.{Boxes, Box}

import scala.concurrent.duration._

class BoxRouteTest extends FunSuite with MustMatchers with ScalatestRouteTest with CustomMarshallers {

  implicit val routeTestTimeout = RouteTestTimeout(20.seconds)
  val boxRoute = new BoxRoute(new BoxService)

  test("get") {

    Get("/boxes") ~> boxRoute.route ~> check {
      entityAs[Source[Box, Any]].runWith(TestSink.probe())
        .request(10)
        .expectNextN((1 to 10).map(x => Box(x.toString)))
        .expectComplete()
    }

  }

  test("post") {

    Post("/boxes", Boxes.ten) ~> boxRoute.route ~> check {
      status mustEqual StatusCodes.OK
    }

  }

  test("bidi") {

    Post("/boxes/bidi", Boxes.ten) ~> boxRoute.route ~> check {
      entityAs[Source[Box, Any]].runWith(TestSink.probe())
        .request(10)
        .expectNextN((1 to 10).map(x => Box(x.toString).updated))
        .expectComplete()
    }

  }
}
