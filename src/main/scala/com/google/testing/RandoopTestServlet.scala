package com.google.testing

import org.slf4j.{Logger, LoggerFactory}

import org.scalatra.ScalatraServlet

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
        <h1>Randoop Test Generation Service</h1>
        <p>Try Randoop on a built-in java class.</p>
        </div>
        <div ng-controller="mainController">
        <form class="form-inline">
        <div class="form-group"> 
          <label for="url">Full path of the test class</label>
          <input type="text" id="url" class="form-control" ng-model="url" placeholder="java.util.Collections"></input>
        </div>
        <div class="form-group">
          <label for="timelimit">Time limit</label>
          <div class="input-group">
          <input type="number" id="timelimit" placeholder="time limit"></input>
          <div class="input-group-addon">seconds</div>
          </div>
          <button class="btn btn-primary" type="button" ng-click="addTest()">Go!</button>
        </div>
        </form>
        <div ng-model="tests" class="panel">
          <ul class="list-group">
            <li class="list-group-item" ng-repeat="test in tests | orderBy:'+ID' track by test.ID">
            <span class="id-width">{{ test.ID }}</span> <span class="url-width">{{ test.url }}</span> 
            <span ng-switch="test.state">
              <span class="text-primary" ng-switch-when="WORKING"><strong>{{ test.state }}</strong></span>
              <span class="text-success" ng-switch-when="COMPLETED"><strong>{{ test.state }}</strong></span>
              <span ng-switch-default><strong>{{ test.state }}</strong></span>
            </span>
           </li>
          </ul>
        </div>

        </div>
        </div>

      </body>
    </html>"""
  }

}
