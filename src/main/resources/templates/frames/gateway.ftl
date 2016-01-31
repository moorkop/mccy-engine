<div class="container">
    <div class="side-body">
        <div class="row text-center">
            <div class="col-xs-8 col-xs-offset-2">
                <div class="panel fresh-color panel-primary">
                    <div class="panel-default">
                        <table class="table table-hover">
                            <thread>
                                <tr>
                                    <th>#</th>
                                    <th>Type</th>
                                    <th>Version</th>
                                    <th>Address</th>
                                    <th>Mods</th>
                                </tr>
                            </thread>
                            <tbody>
                            <tr ng-repeat="c in containers" class="panel panel-default">
                                <th scope="row">{{$index}}</th>
                                <td>{{c.type}}</td>
                                <td>{{c.version}}</td>
                                <td>{{serverAddress(c)}}>
                                <td><a ng-href="{{c.modpack}}">Download</a></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>