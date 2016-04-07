function MockSpecRunner() {}
MockSpecRunner.prototype.run = function(spec, specDone) {
  spec.before.call(this);
  spec.body.call(this);
  spec.after.call(this);
  specDone();
};

MockSpecRunner.prototype.addFuture = function(name, fn, line) {
  return {name: name, fn: fn, line: line};
};



angular.scenario.Describe = function(descName, parent) {
  this.only = parent && parent.only;
  this.beforeEachFns = [];
  this.afterEachFns = [];
  this.its = [];
  this.children = [];
  this.name = descName;
  this.parent = parent;
  this.id = angular.scenario.Describe.id++;
}