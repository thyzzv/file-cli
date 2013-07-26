<!DOCTYPE html>
<html>

    <head>
        <title>SIS / All Containers</title>
    </head>

    <body>

<#list containers as container>
        <h1><a href="container/${container.name}">${container.name}</a></h1>
        <ul>
<#list container.objects as object>
            <li><a href="${object.tempUrl}">${object.name}</a> (${object.size}) | <a href="">delete</a></li>
</#list>
        </ul>
</#list>

    </body>

</html>