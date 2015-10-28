package tmt.common

import org.scalatest.{MustMatchers, FunSuite}
import tmt.transformations.A

class DD extends FunSuite with MustMatchers {

  test("dd") {
    1 mustEqual 1
    A.dd
  }

}
