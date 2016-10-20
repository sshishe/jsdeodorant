function SuperHuman (name, superPower) {
    this.name = name;
    this.superPower = superPower;
}

SuperHuman.prototype.usePower = function () {
    console.log(this.superPower + "!");
};

function SuperHero (name, superPower) {
    SuperHuman.call(this, name, superPower);

    this.allegiance = "Good";
}

SuperHero.prototype = new SuperHuman();

SuperHero.prototype.usePower = function () {
    console.log("SuperHero can fly");
};


SuperHuman.prototype.save = function () {
	throw "Abstract method save not implemented";
};


SuperHero.prototype.save = function () {
	 console.log("saving the world!");
};


SuperHuman.prototype.sleep = function () {
	 alert('"Abstract method sleep not implemented');
};


SuperHero.prototype.sleep = function () {
	 console.log("now sleep");
};