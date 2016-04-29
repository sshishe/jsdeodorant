 var  namespace={
 innerNamespace : {
      Foo: function() {
      console.log('object literal way');
    }
}
};
var aliasToNamespace;
aliasToNamespace=namespace.innerNamespace;
var b=new aliasToNamespace.Foo(); 