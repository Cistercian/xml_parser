<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <g:set var="entityName" value="${message(code: 'site.label', default: 'Product')}"/>
    <title>
        ${entityName}
    </title>

    <asset:stylesheet src="main.css"/>
    <asset:stylesheet src="bootstrap.min.css"/>
    <asset:stylesheet src="style.css?compile=true"/>

    <asset:javascript src="jquery.js"/>

</head>

<body>

<g:render template="/layouts/nav-panel" model=""/>
<script language="javascript" type="text/javascript">
    $(function () {
        $(document).on('change', ':file', function () {
            var input = $(this),
                    numFiles = input.get(0).files ? input.get(0).files.length : 1,
                    label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
            input.trigger('fileselect', [numFiles, label]);
        });

        $(document).ready(function () {
            $(':file').on('fileselect', function (event, numFiles, label) {

                var input = $(this).parents('.input-group').find(':text'),
                        log = numFiles > 1 ? /*numFiles + ' файлов выбрано'*/ label : label;

                if (input.length) {
                    input.val(log);
                }
            });
        });
    });
</script>

<div class="content container-fluid wam-radius wam-min-height-0">
    <div class='row'>
        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-panel">
                <div class="panel-heading ">
                    <h2 class="wam-margin-bottom-0 wam-margin-top-0">
                        <g:message code="data.title"/>
                    </h2>
                </div>

                <div id="bodyTable" class="panel-body">
                    <g:form action="importXml" enctype="multipart/form-data" useToken="true">
                        <div class="row">
                            <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                                <div class="input-group ">
                                    <label class="input-group-btn">
                                        <span class="btn btn-primary ">
                                        <g:message code="data.browse.file"/>&hellip;
                                            <input type="file" style="display: none;" multiple name="sourceXml">
                                        </span>
                                    </label>
                                    <input type="text" class="form-control" readonly>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                                <button type="submit" class="btn-danger btn-lg btn-block wam-btn-2">
                                    <g:message code="data.import.xml"/>
                                </button>
                            </div>
                        </div>
                    </g:form>

                    <div class="col-xs-12 col-md-12">
                        <g:if test="${flash.message}">
                            <div class="message" role="status">${flash.message}</div>
                        </g:if>
                    </div>
                    <div class="col-xs-12">
                        <h4><strong>
                            <g:message code="data.import.scheduler.log.label"/>
                        </strong></h4>
                    </div>
                    <div class="col-xs-12">
                        <g:each in="${Util.getSchedulerLog()}" var="line">
                            <p><span class="text-muted">${line}</span></p>
                        </g:each>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>