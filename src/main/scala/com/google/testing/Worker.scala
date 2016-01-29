package com.google.testing

import org.slf4j.LoggerFactory
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global // default

import scala.sys.env
import scala.sys.process._
import scala.collection.JavaConversions._

import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files
import java.nio.file.FileSystems
import java.nio.file.StandardCopyOption

object Worker {

  val logger = LoggerFactory.getLogger(getClass)

  def createZipFile(dir: Path, glob: String,
    zipname:String): Option[Path] = {
    val srcFiles = Files.newDirectoryStream(dir, glob).toList
    if (!srcFiles.isEmpty) {
      val env:java.util.Map[String, String] = new java.util.HashMap();
      env.put("create", "true")
      val zipfile:Path = dir.resolve(zipname)
      val uri = URI.create("jar:file:" + zipfile.toString())
      val zipfs = FileSystems.newFileSystem(uri, env)
      for (srcFile <- srcFiles) {
        val pathInZip = zipfs.getPath("/", srcFile.getFileName().toString())
        Files.copy(srcFile, pathInZip, StandardCopyOption.REPLACE_EXISTING)
      }
      zipfs.close()
      Some(zipfile)
    } else {
      None
    }
  }

  def executeTest(t: TestState, dir:Path): TestState = {
    val cmd = Seq("java",
      "-classpath", Constants.randoopJar,
      "randoop.main.Main", "gentests",
      "--testclass=" + t.classname,
      "--timelimit=" + t.timelimit,
      "--junit-output-dir=" + t.dir)
    val log = cmd.!!

    // Create zip files for Error tests
    val errorTest = createZipFile(dir,
      "ErrorTest*.java", "error_test.zip")
    val err = errorTest flatMap { case x => Some(x.toString) }

    val regTest = createZipFile(dir,
      "RegressionTest*.java", "regression_test.zip")
    val reg = regTest flatMap { case x => Some(x.toString) }

    t.copy(log=log, result=Some(Result(err, reg)))
  }


  def work(t: TestState): Future[TestState] = Future {
    logger.info("Running command")

    // Prepare working directory
    val tmpDir = Paths.get("/tmp")
    val workingDir = Files.createTempDirectory(tmpDir,
      "working_" + t.ID)
    logger.info("Created working directory {}", workingDir.toString())
    val t1 = t.copy(dir=workingDir.toString())

    // Execute test
    val t2 = executeTest(t1, workingDir)

    // Collecting results
    t2.copy(state="COMPLETED")
  }
  

  def workOn(t: TestState) {
    logger.info("Work on {}", t)
    val newT: TestState = t.copy(state="WORKING")
    Store.updateTest(newT)

    val result = work(newT)
    result.onSuccess {
      case (t:TestState) => Store.updateTest(t) 
    }
  }


  def run {
    Store.nextReady match {
      case Some(t) => workOn(t)
      case None => ()
    }
  }

}
