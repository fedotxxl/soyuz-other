angular.module('common')
    .directive('cFormRemote', function ($http, $rootScope, _cFormRemoteErrorsExtractor) {
        // SOURCE - http://jsfiddle.net/rommsen/kyxrF/1/

        function getDescendantProp(obj, desc) {
            var arr = desc.split(".");
            while(arr.length && (obj = obj[arr.shift()]));
            return obj;
        }

        return {
            restrict: 'A',
            scope: true,
            require: ['cFormRemote', '?^cFormWrapper'],
            controller: function ($scope, $element, $attrs) {
                var self = this;
                self.formComponents = {};

                self.registerFormComponent = function (name, ngModel) {
                    self.formComponents[name] = ngModel;
                };
                self.hasFormComponent = function (name) {
                    return self.formComponents[name] != undefined;
                };
                self.getFormComponent = function (name) {
                    return self.formComponents[name];
                };
                self.clearServerError = function (name) {
                    self.formComponents[name].$setValidity('server', true);
                    $scope.serverValidationError[name] = null;
                };
                self.clearAllServerErrors = function () {
                    _.each(self.formComponents, function (component, name) {
                        self.clearServerError(name);
                    });

                    $scope.serverValidationError = {};
                };
                self.getFormComponentError = function (name) {
                    return $scope.serverValidationError[name];
                };

                /**
                 * Every submit should reset the form component, because its possible
                 * that the error is gone, but the form is still not valid
                 */
                self.resetFormComponentsValidity = function () {
                    _.each(self.formComponents, function (component) {
                        component.$setValidity('server', true);
                    });
                };
                self.setComponentError = function (name, message) {
                    if (self.hasFormComponent(name)) {
                        self.getFormComponent(name).$setValidity('server', false);
                    }

                    $scope.serverValidationError[name] = message;
                };
                self.addGlobalError = function (message) {
                    $scope.serverGlobalErrors.push(message);
                };
                self.clearAllGlobalErrors = function () {
                    $scope.serverGlobalErrors = [];
                };

                $scope.serverValidationError = {};
                $scope.serverGlobalErrors = [];
                // error code defaults to 400
                $scope.validation_error_code = $attrs['errorCode'] || 400;
                // property path defaults to propertyPath
                $scope.property_path_key = $attrs['propertyPath'] || 'errorField';
                // message key defaults to message
                $scope.message_key = $attrs['message'] || 'message';
            },

            'link': function (scope, element, attrs, ctrls) {
                var ctrl = ctrls[0];
                var messageCtrl = ctrls[1];

                function getScope(attr) {
                    return getDescendantProp(scope, attr);
                }

                element.submit(function () {
                    var data = attrs.cFormRemote;

                    $rootScope.$broadcast('c-form:before-submit', data);
                    ctrl.resetFormComponentsValidity();

                    if (attrs.cFormRemoteBeforeSubmit && getScope(attrs.cFormRemoteBeforeSubmit)) {
                        getScope(attrs.cFormRemoteBeforeSubmit)(getScope(attrs.cFormRemote));
                    }

                    var promise;

                    if (attrs.cRemoteFormSubmit) {
                        promise = getScope(attrs.cRemoteFormSubmit)(getScope(attrs.cFormRemote));
                    } else {
                        promise = $http[(attrs.method || 'post')].apply($http, [attrs.target, getScope(attrs.cFormRemote) || {}]);
                    }


                    //send server request
                    promise
                        .then(function (r) {
                            //clear all server errors
                            ctrl.clearAllGlobalErrors();
                            ctrl.clearAllServerErrors();

                            var response = r || {};

                            if (response.status) {
                                response = response.data;
                                r = response;
                            }

                            if (response.url) {
                                window.location.href = response.url;
                            } else if (response.message) {
                                if (messageCtrl) messageCtrl.displayMessage(response.label, response.message);
                            }

                            if (attrs.cFormRemoteSuccess && getScope(attrs.cFormRemoteSuccess)) {
                                getScope(attrs.cFormRemoteSuccess)(r);
                            }

                            $rootScope.$broadcast('c-form:submit-completed', data);
                        })
                        .catch(function (response) {
                            var data = response.data || response;
                            var status = response.status;

                            if (attrs.cFormRemoteFailure && getScope(attrs.cFormRemoteFailure)) {
                                getScope(attrs.cFormRemoteFailure)(data, status);
                            }

                            if (!status || status == scope.validation_error_code) {
                                //clear all server errors
                                ctrl.clearAllGlobalErrors();
                                ctrl.clearAllServerErrors();

                                _.each(_cFormRemoteErrorsExtractor.get(data, attrs), function (err) {
                                    if (err.message) {
                                        if (err.field) {
                                            ctrl.setComponentError(err.field, err.message);
                                        } else {
                                            ctrl.addGlobalError(err.message);
                                        }
                                    }
                                });
                            }

                            $rootScope.$broadcast('c-form:submit-completed', data);
                        });
                });
            }
        }
    })
    .directive('cFormRemoteComponent', function () {
        var getCustomValidationFunction = function (scope, attrs) {
            var customValidation = attrs.cCustomValidation;

            if (typeof customValidation != 'undefined') {
                if (customValidation) {
                    return scope[customValidation];
                } else {
                    return scope[attrs.name + 'CustomValidation'];
                }
            } else {
                return null;
            }
        };

        var getCustomOnChangeFunction = function (scope, attrs) {
            var onChange = attrs.cOnChange;

            if (typeof onChange != 'undefined') {
                if (onChange) {
                    return scope[onChange];
                }
            }

            return null;
        };

        var getOnChangeFunction = function (attrs, formCtrl, customValidationFunction, customOnChangeFunction, options) {
            var answer = null;
            var preChange = (typeof(customOnChangeFunction) == "function") ? customOnChangeFunction : function () {
            };

            if (typeof(customValidationFunction) == "function") {
                answer = function (current, prev) {
                    preChange(current, prev);

                    var error = customValidationFunction(current, prev);
                    if (error) {
                        formCtrl.setComponentError(attrs.name, error);
                    } else {
                        formCtrl.clearServerError(attrs.name);
                    }
                }
            } else if (options.resetOnChange) {
                answer = function (current, prev) {
                    preChange(current, prev);

                    if (current != prev) {
                        formCtrl.clearServerError(attrs.name)
                    }
                }
            } else {
                answer = preChange;
            }

            return answer;
        };

        var registerFormComponent = function (formCtrl, attrs, ngModel) {
            formCtrl.registerFormComponent(attrs.name, ngModel);
        };

        var resetOrChangeErrorOnModelChange = function (formCtrl, attrs, scope, options) {
            var customValidationFunction = getCustomValidationFunction(scope, attrs);
            var customOnChangeFunction = getCustomOnChangeFunction(scope, attrs);
            var onChangeFunction = getOnChangeFunction(attrs, formCtrl, customValidationFunction, customOnChangeFunction, options);

            if (onChangeFunction) {
                scope.$watch(attrs.ngModel, onChangeFunction);
            }
        };

        var initFormComponent = function (scope, attrs, ctrls) {
            var formCtrl = ctrls[0];
            var ngModel = ctrls[1];
            var options = (attrs.cFormRemoteComponent) ? JSON.parse(attrs.cFormRemoteComponent) : {};


            registerFormComponent(formCtrl, attrs, ngModel);
            resetOrChangeErrorOnModelChange(formCtrl, attrs, scope, options);
        };

        return {
            'restrict': 'A',
            'require': ['^cFormRemote', 'ngModel'],

            'link': function (scope, element, attrs, ctrls) {
                //do main stuff - register form component
                initFormComponent(scope, attrs, ctrls);
            }
        }
    });
    // .directive('cFormRemoteError', function () {
    //     return {
    //         restrict: "A",
    //         link: function ($scope, $element, attrs) {
    //             //display / hide error message
    //             $scope.$watch('serverValidationError.' + attrs.cFormRemoteError, function (value) {
    //                 if (value) {
    //                     show(value);
    //                 } else {
    //                     hide();
    //                 }
    //             });
    //         }
    //     }
    // });
