package com.google.testing

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class StoreTest extends FunSuite with BeforeAndAfter {
  val testClassName = "java.util.DummyTestClass"
  val testTimeLimit = 10
  val testState = "READY"
  val ts =
    TestState.fromNewTest(
      NewTest(10, testClassName, testTimeLimit, testState))

  before {
    Store.addTest(
      NewTest(10, testClassName, testTimeLimit, testState))
  }

  test("Get tests") {
    val l = Store.getTests
    assert(l == List(ts.toTest))
  }

  test("Next ready") {
    val x = Store.nextReady
    assert(x == Some(ts))
  }
}
