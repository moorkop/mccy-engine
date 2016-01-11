<!DOCTYPE html>
<html lang="en" ng-app="MccyApp">
<head>
    <#include "ftl-bits/head-meta.ftl">
    <!-- The above head-meta *must* come first in the head; any other head content must come *after* these tags -->
    <#include "ftl-bits/ext-css.ftl">
    <#include "ftl-bits/app-css.ftl">
    <title>Minecraft Container Yard</title>
</head>

<body class="theme" ng-controller="MainCtrl">
<toaster-container toaster-options="toasterOptions"></toaster-container>
<div class="app-container">
    <div class="row content-container">
        <#include "frames/topbar.html">
        <#include "frames/sidebar.html">
        <#include "frames/view.html">
        <#include "frames/footer.html">
    </div>
</div>

<span>
    <#include "ftl-bits/ext-js.ftl">
    <#include "ftl-bits/app-js.ftl">
</span>

</body>
</html>
