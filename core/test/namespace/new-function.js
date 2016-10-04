/* new Function way */
var ns = new function() {

	var internalFunction = function() {

	};

	this.publicFunction = function() {
		console.log('function way!');
	};
};
var publicInstance = new ns.publicFunction();
var internalInstance = new ns.internalFunction(); // exception in runtime
