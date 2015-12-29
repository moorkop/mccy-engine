<!DOCTYPE html>
<html lang="en" ng-app="MccyApp">
<head>
    <#include "ftl-bits/head-meta.ftl">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <title>Minecraft Container Yard</title>

    <#include "ftl-bits/ext-css.ftl">
    <#include "ftl-bits/app-css.ftl">

</head>
<body ng-controller="MainCtrl">
<toaster-container toaster-options="toasterOptions"></toaster-container>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-2">
            <img src="images/mccy_logo_120.png"/>
        </div>
        <div class="col-md-10">
                <h1> Minecraft Container Yard <small ng-show="appInfo.dockerHostUri">via {{appInfo.dockerHostUri}}</small></h1>
        </div>
    </div>

    <div class="row">
        <div class="col-md-2 actions">
            <h2>Actions</h2>

            <button class="btn btn-default" ng-class="{'active':isCurrentView(v)}" ng-repeat="v in views"
                    ng-click="goto(v)">{{v.label}}</button>
        </div>

        <div class="col-md-10">
            <div class="row" ng-view>

            </div>
        </div>

    </div>
</div>

<#include "ftl-bits/ext-js.ftl">
<#include "ftl-bits/app-js.ftl">
</body>
</html>