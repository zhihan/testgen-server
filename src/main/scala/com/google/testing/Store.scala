package com.google.testing

import org.slf4j.LoggerFactory

import scala.collection.mutable.Map 

/** A newly created test */
case class NewTest(val ID: Int,
  val classname: String,
  val timelimit: Int,
  val state: String)

/** A Test class that is exposed to the REST endpoints. */
case class Test(val ID: Int,
  val classname: String,
  val timelimit: Int,
  val state: String,
  val log: String,
  val hasResult: Boolean)


case class Result(val errorTest: Option[String], // Path to the zip files
  val regressionTest: Option[String]) 

/** An internal representation of the test */
case class TestState(val ID: Int,
  val classname: String,
  val timelimit: Int,
  val state: String,
  val dir: String,
  val log: String,
  val result: Option[Result]) {
  def toTest:Test = Test(
    ID, classname, timelimit, state, log, result.isEmpty)
}


object TestState {
  def fromNewTest(t: NewTest): TestState = new TestState(t.ID,
    t.classname, t.timelimit, t.state, "", "", None)
}


/** In-memory store of all the tests. */
object Store {

  val logger = LoggerFactory.getLogger(getClass)

  val testStates = Map[Int, TestState]()

  def getTests: List[Test] = testStates.values.toList.map{ _.toTest }

  def addTest(t: NewTest) {
    logger.info("Try to add {}", t)
    testStates += (t.ID -> TestState.fromNewTest(t))
    logger.info("{} added to store", t)
  }

  def updateTest(ts: TestState) {
    testStates(ts.ID) = ts
    logger.info("{} updated", ts)
  }

  def nextReady: Option[TestState] =
    testStates.find{
      case(id, y) => y.state == "READY"
    }.flatMap{ case (id, y) => Some(y) }

  def clear { testStates.clear }

  def getErrorTest(id: Int): Option[String] = 
    for (ts <- testStates.get(id);
      r <- ts.result;
      e <- r.errorTest) yield e

  def getRegressionTest(id: Int): Option[String] = 
    for (ts <- testStates.get(id);
      r <- ts.result;
      reg <- r.regressionTest) yield reg
  

}
