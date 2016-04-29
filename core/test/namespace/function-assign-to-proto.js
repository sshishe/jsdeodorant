function MyClass(){
	MyClass.prototype.anotherFunction = function(){

    }
}

MyClass.prototype.myFunction = function(){

}

var myClassInstance = new MyClass();
myClassInstance.myFunction();
myClassInstance.anotherFunction();
