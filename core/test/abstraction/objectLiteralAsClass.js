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

orange.prototype.getType :function() {
    return this.type;
}