<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}"/>
    <title>
        <g:message code="default.list.label" args="[entityName]"/>
    </title>

    <asset:stylesheet src="dataTables.bootstrap.css"/>
    <asset:stylesheet src="bootstrap.min.css"/>
    <asset:stylesheet src="application1.css"/>
    <asset:stylesheet src="style.css?compile=true"/>

    <asset:javascript src="jquery.js"/>
    <asset:javascript src="jquery.dataTables.min.js"/>

</head>

<body>

<g:render template="/layouts/nav-panel" model=""/>

<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajax({
            url: '/product/getData',
            type: "GET",
            data: '',
            dataType: 'json',
            success: function (data) {

                var products = data;
                var tableData = new Array();
                products.forEach(function (product, index, products) {
                    var entity = new Array();

                    entity[0] = product.productId;
                    entity[1] = "<a href='/product/show/" + product.id + "'>" + product.title + "</a>";
                    entity[2] = product.price;
                    entity[3] = product.rating;
                    entity[4] = product.category;
                    entity[5] = product.description;
                    entity[6] = "<a href='/product/show/" + product.id + "'><img src='" + product.image + "'></a>";

                    tableData.push(entity);
                });

                $('#bodyTable').append(
                        "<table id='products' class='table table-striped table-bordered table-text wam-margin-top-2' cellspacing='0' " +
                        "width='100%'>" +
                        "</table>"
                );

                var table = $('#products').DataTable({
                    responsive: true,
                    "bLengthChange": false,
                    language: {
                        "processing": "Подождите...",
                        "search": "Поиск:",
                        "lengthMenu": "Показать _MENU_ записей",
                        "info": "Записи с _START_ до _END_ (Всего записей: _TOTAL_).",
                        "infoEmpty": "Записи с 0 до 0 из 0 записей",
                        "infoFiltered": "(отфильтровано из _MAX_ записей)",
                        "infoPostFix": "",
                        "loadingRecords": "Загрузка записей...",
                        "zeroRecords": "Записи отсутствуют.",
                        "emptyTable": "В таблице отсутствуют данные",
                        "paginate": {
                            "first": "Первая",
                            "previous": "Предыдущая",
                            "next": "Следующая",
                            "last": "Последняя"
                        },
                        "aria": {
                            "sortAscending": ": активировать для сортировки столбца по возрастанию",
                            "sortDescending": ": активировать для сортировки столбца по убыванию"
                        }
                    },
                    data: tableData,
                    columns: [
                        {title: "id"},
                        {title: "title"},
                        {title: "price"},
                        {title: "rating"},
                        {title: "category"},
                        {title: "description"},
                        {title: "image"}
                    ],
                    "sort": true,
                    "order": [[1, "DESC"]],
                });

                $('#products_filter').empty();
                $('#products_filter').append(
                        "<div class='col-xs-2 col-md-4 wam-padding-left-0 wam-padding-right-0'>" +
                        "<h5>Поиск: </h5>" +
                        "</div>" +
                        "<div class='col-xs-10 col-md-8 wam-padding-left-0 wam-padding-right-0'>" +
                        "<input id='searchDataTable' type='text' class='form-control form' placeholder='' aria-controls='products'>" +
                        "</div>"
                );
                $('#searchDataTable').on('keyup', function () {
                    table.search(this.value).draw();
                });
            }
        });
    })
</script>

<div id="list-product" class="content scaffold-list" role="main">
    <g:form action="importXml" enctype="multipart/form-data" useToken="true">
        <span class="button">
            <input type="file" name="sourceXml"/>
            <input type="submit" class="upload" value="upload"/>
        </span>
    </g:form>
</div>

<div class="content container-fluid wam-radius wam-min-height-0">
    <div class='row'>
        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h4 class="wam-margin-bottom-0 wam-margin-top-0">Данные БД</h4>
                </div>

                <div id="bodyTable" class="panel-body">
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>