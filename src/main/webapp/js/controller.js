var testApp = angular.module("testApp", ["ngResource"]);

testApp.controller(
    "mainController", 
    ["$scope", "$resource", "$interval",  
     function($scope, $resource, $interval) {
	 var TestResource = $resource('/json/test/:ID', 
				      {ID: "@ID"}); 

	 $scope.tests = [];
	 $scope.nextID = 0;
	 
	 $scope.newID = function() {
             var id = $scope.nextID;
             $scope.nextID += 1;
             return id;
	 }
	 
	 $scope.addTest = function() {
             if ($scope.classname && $scope.timelimit) {
	         var classname = $scope.classname;
	         var id = $scope.newID();
                 var timelimit = $scope.timelimit
                 
	         var newTest = {"ID": id,
			        "classname": classname,
                                "timelimit": timelimit,
			        "state": "READY",
                               };
                 $scope.tests.push(newTest);
                 $scope.classname = "";
                 $scope.timelimit = "";
                 
                 var newTestResource = new TestResource(newTest);
                 newTestResource.$save();
             }
	 }
         
         
	 $scope.loadData = function() {
	     TestResource.query(function(tests) {
		 $scope.tests = tests;
                 
                 function getMaxOfArray(numArray) {
                     return Math.max.apply(null, numArray);
                 }

                 if (tests.length >= 1) {
                     $scope.nextID = getMaxOfArray(tests.map(function(test) {
                         return test.ID;
                     })) + 1;
                 } else {
                     $scope.nextID = 1;
                 }
	     })
	 }
	 
	 $scope.loadData();

         var stop = $interval($scope.loadData, 1000); // Update every second

         $scope.stopInteval = function() {
             if (angular.isDefined(stop)) {
                 $interval.cancel(stop);
                 stop = undefined;
             }
         }

         $scope.$on("$destroy", function() {
             $scope.stopInterval();
         });
         
}]);
