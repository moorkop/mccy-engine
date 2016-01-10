<div class="by-line text-muted">
    <span><abbr title="pronounced like em-cee">MCCY</abbr> is created by
        <a target="_blank" href="https://github.com/itzg">Geoff Bourne</a></span>
    <span><a target="_blank" href="https://github.com/itzg/minecraft-container-yard/issues"><i class="fa fa-question-circle"></i> Help and Support</a></span>
    <#if deploymentPoweredBy??>
    <span>
        This deployment
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
