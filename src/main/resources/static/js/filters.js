angular.module('Mccy.filters',[])

    .filter('asYesNo', function(){
        function render(boolVal) {
            return boolVal ? 'Yes' : 'No';
        }

        return function(value) {
            if (_.isBoolean(value)) {
                return render(value);
            }
            else if (_.isString(value)) {
                return render(value.toLowerCase() == 'true');
            }
            else {
                // generic JS truthy
                return render(value);
            }
        };
    })

;