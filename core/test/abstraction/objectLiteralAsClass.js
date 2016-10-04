var singletonClass ={
 	foo: 0,
 	printIt: function(){
 		console.log(this.foo);
	}
}

singletonClass.foo = 5;
singletonClass.printIt();

var orange = new function() {
    this.type = "naval";
    this.color = "orange";
    this.getInfo = function() {
        return this.color ;
    };
    
    getMoreInfo: function() {
        return this.color + ' ' + this.type + ' apple';
    }
}


orange.prototype.getColor= function() {
    return this.color;
}

function Car(make, model, level, color, warranty) {
    this.make     = make;
    this.model    = model;
    this.level    = level;
    this.color    = color;
    this.warranty = warranty;
}

Car.prototype = {
    toString: function () {
      return this.make + ', ' + this.model + ', ' + this.level + ', '+ this.color + ', ' + this.warranty;
    }
};

var bmw= new Car(2016, 'BMW', 1,'white', 1);
alert(bmw.toString());