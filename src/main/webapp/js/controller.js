var testApp = angular.module("testApp", ["ngResource"]);

testApp.controller(
    "mainController", 
    ["$scope", "$resource", "$interval",
    function($scope, $resource, $interval) {
        var TestResource = $resource('/json/test/:ID', {ID: "@ID"}); 

        $scope.tests = [];
        
        $scope.newID = function() {
            return -1;
        };

        $scope.addTest = function() {
            if ($scope.classname && $scope.timelimit) {
                var classname = $scope.classname;
                var id = $scope.newID();
                var timelimit = $scope.timelimit

                var newTest = {"ID": id,
                    "classname": classname,
                    "timelimit": timelimit,
                    "state": "READY"};
                $scope.classname = "";
                $scope.timelimit = "";

                var newTestResource = new TestResource(newTest);
                newTestResource.$save();
            }
        };

        $scope.showLog = function(text) {
            var w = window.open();
            w.document.open();
            w.document.write("<html><body><pre>");
            w.document.write(text);
            w.document.write("</pre></body></html>");
            w.document.close();
        };

        $scope.downloadResult = function(ID) {
            window.location="/results/"+ID;
        };

        $scope.loadData = function() {
            TestResource.query(function(tests) {
                $scope.tests = tests;
            });
        };

        $scope.loadData();

        var stop = $interval($scope.loadData, 1000); // Update every second

        $scope.stopInteval = function() {
            if (angular.isDefined(stop)) {
               $interval.cancel(stop);
               stop = undefined;
           }
        };

        $scope.$on("$destroy", function() {
            $scope.stopInterval();
        });
    }]);
