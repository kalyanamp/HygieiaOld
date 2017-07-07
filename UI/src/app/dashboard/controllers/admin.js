/**
 * Controller for administrative functionality
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('AdminController', AdminController).directive('fileModel', ['$parse', function ($parse) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var model = $parse(attrs.fileModel);
                var modelSetter = model.assign;
                element.bind('change', function(){
                    scope.$apply(function(){
                        modelSetter(scope, element[0].files[0]);
                    });
                });
            }
        };
    }]);


    AdminController.$inject = ['$scope', 'dashboardData', '$location','$uibModal', 'userService', 'authService', 'userData','collectorProperties'];
    function AdminController($scope, dashboardData, $location, $uibModal, userService, authService, userData, collectorProperties) {
        var ctrl = this;
        if (userService.isAuthenticated() && userService.isAdmin()) {
            $location.path('/admin');
        }
        else {
            console.log("Not authenticated redirecting");
            $location.path('#');
        }

        ctrl.storageAvailable = localStorageSupported;
        ctrl.showAuthentication = userService.isAuthenticated();
        ctrl.templateUrl = "app/dashboard/views/navheader.html";
        ctrl.username = userService.getUsername();
        ctrl.authType = userService.getAuthType();
        ctrl.collectorItemProperties = {};
        ctrl.login = login;
        ctrl.logout = logout;
        ctrl.editDashboard = editDashboard;
        ctrl.generateToken = generateToken;
        ctrl.getPropertyItemList = getPropertyItemList;
        ctrl.getPropertiesForSelected = getPropertiesForSelected;
        ctrl.addProperty = addProperty;
        ctrl.deleteProperty = deleteProperty;
        ctrl.editProperty = editProperty;
        ctrl.submitProperty = submitProperty;

        $scope.tab="dashboards";

        // list of available themes. Must be updated manually
        ctrl.themes = [
            {
                name: 'Dash',
                filename: 'dash'
            },
            {
                name: 'Dash for display',
                filename: 'dash-display'
            },
            {
                name: 'Bootstrap',
                filename: 'default'
            },
            {
                name: 'BS Slate',
                filename: 'slate'
            }];

        // used to only show themes option if local storage is available
        if(localStorageSupported) {
            ctrl.theme = localStorage.getItem('theme');
        }


        // ctrl.dashboards = []; don't default since it's used to determine loading

        // public methods
        ctrl.deleteDashboard = deleteDashboard;
        ctrl.applyTheme = applyTheme;


        // request dashboards
        dashboardData.search().then(processResponse);
        userData.getAllUsers().then(processUserResponse);
        userData.apitokens().then(processTokenResponse);


        //implementation of logout
        function logout() {
            authService.logout();
            $location.path("/login");
        }

        function login() {
          $location.path("/login")
        }

        // method implementations
        function applyTheme(filename) {
            if(localStorageSupported) {
                localStorage.setItem('theme', filename);
                location.reload();
            }
        }

        function deleteDashboard(id) {
            dashboardData.delete(id).then(function() {
                _.remove(ctrl.dashboards, {id: id});
            });
        }

        function editDashboard(item)
        {
            console.log("Edit Dashboard in Admin");

            var mymodalInstance=$uibModal.open({
                templateUrl: 'app/dashboard/views/editDashboard.html',
                controller: 'EditDashboardController',
                controllerAs: 'ctrl',
                resolve: {
                    dashboardId: function() {
                        return item.id;
                    },
                    dashboardName: function() {
                        return item.name;
                    }
                }
            });

            mymodalInstance.result.then(function(condition) {
                window.location.reload(false);
            });

        }

        function generateToken()
        {
            console.log("Generate token in Admin");

            var mymodalInstance=$uibModal.open({
                templateUrl: 'app/dashboard/views/generateApiToken.html',
                controller: 'GenerateApiTokenController',
                controllerAs: 'ctrl',
                resolve: {
                }
            });

            mymodalInstance.result.then(function(condition) {
                window.location.reload(false);
            });

        }

        function processResponse(data) {
            ctrl.dashboards = [];
            for (var x = 0; x < data.length; x++) {
                ctrl.dashboards.push({
                    id: data[x].id,
                    name: data[x].title
                });
            }
        }

        function processUserResponse(response) {
            $scope.users = response.data;
        }

        function processTokenResponse(response) {
            $scope.apitokens = response.data;
        }

        $scope.navigateToTab = function(tab) {
          $scope.tab=tab;
        }

        $scope.isActiveUser = function(user) {
          if(user.authType === ctrl.authType && user.username === ctrl.username) {
            return true;
          }
          return false;
        }

        $scope.promoteUserToAdmin = function(user) {
          userData.promoteUserToAdmin(user).then(
            function(response) {
              var index = $scope.users.indexOf(user);
              $scope.users[index] = response.data;
            },
            function(error) {
              $scope.error = error;
            }
        );
        }

        $scope.demoteUserFromAdmin = function(user) {
          userData.demoteUserFromAdmin(user).then(
            function(response) {
              var index = $scope.users.indexOf(user);
              $scope.users[index] = response.data;
            },
            function(error) {
              $scope.error = error;
            }
        );
        }
        $scope.uploadFile = function(){
            var file = $scope.myFile;
            collectorProperties.uploadFileToUrl(file);
        };
        function getPropertyItemList(filter) {
            return collectorProperties.getStoredItemPropertyList({"search": filter, "size": 20}).then(function (response){
                return response;
            });
        }

        function getPropertiesForSelected(propertyType){
           collectorProperties.getSelectedItemProperties(propertyType).then(function (response){
               ctrl.collectorItemProperties = response
            })
        }
        function addProperty(form){

            if (form.$valid) {
                var submitData = {
                    name: ctrl.collectorProperties.name,
                    propertiesKey: document.addPropertyForm.propertiesKey.value,
                    propertiesValue: document.addPropertyForm.propertiesValue.value
                };
                submitProperty(submitData);
            }

        }
        function editProperty(key, value){
            console.log(value)
            var submitData = {
                name: ctrl.collectorProperties.name,
                propertiesKey: key,
                propertiesValue: value
            };
            submitProperty(submitData);
        }
        function deleteProperty(key, value){

            var submitData = {
                name: ctrl.collectorProperties.name,
                propertiesKey: key,
                propertiesValue: value
            };
            collectorProperties
                .removeProperties(submitData)
                .success(function (data) {
                    getPropertiesForSelected(data.name)
                })
                .error(function (data) {

                });
        }
        function submitProperty(submitData){


            collectorProperties
                .updateProperties(submitData)
                .success(function (data) {
                    getPropertiesForSelected(data.name)
                })
                .error(function (data) {

                });
        }
    }
})();
