<!DOCTYPE html>
<html lang="en" ng-app="MccyApp">
<head>
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <#include "ftl-bits/head-meta.ftl">
    <#include "ftl-bits/ext-css.ftl">
    <#include "ftl-bits/app-css.ftl">
    <title>Minecraft Container Yard</title>

</head>
<body class="theme" ng-controller="MainCtrl">
<toaster-container toaster-options="toasterOptions"></toaster-container>
<div class="app-container">
    <div class="row content-container">
        <#include "frames/navbar.html">
        <#include "frames/sidebar.html">
        <div class="row">
            <div class="col-md-2 actions">
                <h2>Actions</h2>
                <div class="row">
                    <div class="col-md-12 col-sm-6" ng-repeat="v in views">
                        <button class="btn btn-default btn-block" ng-class="{'active':isCurrentView(v)}"
                            ng-click="goto(v)">{{v.label}}</button>
                    </div>
                </div>
            </div>
            <div class="col-md-10">
                <div class="row" ng-view>
                </div>
            </div>
        </div>
        <#include "frames/footer.html">
    </div>
</div>

<span>
    <#include "ftl-bits/ext-js.ftl">
    <#include "ftl-bits/app-js.ftl">
</span>
</body>
</html>
