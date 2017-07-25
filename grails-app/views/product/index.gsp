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
    <asset:javascript src="bootstrap.min.js"/>

</head>

<body>

<g:render template="/layouts/nav-panel" model=""/>

<div class="content container-fluid wam-radius wam-min-height-0">
    <div class='row'>
        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-panel">
                <div class="panel-heading ">
                    <h2 class="wam-margin-bottom-0 wam-margin-top-0">
                        <g:message code="product.title"/>
                    </h2>
                </div>

                <div id="bodyTable" class="panel-body wam-not-padding">
                    <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                        <button type="submit" class="btn-primary btn-lg btn-block wam-btn-1"
                                onclick="location.href = '/product/create'">
                            <g:message code="button.new.label"/>
                        </button>
                    </div>
                    <g:if test="${productCount != null && productCount > 0}">
                        <div class="col-xs-12 wam-not-padding">
                            <table class="table table-striped table-bordered table-text wam-margin-top-2 dataTable no-footer ">
                                <thead>
                                <tr>
                                    <g:sortableColumn property="productId"
                                                      title="${message(code: "product.productId")}"/>
                                    <g:sortableColumn property="title" title="${message(code: "product.label.title")}"/>
                                    <g:sortableColumn property="price" title="${message(code: "product.price")}"/>
                                    <g:sortableColumn property="rating"
                                                      title="${message(code: "product.rating")}"/>
                                    <g:sortableColumn property="category"
                                                      title="${message(code: "product.category")}"/>
                                    <g:sortableColumn property="description"
                                                      title="${message(code: "product.description")}"/>
                                    <g:sortableColumn property="image" title="${message(code: "product.image")}"/>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${productList}" var="bean" status="i">
                                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                                        <td>
                                            ${bean.productId}
                                        </td>
                                        <td>
                                            <g:link method="GET" resource="${bean}">
                                                ${bean.title}
                                            </g:link>
                                        </td>
                                        <td>
                                            ${bean.price}
                                        </td>
                                        <td>
                                            ${bean.rating}
                                        </td>
                                        <td>
                                            ${bean.getCategoryName()}
                                        </td>
                                        <td>
                                            ${bean.getFormattedDescription()}
                                        </td>
                                        <td>
                                            <g:link method="GET" resource="${bean}" >
                                                <img src="${bean.image}" class="wam-img-width">
                                            </g:link>
                                        </td>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>

                            <div class="pagination">
                                <g:paginate total="${productCount ?: 0}"/>
                            </div>
                        </div>
                    </g:if>
                    <g:else>
                        <div class="col-xs-12 wam-not-padding">
                            <h4><span><g:message code="product.table.empty"/></span></h4>
                        </div>
                    </g:else>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>