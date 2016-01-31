package com.google.testing

import java.io.File

import org.slf4j.{Logger, LoggerFactory}
import org.scalatra.ScalatraServlet
import org.scalatra.NotFound

class RandoopTestServlet extends ScalatraServlet {
  val logger = LoggerFactory.getLogger(getClass)

  get("/") {
    contentType = "text/html"

    """<html ng-app="testApp">
      <head>
      <title>Randoop Test Service</title>
      <meta name="viewport" content="width=device-width, initial-scale=1">
      
      <link rel="stylesheet" href="css/bootstrap.css"></link>
      <link rel="stylesheet" href="css/testservice.css"></link>

      <script src="js/angular.min.js"> </script>
      <script src="js/angular-resource.min.js"></script>
      <script src="js/controller.js"></script>
      </head>
      <body>
        <div class="container-fluid">
        <div class="jumbotron bg-primary">
        <h1>Java Test Generation Service</h1>
        <p>Use Randoop to generate test code for a built-in java class.</p>
        </div>
        <div ng-controller="mainController">
        <form class="form-inline">
        <div class="form-group"> 
          <label for="classname">Full path of the test class</label>
          <input type="text" id="classname" class="form-control" ng-model="classname" placeholder="java.util.Collections"></input>
        </div>
        <div class="form-group">
          <label for="timelimit">Time limit</label>
          <div class="input-group">
          <input type="number" id="timelimit" placeholder="time limit" ng-model="timelimit"></input>
          <div class="input-group-addon">seconds</div>
          </div>
          <button class="btn btn-primary" type="button" ng-click="addTest()">Start</button>
        </div>
        </form>
        <div ng-model="tests" class="panel">
          <ul class="list-group">
            <li class="list-group-item" ng-repeat="test in tests | orderBy:'+ID' track by test.ID">
            <span class="id-width">{{ test.ID }}</span> <span>{{ test.classname }}</span> 
            <span ng-switch="test.state">
              <span class="text-primary" ng-switch-when="WORKING"><strong>{{ test.state }}</strong></span>
              <span class="text-success" ng-switch-when="COMPLETED"><strong>{{ test.state }}</strong></span>
              <span ng-switch-default><strong>{{ test.state }}</strong></span>
            </span>
            <button type="button" class="btn btn-default btn-sm" 
            ng-if="test.log" ng-click="showLog(test.log)">LOG</button>
           </li>
          </ul>
        </div>

        </div>
        </div>

      </body>
    </html>"""
  }

  get("/results/:id/error-test") {
    contentType = "application/octet-stream"
    val theFile = new File("/Users/zhihan/test.txt")
    response.setHeader("Content-Disposition",
      "attachment; filename=" + theFile.getName())
    theFile
  }

  get("/results/:id/regression-test") {
    val regressionTestFile = Store.getRegressionTest(params("id").toInt)
    if (!regressionTestFile.isEmpty) {
      contentType = "application/octet-stream"
      val theFile = new File(regressionTestFile.get)
      response.setHeader("Content-Disposition",
        "attachment; filename=" + theFile.getName())
      theFile
    } else {
      contentType = null
      NotFound("Result file not found.")
    }
  }

  get("/results/:id/error-test") {
    val errorTestFile = Store.getErrorTest(params("id").toInt)
    if (!errorTestFile.isEmpty) {
      contentType = "application/octet-stream"
      val theFile = new File(errorTestFile.get)
      response.setHeader("Content-Disposition",
        "attachment; filename=" + theFile.getName())
      theFile
    } else {
      contentType = null
      NotFound("Result file not found.")
    }
  }
  

}
