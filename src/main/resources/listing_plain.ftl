<#if !containerName??>
<#list containers as container>
<#list container.objects as object>
${object.name}
</#list>
</#list>
</#if>
<#if containerName??>
<#list objects as object>
${object.name}
</#list>
</#if>