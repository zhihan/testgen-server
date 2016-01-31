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


class FileUtil {
  val logger = LoggerFactory.getLogger(getClass)

  /** Create a zip file that contains all files in the glob pattern. */
  def createZipFile(dir: Path, glob: String, zipname:String): Option[Path] = {
    val srcFiles = Files.newDirectoryStream(dir, glob).toList
    if (!srcFiles.isEmpty) {
      logger.info("Creating zip file for results")

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
      logger.info("No output file found")
      None
    }
  }

  /** Copy one file from source to destination */
  def copyOneFile(src: String, dstDir:Path): String = {
    val srcFile = Paths.get(src)
    val dstFile = dstDir.resolve(srcFile.getFileName())
    Files.copy(srcFile, dstFile, StandardCopyOption.REPLACE_EXISTING)
    dstFile.toString() 
  }

  /** Ensure the directory exists */
  def ensureExists(resultsDir:Path) {
   if (!Files.exists(resultsDir)) {
      Files.createDirectory(resultsDir)
    }
  }

}



class Worker(fileUtil: FileUtil, constants: Constants) {

  val logger = LoggerFactory.getLogger(getClass)

  val RESULT_FILENAME = "test.zip"

  /** Execute the test generation step */
  def executeTest(t: TestState, dir:Path): TestState = {
    val cmd = Seq("java",
      "-classpath", constants.randoopJar,
      "randoop.main.Main", "gentests",
      "--testclass=" + t.classname,
      "--timelimit=" + t.timelimit,
      "--junit-output-dir=" + t.dir)
    val log = cmd.!!

    // Create zip files for tests
    val result = fileUtil.createZipFile(dir,
      "*.java", RESULT_FILENAME)
    val resultField = for (r <- result) yield r.toString

    t.copy(log=log, result=resultField)
  }


  /** Copy test results to the result directory */
  def copyResults(t: TestState): TestState = {
    val resultsRoot = Paths.get(constants.resultsDir)
    val resultsDir = resultsRoot.resolve(t.ID.toString)
    fileUtil.ensureExists(resultsDir)

    val result = for (tmpResult <- t.result) 
      yield fileUtil.copyOneFile(tmpResult, resultsDir)
    t.copy(result=result)
  }

  def work(t: TestState): Future[TestState] = Future {
    logger.info("Running command")

    // Prepare working directory
    val tmpDir = Paths.get("/tmp")
    val workingDir = Files.createTempDirectory(tmpDir,
      "working_" + t.ID)
    logger.info("Created working directory {}", workingDir.toString())
    val t1 = t.copy(dir=workingDir.toString())

    logger.info("Executing test")
    // Execute test
    val t2 = executeTest(t1, workingDir)

    // Collecting results
    logger.info("Copying test results")
    val t3 = copyResults(t2)

    logger.info("Test {} completed", t.ID)
    t3.copy(state="COMPLETED")
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
