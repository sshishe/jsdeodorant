//function constructor
function Employee(name){
        this.name = name;
        this.getName = function(){
            return this.name;
        };	
}

var emp=new Employee("shahriar");