<div class="side-menu sidebar-inverse">
    <nav class="navbar navbar-default" role="navigation">
        <div class="side-menu-container">
            <div class="navbar-header">
                <a class="navbar-brand" href="#">
                    <div class="icon fa fa-cubes"></div>MCCY
                </a>
                <button type="button" class="navbar-expand-toggle pull-right visible-xs">
                    <i class="fa fa-times icon"></i>
                </button>
            </div>
            <ul class="nav navbar-nav" ng-repeat="view in views">
                <li ng-class="{'active':isCurrentView(view)}">
                    <a href="" ng-click="goto(view)">
                        <span class="{{view.settings.icon}}"></span><span class="title">{{view.settings.label}}</span>
                    </a>
                </li>
            </ul>
        </div>
    </nav>
</div>
