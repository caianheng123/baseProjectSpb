var util={};
window.onload = function ()
{

    //引入基础CSS
    var bootstrap_css = document.createElement('link');
    bootstrap_css.rel = 'stylesheet';
    bootstrap_css.href = 'css/bootstrap.css';
    document.head.appendChild(bootstrap_css);
    var bootstrap_theme_css = document.createElement('link');
    bootstrap_theme_css.rel = 'stylesheet';
    bootstrap_theme_css.href = 'css/bootstrap-theme.css';
    document.head.appendChild(bootstrap_theme_css);

    (function(obj){
        obj.getCookie=function(name){
            var value =$(document).scope().getCookie(name)
            return value;
        }
        obj.addCookie= function(name,value){

            var resp = $(document).scope().cookies(name,value);
            return resp
        }
        //call(obj)
        //fetch 访问连接
        //document
        //location
        //window


    })(util)
}

var app = angular.module('myApp', []);
app.controller('myCtrl', function($scope) {
    $scope.name = "Jerome";
    $scope.sites = [
        {site : "Google", url : "http://www.google.com"},
        {site : "Runoob", url : "http://www.runoob.com"},
        {site : "Taobao", url : "http://www.taobao.com"}
    ];
});
var personP = {
    fullName: function(city, country) {
        return this.firstName + " " + this.lastName + "," + city + "," + country;
    }
}
var personP1 = {
    firstName:"John",
    lastName: "Doe"
}
personP.fullName.call(person1, "Oslo", "Norway");


var personP = {
    fullName: function(city, country) {
        return this.firstName + " " + this.lastName + "," + city + "," + country;
    }
}
var person1 = {
    firstName:"John",
    lastName: "Doe"
}
person.fullName.apply(person1, ["Oslo", "Norway"]);