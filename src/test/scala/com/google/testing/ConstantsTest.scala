package com.google.testing

import org.scalatest.FunSuite

class ConstantsTest extends FunSuite {

  test("Configuration file should be correct") {
    assert(Constants.randoopJar == "/Users/zhihan/tools/randoop-2.1.1.jar")
  }

}
