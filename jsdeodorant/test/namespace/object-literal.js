/* Object Literal Way */
var yourNamespace = {

    Foo: function() {
      console.log('object literal way');
    },

    Bar: function() {
    }
};

var fooInstance= new yourNamespace.Foo();
var barInstance= new yourNamespace.Bar();