
/////////////////// using Object.create //////////////////////////////

function Shape() {
  this.x = 0;
  this.y = 0;
}

// Rectangle - subclass
function Rectangle() {
  Shape.call(this); // call super constructor
} 

// subclass extends superclass
Rectangle.prototype = Object.create(Shape.prototype);

/////////////////// using new //////////////////////////////

function Product(name, price) {
  this.name = name;
  this.price = price;

  if (price < 0)
    throw RangeError('Cannot create product "' + name + '" with a negative price');
  return this;
}

function Food(name, price) {
  Product.call(this, name, price);
  this.category = 'food';
}
Food.prototype = new Product();

function Toy(name, price) {
  Product.call(this, name, price);
  this.category = 'toy';
}
Toy.prototype = new Product();

var cheese = new Food('feta', 5);
var fun = new Toy('robot', 40);



/////////////////// No inheritance //////////////////////////////

function greet() {
  var reply = [this.person, 'Is An Awesome', this.role].join(' ');
  console.log(reply);
}

var i = {
  person: 'Douglas Crockford', role: 'Javascript Developer'
};

greet.call(i); // Douglas Crockford Is An Awesome Javascript Developer
