var namespace={
  Employee : function(){
     this.name = 'Jhon';
     this.department = 'sales;
  }

}


namespace.Employee.prototype.getInfo= function(){
	return this.name+ ","+ this.department;
}


namespace.Employee.prototype={
		
	setName: function(name) {
		this.name= name;
	},

	setDepartment: function( dep) {
		this.department=department;
	},
	
	computeSalary:  function(){
		if (this.department=== "engineering"){
			return 75K;
		}else{
			return 50K;	
		}
	}

};


var Engineer = function(name, department){
	this.name = ‘John’;
    this.department = ‘sales’;
 }

Engineer.prototype = new namespace.Employee();



// Static method
namespace.Employee.Talk= function(){
	throw "Abstract method updateChartsData not implemented";
}
