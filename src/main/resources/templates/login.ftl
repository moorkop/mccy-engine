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
        <div>
            <div class="login-form row">
                <div class="col-sm-12 text-center login-header">
                    <h4 class="login-title">Minecraft Container Yard</h4>
                </div>
                <div class="col-sm-12">
                    <div class="login-body">
                        <form action="/login" method="post">
                            <input type="hidden" name="${csrf.parameterName}" value="${csrf.token}" />
                            <div class="login-form">
                                <input name="username" type="text" class="form-control" id="username" placeholder="Username" required="true">
                            </div>
                            <div class="login-form">
                                <input name="password" type="password" class="form-control" id="password" placeholder="Password">
                            </div>
                            <div class="login-button text-center">
                                <input type="submit" class="btn btn-primary" value="Login">
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<span>
    <#include "includes/ext-js.ftl">
    <#include "includes/app-js.ftl">
</span>

</body>
</html>
