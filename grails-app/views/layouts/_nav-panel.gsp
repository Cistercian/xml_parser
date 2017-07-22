<header class="content wam-radius">
    <nav class="navbar navbar-default wam-radius" role="navigation">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <span><g:message code="default.list.label" /></span>
        </div>

        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav navbar-right">
                <li><a href="/"><g:message code="menu.nav.home" /></a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><g:message code="menu.nav.home" /> <b class="caret"></b></a>
                    <ul class="dropdown-menu wam-dropdown-menu">
                        <li><a href="/"><g:message code="menu.nav.home" /></a></li>
                        <li class="divider"></li>
                        <li><a href="/"><g:message code="menu.nav.home" /></a></li>
                    </ul>
                </li>
                <li><a href="/statistics"><g:message code="menu.nav.statistics" /></a></li>

            </ul>

        </div><!-- /.navbar-collapse -->
        <div id="alerts" class="wam-ontop col-sm-6 col-sm-offset-6">
        </div>
    </nav>
</header>

<!-- Modal Panel -->
<div id="modal" class="modal  " tabindex="-1" role="dialog" aria-labelledby="modalHeader"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content wam-radius">
            <div id="modalHeader" class="modal-header ">
                <div  class="modal-title">
                </div>
            </div>
            <div id="modalBody" class="modal-body">
                Loading data...
            </div>
            <div id='modalFooter' class="modal-footer wam-margin-top-1">
                <div class="col-xs-12 col-md-4 col-md-offset-8">
                    <button type="button" class="btn-primary btn-lg btn-block" data-dismiss="modal">Закрыть</button>
                </div>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
