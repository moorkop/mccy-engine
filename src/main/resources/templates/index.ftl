<!DOCTYPE html>
<html lang="en" ng-app="MccyApp">
<head>
    <#include "includes/head-meta.ftl">
    <!-- The above head-meta *must* come first in the head; any other head content must come *after* these tags -->
    <#include "includes/ext-css.ftl">
    <#include "includes/app-css.ftl">
    <title>Minecraft Container Yard</title>
</head>

<body class="theme" ng-controller="MainCtrl">
<toaster-container toaster-options="toasterOptions"></toaster-container>
<div class="app-container">
    <div class="row content-container">
        <#include "frames/topbar.ftl">
        <#include "frames/sidebar.ftl">
        <#include "frames/view.ftl">
    </div>
</div>

<span>
    <#include "frames/footer.ftl">
    <#include "includes/ext-js.ftl">
    <#include "includes/app-js.ftl">
</span>

</body>
</html>
