<div class="login-box">
    <div class="login-form row">
        <div class="login-header text-center">
            <div class="login-title">Minecraft Container Yard</div>
        </div>
        <div class="login-body">
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
        <div class="login-error">
            Login failed.
        </div>
    <#else>
        <div class="login-default">
            Welcome.
        </div>
    </#if>
    </div>
    <div class="text-center">
    <#if deploymentPoweredBy??>
        <a target="_blank" href="${deploymentPoweredBy.href}">
            <#if deploymentPoweredBy.imageSrc??>
                <img src="${deploymentPoweredBy.imageSrc}"/>
            </#if>
        </a>
    </#if>
    </div>
</div>
