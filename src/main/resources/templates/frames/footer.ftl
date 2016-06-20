<div class="app-footer">
    <div class="wrapper">
        <div class="row no-gap">
            <div class="col-sm-11 col-sm-offset-1">
                <div class="col-xs-12 col-sm-4 text-center">
                    <a target="_blank" href="https://github.com/itzg">© 2016 Geoff Bourne</a>.
                </div>
                <div class="col-xs-12 col-sm-4 text-center">
                    MCCY ${build.version}
                    <#if build.branch?has_content>-${build.branch}</#if>
                    <#if build.job?has_content>-${build.job}</#if>
                </div>
                <div class="col-xs-12 col-sm-4 text-center">
                    <a target="_blank" href="https://github.com/moorkop/mccy-engine/issues">
                        Support <i class="fa fa-question-circle"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

