<!DOCTYPE html>
<html lang="en">
<head>
    <#include "ftl-bits/head-meta.ftl">
    <!-- The above head-meta *must* come first in the head; any other head content must come *after* these tags -->
    <#include "ftl-bits/ext-css.ftl">
    <#include "ftl-bits/app-css.ftl">
    <title>Minecraft Container Yard - Login</title>
</head>

<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-2">
            <img src="images/mccy_logo_120.png"/>
        </div>
        <div class="col-md-10">
            <h1>Minecraft Container Yard</h1>
        </div>
    </div>

    <div class="row">
        <div class="col-md-4 col-md-offset-2">
            <form action="/login" method="post">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input name="username" class="form-control" id="username" required="true">
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input name="password" type="password" class="form-control" id="password" placeholder="Password">
                </div>

                <input type="hidden" name="${csrf.parameterName}" value="${csrf.token}" />
                <button type="submit" class="btn btn-default">Login</button>
            </form>
        </div>
    </div>
</div>

<span>
    <#include "ftl-bits/ext-js.ftl">
    <#include "ftl-bits/app-js.ftl">
</span>

</body>
</html>
