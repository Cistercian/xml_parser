<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <g:set var="entityName" value="${message(code: 'site.label', default: 'Product')}"/>
    <title>
        <g:message code="default.list.label" args="[entityName]"/>
    </title>

    <asset:stylesheet src="main.css"/>
    <asset:stylesheet src="bootstrap.min.css"/>
    <asset:stylesheet src="style.css?compile=true"/>

    <asset:javascript src="jquery.js"/>
    <asset:javascript src="jquery.flot.js"/>
    <asset:javascript src="jquery.flot.time.js"/>
    <asset:javascript src="jquery.flot.selection.js"/>
    <asset:javascript src="bootstrap.min.js"/>

</head>

<body>

<g:render template="/layouts/nav-panel" model=""/>

<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        //запрашиваем данные и отображаем график
        $.ajax({
            url: '/viewCounter/getCountersData',
            type: "GET",
            data: '',
            dataType: 'json',
            success: function (data) {
                drawChart(data);
            }

        })
    })

    /**
     * Функция отображения графика.
     * @param d данные в JSON формате в виде [timestamp, value]
     */
    function drawChart(d) {
        var options = {
            xaxis: {
                mode: "time",
                tickLength: 5,
                timezone: "browser",
                monthNames: ["Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"]
            },
            selection: {
                mode: "x"
            },
            points:{
                show: true,
                radius: 3
            },
            lines: {
                show: true,
                fill: true,
                lineWidth: 1
            },
            colors: ["#337ab7"],
        };

        var plot = $.plot("#placeholder", [d], options);

        var overview = $.plot("#overview", [d], {
            series: {
                lines: {
                    show: true,
                    fill: true,
                    lineWidth: 1
                },
                shadowSize: 0
            },
            xaxis: {
                ticks: [],
                mode: "time"
            },
            yaxis: {
                ticks: [],
                min: 0,
                autoscaleMargin: 0.1
            },
            selection: {
                mode: "x"
            },
            colors: ["#337ab7"]
        });


        $("#placeholder").bind("plotselected", function (event, ranges) {

            $.each(plot.getXAxes(), function (_, axis) {
                var opts = axis.options;
                opts.min = ranges.xaxis.from;
                opts.max = ranges.xaxis.to;
            });
            plot.setupGrid();
            plot.draw();
            plot.clearSelection();

            overview.setSelection(ranges, true);
        });

        $("#overview").bind("plotselected", function (event, ranges) {
            plot.setSelection(ranges);
        });
    }
</script>

<div class="content container-fluid wam-radius wam-min-height-0">
    <div class='row'>
        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-panel">
                <div class="panel-heading ">
                    <h4 class="wam-margin-bottom-0 wam-margin-top-0"><g:message code="statistics.label"/></h4>
                </div>

                <div class="panel-body">
                    <div class="row">
                        <div class="col-xs-12">
                            <div id="placeholder" class="graph"></div>
                        </div>

                        <div class="col-xs-12">
                            <h4><strong><g:message code="statistics.fulltime"/></strong></h4>
                        </div>

                        <div class="col-xs-12">
                            <div id="overview" class="graph-fulltime"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>