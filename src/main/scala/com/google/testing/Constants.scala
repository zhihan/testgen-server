package com.google.testing

import java.io.File

import org.json4s._
import org.json4s.jackson.JsonMethods._

trait Constants {
  def randoopJar: String
  def resultsDir: String
}

object JsonConstants extends Constants {
  implicit lazy val formats = DefaultFormats

  lazy val configFile: String = "/Users/zhihan/tools/randoop-server.json"

  lazy val config = parse(new File(configFile))

  override lazy val randoopJar: String = {
    val tools_dir = (config \ "tools_dir").extract[String]
    val filename = (config \ "randoop_jar").extract[String]
    tools_dir + "/" + filename
  }

  lazy val resultsDir: String =
    (config \ "results_dir").extract[String]

}
