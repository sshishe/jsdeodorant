package ca.concordia.javascript.language;

import java.util.ArrayList;
import java.util.List;

public class PredefinedFunctions {
	private static List<PredefinedFunction> predefinedFunctions = new ArrayList<>();

	static {
		// Number
		PredefinedFunction numberFunctions = new PredefinedFunction("Number");
		numberFunctions.addFunction("constructor");
		numberFunctions.addFunction("toExponential");
		numberFunctions.addFunction("toFixed");
		numberFunctions.addFunction("toLocaleString");
		numberFunctions.addFunction("toPrecision");
		numberFunctions.addFunction("toString");
		numberFunctions.addFunction("valueOf");
		predefinedFunctions.add(numberFunctions);

		// Boolean
		PredefinedFunction booleanFunctions = new PredefinedFunction("Boolean");
		booleanFunctions.addFunction("toSource");
		booleanFunctions.addFunction("toString");
		booleanFunctions.addFunction("valueOf");
		predefinedFunctions.add(booleanFunctions);

		// String
		PredefinedFunction stringFunctions = new PredefinedFunction("String");
		stringFunctions.addFunction("charAt");
		stringFunctions.addFunction("charCodeAt");
		stringFunctions.addFunction("concat");
		stringFunctions.addFunction("indexOf");
		stringFunctions.addFunction("lastIndexOf");
		stringFunctions.addFunction("localeCompare");
		stringFunctions.addFunction("length");
		stringFunctions.addFunction("match");
		stringFunctions.addFunction("replace");
		stringFunctions.addFunction("search");
		stringFunctions.addFunction("slice");
		stringFunctions.addFunction("split");
		stringFunctions.addFunction("substr");
		stringFunctions.addFunction("substring");
		stringFunctions.addFunction("toLocaleLowerCase");
		stringFunctions.addFunction("toLocaleUpperCase");
		stringFunctions.addFunction("toLowerCase");
		stringFunctions.addFunction("toString");
		stringFunctions.addFunction("toUpperCase");
		stringFunctions.addFunction("valueOf");
		predefinedFunctions.add(stringFunctions);

		// HTML
		PredefinedFunction htmlFunctions = new PredefinedFunction("HTML");
		htmlFunctions.addFunction("anchor");
		htmlFunctions.addFunction("big");
		htmlFunctions.addFunction("blink");
		htmlFunctions.addFunction("bold");
		htmlFunctions.addFunction("fixed");
		htmlFunctions.addFunction("fontcolor");
		htmlFunctions.addFunction("fontsize");
		htmlFunctions.addFunction("italics");
		htmlFunctions.addFunction("link");
		htmlFunctions.addFunction("small");
		htmlFunctions.addFunction("strike");
		htmlFunctions.addFunction("sub");
		htmlFunctions.addFunction("sup");
		predefinedFunctions.add(htmlFunctions);

		// Array
		PredefinedFunction arrayFunctions = new PredefinedFunction("Array");
		arrayFunctions.addFunction("concat");
		arrayFunctions.addFunction("every");
		arrayFunctions.addFunction("filter");
		arrayFunctions.addFunction("forEach");
		arrayFunctions.addFunction("indexOf");
		arrayFunctions.addFunction("join");
		arrayFunctions.addFunction("lastIndexOf");
		arrayFunctions.addFunction("map");
		arrayFunctions.addFunction("pop");
		arrayFunctions.addFunction("push");
		arrayFunctions.addFunction("reduce");
		arrayFunctions.addFunction("reduceRight");
		arrayFunctions.addFunction("reverse");
		arrayFunctions.addFunction("shift");
		arrayFunctions.addFunction("slice");
		arrayFunctions.addFunction("some");
		arrayFunctions.addFunction("toSource");
		arrayFunctions.addFunction("splice");
		arrayFunctions.addFunction("toString");
		arrayFunctions.addFunction("unshift");
		predefinedFunctions.add(arrayFunctions);

		// Date
		PredefinedFunction dateFunctions = new PredefinedFunction("Date");
		dateFunctions.addFunction("Date");
		dateFunctions.addFunction("getDate");
		dateFunctions.addFunction("getDay");
		dateFunctions.addFunction("getFullYear");
		dateFunctions.addFunction("getHours");
		dateFunctions.addFunction("getMilliseconds");
		dateFunctions.addFunction("getMinutes");
		dateFunctions.addFunction("getMonth");
		dateFunctions.addFunction("getSeconds");
		dateFunctions.addFunction("getTime");
		dateFunctions.addFunction("getTimezoneOffset");
		dateFunctions.addFunction("getUTCDate");
		dateFunctions.addFunction("getUTCDay");
		dateFunctions.addFunction("getUTCFullYear");
		dateFunctions.addFunction("getUTCHours");
		dateFunctions.addFunction("getUTCMilliseconds");
		dateFunctions.addFunction("getUTCMinutes");
		dateFunctions.addFunction("getUTCMonth");
		dateFunctions.addFunction("getUTCSeconds");
		dateFunctions.addFunction("getYear");
		dateFunctions.addFunction("setDate");
		dateFunctions.addFunction("setFullYear");
		dateFunctions.addFunction("setHours");
		dateFunctions.addFunction("setMilliseconds");
		dateFunctions.addFunction("setMinutes");
		dateFunctions.addFunction("setMonth");
		dateFunctions.addFunction("setSeconds");
		dateFunctions.addFunction("setTime");
		dateFunctions.addFunction("setUTCDate");
		dateFunctions.addFunction("setUTCFullYear");
		dateFunctions.addFunction("setUTCHours");
		dateFunctions.addFunction("setMonth");
		dateFunctions.addFunction("setUTCMilliseconds");
		dateFunctions.addFunction("setUTCMinutes");
		dateFunctions.addFunction("setUTCMonth");
		dateFunctions.addFunction("setUTCSeconds");
		dateFunctions.addFunction("setYear");
		dateFunctions.addFunction("toDateString");
		dateFunctions.addFunction("toGMTString");
		dateFunctions.addFunction("toLocaleDateString");
		dateFunctions.addFunction("toLocaleFormat");
		dateFunctions.addFunction("toLocaleString");
		dateFunctions.addFunction("toLocaleTimeString");
		dateFunctions.addFunction("toSource");
		dateFunctions.addFunction("toString");
		dateFunctions.addFunction("toTimeString");
		dateFunctions.addFunction("toUTCString");
		dateFunctions.addFunction("valueOf");
		dateFunctions.addFunction("parse");
		dateFunctions.addFunction("UTC");
		predefinedFunctions.add(dateFunctions);

		// Math
		PredefinedFunction mathFunctions = new PredefinedFunction("Math");
		dateFunctions.addFunction("abs");
		dateFunctions.addFunction("acos");
		dateFunctions.addFunction("asin");
		dateFunctions.addFunction("atan");
		dateFunctions.addFunction("atan2");
		dateFunctions.addFunction("ceil");
		dateFunctions.addFunction("cos");
		dateFunctions.addFunction("exp");
		dateFunctions.addFunction("floor");
		dateFunctions.addFunction("log");
		dateFunctions.addFunction("max");
		dateFunctions.addFunction("min");
		dateFunctions.addFunction("pow");
		dateFunctions.addFunction("random");
		dateFunctions.addFunction("round");
		dateFunctions.addFunction("sin");
		dateFunctions.addFunction("sqrt");
		dateFunctions.addFunction("tan");
		dateFunctions.addFunction("toSource");
		predefinedFunctions.add(mathFunctions);

		// RegExp
		PredefinedFunction regexFunctions = new PredefinedFunction("Regex");
		regexFunctions.addFunction("exec");
		regexFunctions.addFunction("test");
		regexFunctions.addFunction("toSource");
		regexFunctions.addFunction("toString");
		predefinedFunctions.add(regexFunctions);

		// Node Console object
		PredefinedFunction consoleFunction = new PredefinedFunction("console");
		consoleFunction.addFunction("log");
		consoleFunction.addFunction("error");
		regexFunctions.addFunction("assert");
		regexFunctions.addFunction("info");
		regexFunctions.addFunction("dir");
		regexFunctions.addFunction("log");
		regexFunctions.addFunction("time");
		regexFunctions.addFunction("timeEnd");
		regexFunctions.addFunction("trace");
		regexFunctions.addFunction("warn");
		predefinedFunctions.add(consoleFunction);
	}

	public static boolean isItPredefined(String name) {
		for (PredefinedFunction predefinedFunction : predefinedFunctions) {
			if (predefinedFunction.contains(name))
				return true;
		}
		return false;
	}
}
