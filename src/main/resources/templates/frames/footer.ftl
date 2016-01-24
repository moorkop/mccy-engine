<div class="app-footer">
    <div class="wrapper">
        <div class="row no-gap">
            <div class="col-xs-12 col-sm-3 col-sm-offset-1 text-center">
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
                <a class="pull-right" target="_blank" href="https://github.com/itzg/minecraft-container-yard/issues">
                    MCCY ${build.version}<#if build.branch?has_content>-${build.branch}</#if><#if build.job?has_content>-${build.job}</#if>
                    <i class="fa fa-question-circle"></i> Support
                </a>
            </div>
        </div>
    </div>
</div>

