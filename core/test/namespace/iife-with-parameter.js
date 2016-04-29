// namespace (our namespace name) and undefined are passed here
// to ensure 1. namespace can be modified locally and isn't
// overwritten outside of our function context
// 2. the value of undefined is guaranteed as being truly
// undefined. This is to avoid issues with undefined being
// mutable pre-ES5.
;(function ( namespace, undefined ) {
    // private properties
    var foo = "foo",
        bar = "bar";
    // public methods and properties
    namespace.foobar = "foobar";
    namespace.sayHello = function () {
        speak("hello world");
    };
    // private method
    function speak(msg) {
        console.log("You said: " + msg);
    };
    // check to evaluate whether 'namespace' exists in the
    // global namespace - if not, assign window.namespace an
    // object literal
})(namespace = {});
// we can then test our properties and methods as follows
// public
console.log(namespace.foobar); // foobar
var myClass=new namespace.sayHello(); // hello world
// assigning new properties
namespace.foobar2 = "foobar";
console.log(namespace.foobar2);