package ca.concordia.javascript.analysis.util;

import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVWriter;

public class CSVFileWriter {
	public static void writeToFile(String fileName, String[] entries) {
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(fileName, true), ',');
			writer.writeNext(entries);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
