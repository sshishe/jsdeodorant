var firstModule = require('./firstModule');
var secondModule;
// alisaed require
secondModule = require('./secondModule.js');
var customUtil = require('./custom-folder/util');
var anotherUtilNodeWithIndex = require('anotherUtil');
var moduleWithPackageFile = require ('module-with-packageconf');


new firstModule.Test(2,4);
new secondModule.test(2,4);
new customUtil.pow(2);
new anotherUtilNodeWithIndex.minus(4,2);
new moduleWithPackageFile.plus(3,2);
firstModule.sayHello('hello world!');