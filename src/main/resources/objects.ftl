<!DOCTYPE html>
<html>

<head>
    <title>SIS / Container / ${container}</title>
    <script>
        function confirmDelete(event)
        {
            if (confirm("Are you sure you want to delete this Object?") == true) {
                window.open(window.location, "_self", false);
            }
            event.preventDefault();
        }
    </script>
</head>

<body>

    <h1>${container}</h1>
    <ul>
<#list objects as object>
        <li><a href="${object.tempUrl}">${object.name}</a> (${object.size}) | <a href="" onclick="var event=arguments[0] || window.event; confirmDelete(event)">delete</a></li>
</#list>
    </ul>

</body>

</html>