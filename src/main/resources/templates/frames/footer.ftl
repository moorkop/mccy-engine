<footer class="app-footer">
    <div class="wrapper">
        <div class="row">
            <div class="col-xs-6 col-md-4">
                <a class="pull-left" target="_blank" href="https://github.com/itzg">© 2016 Geoff Bourne</a>.
            </div>
            <div class="col-xs-6 col-md-4 text-center">
                <#if deploymentPoweredBy??>
                <span>
                </#if>
                    <#if deploymentPoweredBy.href??>
                    <a target="_blank" href="${deploymentPoweredBy.href}">
                    </#if>
                        <#if deploymentPoweredBy.imageSrc??>
                        <img src="${deploymentPoweredBy.imageSrc}"/>
                        </#if>
                    <#if deploymentPoweredBy.href??>
                    </a>
                    </#if>
                <#if deploymentPoweredBy??>
                </span>
                </#if>
            </div>
            <div class="col-xs-6 col-md-4">
                <a class="pull-right" target="_blank" href="https://github.com/itzg/minecraft-container-yard/issues">
                    MCCY 0.0 <i class="fa fa-question-circle"></i>
                </a>
            </div>
        </div>
    </div>
</footer>

