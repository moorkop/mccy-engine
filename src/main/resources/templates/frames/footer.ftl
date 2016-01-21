<footer class="app-footer">
    <div class="wrapper">
        <div class="col-sm-4">
            <a class="pull-left" target="_blank" href="https://github.com/itzg">© 2016 Geoff Bourne</a>.
        </div>
        <div class="col-sm-4 text-center">
        <#if deploymentPoweredBy??>
            <span>
                <#if deploymentPoweredBy.href??>
                <a target="_blank" href="${deploymentPoweredBy.href}">
                </#if>
                <img src="${deploymentPoweredBy.imageSrc}"/>
                <#if deploymentPoweredBy.href??>
                </a>
                </#if>
            </span>
        </#if>
        </div>
        <div class="col-sm-4">
            <a class="pull-right" target="_blank" href="https://github.com/itzg/minecraft-container-yard/issues">
                MCCY 0.0 <i class="fa fa-question-circle"></i>
            </a>
        </div>
    </div>
</footer>
