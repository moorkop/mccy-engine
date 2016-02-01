<div class="container">
    <div class="side-body">
        <div class="row text-center">
            <div class="col-xs-8 col-xs-offset-2">
                <div class="panel fresh-color panel-primary">
                    <div class="panel-body">
                        <h4>Public servers on this MCCY system</h4>

                        <div class="public-containers">
                            <div  ng-repeat="c in containers" class="public-container row">
                                <div class="col-xs-2" class="image-area">
                                    <img ng-src="{{c.status.iconSrc}}"/>
                                </div>
                                <div class="col-xs-10" class="details-area">
                                    <div class="users" ng-if="c.status._resolved">{{c.status.onlinePlayers}} / {{c.status.maxPlayers}}</div>
                                    <div class="name">{{c.name}}</div>
                                    <div class="type">{{c.type}}</div>
                                    <div class="version">{{c.version}}</div>
                                    <div class="connection" mccy-server-connection host-ip="c.hostIp" host-port="c.hostPort"></div>
                                    <div class="modpack" ng-if="c.modpack"><a ng-href="{{c.modpack}}" download="">Download Modpack</a></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>