<!DOCTYPE html>
<html lang="en">
<head>
<#include "includes/head-meta.ftl">
    <!-- The above head-meta *must* come first in the head; any other head content must come *after* these tags -->
<#include "includes/ext-css.ftl">
<#include "includes/app-css.ftl">
    <title>Container Yard - Login</title>
</head>

<body class="theme login-page">
<div class="container">
    <div class="login-box">
        <div class="login-form row">
            <div class="col-xs-12 text-center login-header">
                <div class="login-title">Minecraft Container Yard</div>
            </div>
            <div class="col-xs-12 login-body">
                <form action="/login" method="post" class="text-center">
                    <input name="username" type="text" class="form-control" id="username" placeholder="Username" required>
                    <input name="password" type="password" class="form-control" id="password" placeholder="Password">
                    <input type="hidden" name="${csrf.parameterName}" value="${csrf.token}" />
                    <div class="login-button text-center">
                        <button type="submit" class="btn btn-default">Login</button>
                    </div>
                </form>
            </div>
            <#if error>
            <div class="col-xs-12 login-error">
                Login failed.
            </div>
            <#else>
            <div class="col-xs-12 login-default">
                Welcome.
            </div>
            </#if>
        </div>
        <div class="col-xs-12 text-center">
            <#if deploymentPoweredBy??>
            <a target="_blank" href="${deploymentPoweredBy.href}">
                <#if deploymentPoweredBy.imageSrc??>
                <img src="${deploymentPoweredBy.imageSrc}"/>
                </#if>
            </a>
            </#if>
        </div>
    </div>
</div>

<#include "includes/ext-js.ftl">
<#include "includes/app-js.ftl">

</body>
</html>
