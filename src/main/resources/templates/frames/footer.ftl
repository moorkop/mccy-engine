<div class="app-footer">
    <div class="wrapper">
        <div class="row no-gap">
            <div class="col-xs-12 col-sm-4 text-center">
                <a target="_blank" href="https://github.com/itzg">© 2016 Geoff Bourne</a>.
            </div>
            <div class="col-xs-12 col-sm-4 text-center">
                <#if deploymentPoweredBy??>
                    <a target="_blank" href="${deploymentPoweredBy.href}">
                        <#if deploymentPoweredBy.imageSrc??>
                        <img src="${deploymentPoweredBy.imageSrc}"/>
                        </#if>
                    </a>
                </#if>
            </div>
            <div class="col-xs-12 col-sm-4 text-center">
                <a target="_blank" href="https://github.com/itzg/minecraft-container-yard/issues">
                    MCCY 0.0 <i class="fa fa-question-circle"></i>
                </a>
            </div>
        </div>
    </div>
</div>

