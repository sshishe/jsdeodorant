var myApp = myApp || {};
myApp.utils =  {};
(function() {
    var val = 5;
    this.getValue = function() {
        console.log('get val:'+val);
    };
    this.setValue = function(newVal) {
        val = newVal;
    }
    // also introduce a new sub-namespace
    this.tools = {};
}).apply(myApp.utils);
// inject new behaviour into the tools namespace
// which we defined via the utilities module
(function(){
    this.diagnose = function(){
        return 'diagnosis';
    }
}).apply(myApp.utils.tools);
// note, this same approach to extension could be applied
// to a regular IIFE, by just passing in the context as
// an argument and modifying the context rather than just
// 'this'
// testing
console.log(myApp); //the now populated namespace
console.log(new myApp.utils.getValue()); // test get
myApp.utils.setValue(25); // test set
//console.log(myApp.utils.getValue());
console.log(new myApp.utils.tools.diagnose());