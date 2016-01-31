package com.google.testing

import java.nio.file.Paths
import org.scalatest.FunSuite
import org.mockito.Mockito._

object FakeConstants extends Constants {
  val randoopJar = "randoopJar"
  val resultsDir = "resultsDir"
}

class WorkerTest extends FunSuite {
  val mockFileUtil = mock(classOf[FileUtil])
  val worker = new Worker(mockFileUtil, FakeConstants)

  test("Copy file") {
    val ts = TestState(0, "", 1, "", "", "", Some("/tmpDir/test.zip"))
    worker.copyResults(ts)
    verify(mockFileUtil).ensureExists(Paths.get("resultsDir/0"))
    verify(mockFileUtil).copyOneFile("/tmpDir/test.zip",
      Paths.get("resultsDir/0"))
  }
}
