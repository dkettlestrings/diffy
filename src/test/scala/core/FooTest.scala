package core

import org.scalatest.{FlatSpec, Matchers}

class FooTest extends FlatSpec with Matchers {

  "A test" should "pass" in {

    1 + 1 shouldBe 2
  }

}
