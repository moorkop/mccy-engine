<!DOCTYPE html>
<html lang="en" ng-app="MccyApp">
<head>
<#include "includes/head-meta.ftl">
    <!-- The above head-meta *must* come first in the head; any other head content must come *after* these tags -->
<#include "includes/ext-css.ftl">
<#include "includes/app-css.ftl">
    <title>Container Yard - Login</title>
</head>

<body class="theme login-page" ng-controller="LoginCtrl">
<div id="myCarousel" class="carousel slide">
    <!-- Indicators -->
    <ol class="carousel-indicators">
        <li id="indicator-gateway" data-target="#myCarousel" data-slide-to="0" class="active"></li>
        <li id="indicator-login" data-target="#myCarousel" data-slide-to="1"></li>
    </ol>

    <!-- Wrapper for slides -->
    <div class="carousel-inner" role="listbox">
        <div class="item active">
            <#include "frames/gateway.ftl">
        </div>
        <div class="item">
            <#include "frames/login-form.ftl">
        </div>
    </div>
</div>

<#include "includes/ext-js.ftl">
<#include "includes/app-js.ftl">

</body>
</html>
