angular.module("tweetFilterApp", ["tweetFilterApp.controllers", "tweetFilterApp.filters", "angular-ladda", "ui.bootstrap"]);

angular.module("tweetFilterApp")
    .config(function (laddaProvider) {
        laddaProvider.setOption({
            style: 'expand-right'
        });
    });

angular.module("tweetFilterApp.controllers", []).controller("tweetFilterCtrl", function ($scope, $modal, $timeout, $log) {
    $scope.filterText = "poker";
    $scope.currentFilter;
    $scope.ws;
    $scope.tweets = [];
    $scope.hashTagStats = [];
    $scope.filterStats = [];
    $scope.selectedTweet;

    $scope.filter = function () {
        $scope.ws.send(JSON.stringify({filter: $scope.filterText}));
        $scope.tweets = [];
        $scope.hashTagStats = [];
        $scope.filterStats = [];
        $scope.currentFilter = $scope.filterText;
        $scope.filterText = "";
    };

    $scope.initSockets = function () {
        $scope.ws = new WebSocket($("#tweetFilterApp").data("ws-url"));
        $scope.ws.onmessage = function (event) {
            var message = JSON.parse(event.data);
            switch (message.type) {
                case "tweet":
                    $scope.$apply(function () {
                        $scope.tweets.unshift(message);
                    });
                    break;
                case "stats":
                    $scope.$apply(function () {
                        var stats = message.stats;
                        if (stats.wordOccurrencesType == "hashTag") {
                            $scope.hashTagStats = stats.wordOccurrences;
                        } else if (stats.wordOccurrencesType == "filter") {
                            $scope.filterStats = stats.wordOccurrences;
                        } else {
                            console.log(message)
                        }
                    });
                    break;
                default:
                    console.log(message)
            }
        };
        $scope.ws.onopen = function () {
            $scope.$apply(function () {
                $scope.filter();
            });
        };
    };

    $scope.warningLevel = function () {
        return $scope.hideTweets() ? "alert-warning" : "alert-success";
    }

    $scope.hideTweets = function () {
        return $scope.tweets.length == 0;
    };

    $scope.tweetsLength = function () {
        return $scope.tweets.length;
    };

    $scope.hideStats = function () {
        return $scope.hashTagStats.length == 0 && $scope.filterStats.length == 0;
    };

    $scope.setSelected = function(item) {
        $scope.selectedTweet = item;
    };

    $scope.initSockets();

    $scope.open = function (userId) {

        var modalInstance = $modal.open({
            animation: true,
            templateUrl: 'profile.html',
            controller: 'ModalInstanceCtrl',
            resolve: {
                tweet: function () {
                    return userId + " loading...";
                }
            }
        });

        modalInstance.opened.then(function() {
            $scope.loadData(modalInstance, userId);
        }, function() {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

    $scope.loadData = function(aModalInstance, userId) {
        $timeout(function() {
            aModalInstance.setTweet(userId + " loaded!!");
        }, 3000);
    };
});

angular.module("tweetFilterApp.filters", ['ngSanitize']).filter("highlight", function ($sce) {
    return function (input, currentFilter) {
        var words = input.split(" ");
        for (var i = 0; i < words.length; i++) {
            var filters = currentFilter.split(" ");
            if (words[i].indexOf("#") > -1) {
                words[i] = "<span style='color:darkgreen;font-weight:bold'>" + words[i] + "</span>";
                continue;
            }
            for (var j = 0; j < filters.length; j++) {
                if (words[i].toLowerCase().indexOf(filters[j].toLowerCase()) > -1) {
                    words[i] = "<span style='color:red;font-weight:bold'>" + words[i] + "</span>";
                }
            }
        }
        return $sce.trustAsHtml(words.join(" "));
    };
});

angular.module('tweetFilterApp.controllers').controller('ModalInstanceCtrl', function ($scope, $modalInstance, tweet) {

    $scope.tweet = tweet;

    $modalInstance.setTweet = function(tweet) {
        $scope.tweet = tweet;
    };

    $scope.ok = function () {
        $modalInstance.close($scope.tweet);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});