// /* IIFE way */
var someObj = (function() {
  var instance = {},
  inner = 'some value';

  instance.innerObj = (function() {
    var innerInstance = {};
    innerInstance.deepInnerClass = function() {
      console.log('deep inner class');
    }
    return innerInstance;
  })();

    instance.publicClass = function() {
      console.log('inner class created');
    };
    return instance;
  })();

  var innerClass = new someObj.publicClass();
  var deepestInnerClass=new someObj.innerObj.deepInnerClass();

