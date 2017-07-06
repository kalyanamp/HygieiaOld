(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('collectorProperties', collectorProperties);

    function collectorProperties($http) {
        var fileUploadRoute = '/api/collectorProperties/propertiesFileUpload/';
        var storedPropertyListRoute = '/api/collectorProperties/propertyList/';
        var selectedPropertyRoute = '/api/collectorProperties/getSelectedProperty';
        var updatePropertyRoute = '/api/collectorProperties/updateProperties/'
        return {
            getStoredItemPropertyList: getStoredItemPropertyList,
            uploadFileToUrl: uploadFileToUrl,
            getSelectedItemProperties: getSelectedItemProperties,
            updateProperties: updateProperties
        };

        function getStoredItemPropertyList( params){
            return $http.get(storedPropertyListRoute,{params: params}).then(function (response) {
                return response.data;
            });
        }

        function uploadFileToUrl(file){
            var fd = new FormData();
            fd.append('file', file);

            $http.post(fileUploadRoute, fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })
                .success(function(){
                })
                .error(function(data){
                    console.log(data)
                });
        }
        function getSelectedItemProperties(type){
            return $http.get(selectedPropertyRoute + "/"+type).then(function (response) {
                return response.data;
            });
        }

        function updateProperties(data){
            console.log(data)
            return $http.post(updatePropertyRoute, data)
                .success(function (response) {
                    return response.data;
                })
                .error(function (response) {
                    return null;
                });
        }
    }
})();
