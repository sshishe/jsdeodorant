package ca.concordia.javascript.language;

public enum PredefinedClasses {
	Anchor,
	anchors,
	Applet,
	applets,
	Area,
	Array,
	Body,
	Button,
	Checkbox,
	Date,
	document,
	Error,
	EvalError,
	FileUpload,
	Form,
	forms,
	frame,
	frames,
	Function,
	Hidden,
	History,
	history,
	Image,
	images,
	Link,
	links,
	location,
	Math,
	MimeType,
	mimetypes,
	navigator,
	Number,
	Object,
	Option,
	options,
	Password,
	Plugin,
	plugins,
	Radio,
	RangeError,
	ReferenceError,
	RegExp,
	Reset,
	screen,
	Script,
	Select,
	String,
	Style,
	StyleSheet,
	Submit,
	SyntaxError,
	Text,
	Textarea,
	TypeError,
	URIError,
	window,
	Worker;
	
	public static boolean contains(String token) {
		for (PredefinedClasses c : PredefinedClasses.values()) {
			if (c.name().equals(token)) {
				return true;
			}
		}
		return false;
	}
}
