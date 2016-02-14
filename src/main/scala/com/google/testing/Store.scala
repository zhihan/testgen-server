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

/** An internal representation of the test */
case class TestState(val ID: Int,
  val classname: String,
  val timelimit: Int,
  val state: String,
  val dir: String,
  val log: String,
  val result: Option[String]) {
  def toTest:Test = Test(
    ID, classname, timelimit, state, log, !result.isEmpty)
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

  def nextID: Int = if (testStates.isEmpty) 0 
    else testStates.values.maxBy{ _.ID }.ID + 1

  def addTest(t: NewTest) {
    logger.info("Try to add {}", t)
    val testState = TestState.fromNewTest(t)
    if (testState.ID < 0) {
      val newTest = testState.copy(ID=Store.nextID)
      testStates += (newTest.ID -> newTest)
      logger.info("{} added to store", newTest)       
    } else {
      testStates += (t.ID -> testState)
      logger.info("{} added to store", testState)
    }
    
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

  def getResult(id: Int): Option[String] = 
    for (ts <- testStates.get(id);
      r <- ts.result) yield r
  

}
