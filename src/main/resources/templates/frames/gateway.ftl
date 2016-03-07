<div class="game-box" ng-cloak>
    <div class="login-form row">
        <div class="login-header text-center">
            <div class="login-title">Public servers on this MCCY system</div>
        </div>
        <div class="login-body">
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
