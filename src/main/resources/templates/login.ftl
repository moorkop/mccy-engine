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
    <div class="carousel-inner" role="listbox">
        <div class="item active">
            <#include "frames/gateway.ftl">
        </div>
        <div class="item">
            <#include "frames/login-form.ftl">
        </div>
    </div>

    <a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev">
        <span class="fa fa-caret-left fa-3x icon" aria-hidden="true"></span>
        <span class="sr-only">Previous</span>
    </a>

    <a class="right carousel-control" href="#myCarousel" role="button" data-slide="next">
        <span class="fa fa-caret-right fa-3x icon" aria-hidden="true"></span>
        <span class="sr-only">Next</span>
    </a>
</div>


<#include "frames/footer.ftl">

<span>
    <#include "includes/ext-js.ftl">
    <#include "includes/app-js.ftl">
</span>

</body>
</html>
