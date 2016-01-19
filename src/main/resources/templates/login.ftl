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
        <div class="login-form">
            <div class="login-header text-center">
                <h4 class="login-title">Minecraft Container Yard</h4>
            </div>
            <div class="login-body">
                <!--suppress HtmlUnknownTarget -->
                <form action="/login" method="post" class="text-center">
                    <input name="username" type="text" class="form-control" id="username" placeholder="Username" required>
                    <input name="password" type="password" class="form-control" id="password" placeholder="Password">
                    <input type="hidden" name="${csrf.parameterName}" value="${csrf.token}" />
                    <button type="submit" class="btn btn-primary">Login</button>
                </form>
            </div>
        </div>
    </div>
</div>

<#include "includes/ext-js.ftl">
<#include "includes/app-js.ftl">

</body>
</html>
