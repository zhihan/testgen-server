import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.earldouglas.xwp.JettyPlugin
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

object MyTestServerBuild extends Build {
  val Organization = "com.google"
  val Name = "Test Generation Server"
  val Version = "0.1.0"
  val ScalaVersion = "2.11.7"
  val ScalatraVersion = "2.4.0"

  lazy val project = Project (
    "my-test-server",
    file("."),
    settings = ScalatraPlugin.scalatraSettings ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.mockito" % "mockito-all" % "1.10.19" % "test",
        "org.scalatest" %% "scalatest" % "2.2.6" % "test",
        "ch.qos.logback" % "logback-classic" % "1.1.3" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "9.2.14.v20151106" % "container",
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
        "org.scalatra" %% "scalatra-json" % "2.4.0",
        "org.json4s"   %% "json4s-jackson" % "3.3.0"
      ))
  ).enablePlugins(JettyPlugin)
}
