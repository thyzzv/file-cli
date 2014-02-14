<!DOCTYPE html>
<html>

    <head>
<#if !containerName??>
        <title>SIS / All Containers</title>
</#if>
<#if containerName??>
        <title>SIS / Container / ${containerName}</title>
</#if>
        <script>
            function confirmDelete(containerName, objectName) {
                if (confirm("Are you sure you want to delete this Object?") == true) {
                    var xmlHttp = new XMLHttpRequest();
                    xmlHttp.open( "DELETE", "/object/"+containerName+"/"+objectName, false );
                    xmlHttp.send( null );

                    window.open(window.location, "_self", false);
                }
                event.preventDefault();
            }

            function checkContainerForExpires(containerName) {
                var xmlHttp = new XMLHttpRequest();
                xmlHttp.open( "POST", "/expires/"+containerName, false );
                xmlHttp.send( null );
            }

            function uploadFile(containerName) {
                document.file_upload_form.submit();
                checkContainerForExpires(containerName);
            }

        </script>
    </head>

    <body>


<#if !containerName??>
    <#list containers as container>
        <h1><a href="container/${container.name}">${container.name}</a> <#if container.expireFiles??><i style="font-size:12px">expires after 1 day</i></#if>
    </h1>

        <table>
            <thead>
            <tr>
                <th align="left">Name</th>
                <th align="right">Size</th>
                <th>Last modified</th>
        <#if container.expireFiles??>
                <th>Status</th>
        </#if>
                <th>&nbsp;</th>
            </tr>
            </thead>
            <tbody>
                <#list container.objects as object>
                <tr>
                    <td><a href="${object.tempUrl}">${object.name}</a></td>
                    <td align="right">${object.size}</td>
                    <td>| ${object.lastModified}</td>
                <#if container.expireFiles??>
                    <td>| ${object.deleteStatus}</td>
                </#if>
                    <td>| <a href="" onclick="var event=arguments[0] || window.event; confirmDelete('${container.name}', '${object.name}')">delete</a></td>
                </tr>
                </#list>
            </tbody>
        </table>

    </#list>
</#if>

<#if containerName??>
        <p><a href="/">&lt;&lt; Back</a></p>
        <h1>${containerName} <#if expireFiles??><i style="font-size:12px">expires after 1 day</i></#if></h1>
        <form name="file_upload_form" action="${upload_host}/${containerName}" method="POST"
              enctype="multipart/form-data">
            <input type="hidden" name="redirect" value="${redirect}" />
            <input type="hidden" name="max_file_size" value="${max_file_size}" />
            <input type="hidden" name="max_file_count" value="${max_file_count}" />
            <input type="hidden" name="expires" value="${expires}" />
            <input type="hidden" name="signature" value="${signature}" />
            <input type="file" name="file1" /><br />
            <input type="button" onClick="uploadFile('${containerName}')" value="Upload"/>
        </form>

        <table>
            <thead>
                <tr>
                    <th align="left">Name</th>
                    <th align="right">Size</th>
                    <th>Last modified</th>
                <#if expireFiles??>
                    <th>Status</th>
                </#if>
                    <th>&nbsp;</th>
                </tr>
            </thead>
            <tbody>
                <#list objects as object>
                    <tr>
                        <td><a href="${object.tempUrl}">${object.name}</a></td>
                        <td align="right">${object.size}</td>
                        <td>| ${object.lastModified}</td>
                    <#if expireFiles??>
                        <td>| ${object.deleteStatus}</td>
                    </#if>
                        <td>| <a href="" onclick="var event=arguments[0] || window.event; confirmDelete('${containerName}', '${object.name}')">delete</a></td>
                    </tr>
                </#list>
            </tbody>
        </table>
</#if>

    </body>

</html>