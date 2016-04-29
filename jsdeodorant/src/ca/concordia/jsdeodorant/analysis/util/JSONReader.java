package ca.concordia.jsdeodorant.analysis.util;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONReader {
	JSONParser parser;

	public JSONReader() {
		parser = new JSONParser();

	}

	public JSONArray parseArray(String filePath) {
		try {
			JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filePath));
			return jsonArray;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject parseObject(String filePath) {
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(filePath));
			return jsonObject;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getElementFromArray(String filePath, int index, String propertyName) {
		return getElement(parseArray(filePath), index, propertyName);
	}

	public String getElementFromObject(String filePath, String propertyName) {
		return (String) parseObject(filePath).get(propertyName);
	}

	public String getElement(JSONArray array, int index, String propertyName) {
		JSONObject object = (JSONObject) array.get(index);
		return (String) object.get(propertyName);
	}

	public JSONObject getObjectElement(JSONArray array, int index, String propertyName) {
		JSONObject object = (JSONObject) array.get(index);
		return object;
	}
}
