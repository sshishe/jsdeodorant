var firstModule = require('./firstModule.js');
var secondModule;
secondModule = require('./secondModule.js');

console.log(new firstModule.test(2,4));
new secondModule.test(2,4);