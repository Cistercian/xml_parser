<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <g:set var="entityName" value="${message(code: 'site.label', default: 'Product')}"/>
    <title>
        ${entityName}
    </title>

    <asset:stylesheet src="bootstrap.min.css"/>
    <asset:stylesheet src="style.css?compile=true"/>

    <asset:javascript src="jquery.js"/>
    <asset:javascript src="bootstrap.min.js"/>

</head>

<body>

<g:render template="/layouts/nav-panel" model=""/>

<div class="content container-fluid wam-radius wam-min-height-0">
    <div class='row'>
        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h2 class="wam-margin-bottom-0 wam-margin-top-0">
                        <g:message code="error.label" />
                    </h2>
                </div>

                <div class="panel-body">
                    <g:if env="development">
                        <g:if test="${Throwable.isInstance(exception)}">
                            <g:renderException exception="${exception}" />
                        </g:if>
                        <g:elseif test="${request.getAttribute('javax.servlet.error.exception')}">
                            <g:renderException exception="${request.getAttribute('javax.servlet.error.exception')}" />
                        </g:elseif>
                        <g:else>
                            <ul class="errors">
                                <li>An error has occurred</li>
                                <li>Exception: ${exception}</li>
                                <li>Message: ${message}</li>
                                <li>Path: ${path}</li>
                            </ul>
                        </g:else>
                    </g:if>
                    <g:else>
                        <ul class="errors">
                            <li>An error has occurred</li>
                        </ul>
                    </g:else>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
