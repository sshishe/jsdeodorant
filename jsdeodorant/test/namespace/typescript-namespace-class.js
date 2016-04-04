var A;
(function (A) {
    var Twix = (function () {
        function Twix() {
        alert('hello');
        }
        return Twix;
    }());
    A.Twix = Twix;
})(A || (A = {}));

 var  b = new A.Twix();