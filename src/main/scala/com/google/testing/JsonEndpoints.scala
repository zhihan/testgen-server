package com.google.testing

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}
import org.slf4j.{Logger, LoggerFactory}

// JSON handling support from Scalatra
import org.scalatra.json._

import org.scalatra._


class JsonEndpoints extends ScalatraServlet with JacksonJsonSupport {
  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  val logger = LoggerFactory.getLogger(getClass)

  val worker = new Worker(new FileUtil(), JsonConstants)

  before() {
    contentType = formats("json")
  }

  get("/test") {
    worker.run

    Store.getTests
  }

  get("/test/:id") {
    logger.info("Gettting a test for id {}", params("id"))
    Test(0, "", 0, "", "", false)
  }

  post("/test/:id") {
    logger.info("Adding a test for id {}: {}", params("id"))
    Store.addTest(parsedBody.extract[NewTest])
    worker.run
  }

  delete("/test/:id") {
    logger.info("Deleting a test for id {}", params("id"))
  }

}
