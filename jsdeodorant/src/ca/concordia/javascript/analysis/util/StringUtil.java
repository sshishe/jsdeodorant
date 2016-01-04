package ca.concordia.javascript.analysis.util;

public class StringUtil {
	public static boolean isNullOrEmpty(String param) {
		return param == null || param.trim().length() == 0;
	}
}
