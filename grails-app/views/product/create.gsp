<%@ page import="xml_parser.Product" %>
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
    <asset:javascript src="functions.js"/>
</head>

<body>

<g:render template="/layouts/nav-panel" model=""/>
<script language="javascript" type="text/javascript">
    function ClearModalPanel() {
        $('#modalTitle').text("");
        $('[id^="modalBody"]').each(function () {
            $(this).empty();
        });
        $('[id^="modalFooter"]').each(function () {
            $(this).empty();
        });
        //гарантированно чистим остатки всплывающего окна
        $('.modal-backdrop').each(function () {
            $(this).remove();
        });
        //гарантированно-гарантированно чистим остатки всплывающего окна
        $('body').removeClass('modal-open');
    }
</script>

<div class="content container-fluid wam-radius wam-min-height-0">
    <div class='row'>
        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-panel">
                <div class="panel-heading ">
                    <h2 class="wam-margin-bottom-0 wam-margin-top-0">
                        <g:message code="product.new"/>
                    </h2>
                </div>

                <div class="panel-body">
                    <g:if test="${this.product.id != null}">
                        <g:set var="action" value="update/${this.product.id}" />
                    </g:if>
                    <g:else>
                        <g:set var="action" value="save" />
                    </g:else>

                    <form id="update" method="POST" action="/product/${action}">

                        <g:if test="${flash.message}">
                            <div class="message" role="status">${flash.message}</div>
                        </g:if>
                        <div class="row">

                            <div class="col-xs-12 col-md-12">
                                <g:hasErrors bean="${this.product}">
                                    <ul class="errors has-error" role="alert">
                                        <g:eachError bean="${this.product}" var="error">
                                            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                                        </g:eachError>
                                    </ul>
                                </g:hasErrors>
                            </div>

                            <div class="col-xs-12 col-md-6">
                                <h4><strong><g:message code="product.image"/></strong></h4>
                                <input type="text" name="image" class="form-control wam-text-size-1"
                                       value="${this.product.image}"/>
                            </div>

                            <div class="col-xs-12 col-md-6">
                                <h4><strong><g:message code="product.productId"/></strong></h4>
                                <input type="number" name="productId" class="form-control wam-text-size-1"
                                       value="${this.product.productId}"/>
                            </div>

                            <div class="col-xs-12">
                                <h3><strong><g:message code="product.label.title"/></strong></h3>
                                <input type="text" id="inputTitle" name="title" class="form-control wam-text-size-1"
                                       value="<g:fieldValue bean="${this.product}" field="title"/>"/>
                            </div>

                            <div class="col-xs-12">
                                <h3>
                                    <strong><g:message code="product.rating"/></strong>
                                </h3>
                                <input type="number" name="rating" class="form-control wam-text-size-1"
                                       value="${this.product.rating}"/>
                            </div>

                            <div class="col-xs-12">
                                <h3><strong><g:message code="product.price"/></strong></h3>
                                <input type="number" name="price" class="form-control wam-text-size-1"
                                       value="${this.product.price}"/>
                            </div>

                            <div class="col-xs-12">
                                <h3><strong><g:message code="product.description"/></strong></h3>
                                <textarea type="text" name="description" class="form-control input-lg erasable" rows="10"><g:fieldValue bean="${this.product}" field="description"/></textarea>
                            </div>
                        </div>
                    </form>
                    <div class="wam-not-padding panel-body">
                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" class="btn-primary btn-lg btn-block wam-btn-1"
                                    onclick="update.submit();">
                                <g:message code="button.ok.label"/>
                            </button>
                        </div>

                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" class="btn-default btn-lg btn-block wam-btn-1 return"
                                    onclick="location.href = '/'">
                                <g:message code="button.cancel.label"/>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>

