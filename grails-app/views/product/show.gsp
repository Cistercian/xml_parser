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
</head>

<body>

<g:render template="/layouts/nav-panel" model=""/>
<script language="javascript" type="text/javascript">
    function Delete(id) {
        ClearModalPanel();

        $('#modal').modal('hide');
        $('#modalBody').append(
                "<div class='col-xs-12'>" +
                "<h4><strong>Вы действительно хотите удалить запись?</strong></h4>" +
                "</div>"
        );
        $('#modalFooter').append(
                "<div class='col-xs-12 col-md-4 col-md-offset-4 wam-not-padding'>" +
                "<button type='button' class='btn btn-default btn-lg btn-block ' " +
                "onclick=\"location.href=\'\/product\/delete/" + id + "'\">" +
                "Да" +
                "</button>" +
                "</div>" +
                "<div class='col-xs-12 col-md-4 wam-not-padding'>" +
                "<button type='button' class='btn btn-primary btn-lg btn-block ' data-dismiss='modal'>" +
                "Нет" +
                "</button>" +
                "</div>"
        );

        $('#modal').modal('show');
    }
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
                        <g:message code="product.show"/>
                    </h2>
                </div>

                <div class="panel-body">
                    <g:if test="${flash.message}">
                        <div class="message" role="status">${flash.message}</div>
                    </g:if>
                %{--<f:display bean="product" />--}%
                    <div class="row">
                        <div class="col-xs-12 col-md-6">
                            %{--<img src="${createLink(controller:'Product', action:'showImage', id:"${this.product.id}")}" width="200" />--}%
                            <img src="${this.product.image}" class="wam-img-width-by-proc"/>
                        </div>

                        <div class="col-xs-12 col-md-6">
                            <div class="col-xs-12">
                                <h4><strong><g:message code="product.productId"/></strong></h4>
                                <input type="number" class="form-control wam-text-size-1" readonly
                                    value="${this.product.productId}" />
                            </div>
                            <div class="col-xs-12">
                                <h5 class="text-muted wam-margin-top-3">
                                    <span><g:message code="product.counter"/>:</span>
                                    <span>${product.getTotalCount()}</span>
                                </h5>
                            </div>
                        </div>

                        <div class="col-xs-12">
                            <h3><strong><g:message code="product.label.title"/></strong></h3>
                            <input type="text" class="form-control wam-text-size-1" readonly
                                   value="${this.product.title}"/>
                        </div>

                        <div class="col-xs-12">
                            <h3>
                                <strong><g:message code="product.rating"/></strong>
                                <g:each in="${0..this.product.getCategoryGrade()}">
                                    <asset:image src="star.ico" data-toggle="tooltip" data-placement="top"
                                        class="wam-width-star" title="${this.product.category.name}"/>
                                </g:each>
                            </h3>
                            <input type="number" class="form-control wam-text-size-1" readonly
                                   value="${this.product.rating}" />
                        </div>

                        <div class="col-xs-12">
                            <h3><strong><g:message code="product.price"/></strong></h3>
                            <input type="number" class="form-control wam-text-size-1" readonly
                                   value="${this.product.price}" />
                        </div>

                        <div class="col-xs-12">
                            <h3><strong><g:message code="product.description"/></strong></h3>
                            <textarea type="text" path="details" class="form-control input-lg erasable" rows="10"
                                  readonly placeholder='${label}'><g:fieldValue bean="${this.product}" field="description"/>
                            </textarea>
                        </div>
                    </div>
                </div>
                <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-0 wam-margin-bottom-0-1">
                    <div class="wam-not-padding panel-body">
                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" class="btn-primary btn-lg btn-block wam-btn-1"
                                    onclick="location.href = '/product/create'">
                                <g:message code="button.new.label"/>
                            </button>
                        </div>

                        <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                            <button type="submit" class="btn-default btn-lg btn-block wam-btn-1 return"
                                    onclick="location.href = '/product/edit/${this.product.id}'">
                                <g:message code="button.edit.label"/>
                            </button>
                        </div>

                        <div class="col-xs-12 col-md-6 col-md-push-6 wam-not-padding-xs">
                            <button type="submit" class="btn-danger btn-lg btn-block wam-btn-2"
                                    onclick="Delete('${this.product.id}');
                                    return false;">
                                <g:message code="button.delete.label"/>
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

