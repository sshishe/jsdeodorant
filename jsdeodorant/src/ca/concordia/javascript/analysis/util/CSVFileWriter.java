package ca.concordia.javascript.analysis.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import au.com.bytecode.opencsv.CSVWriter;

public class CSVFileWriter {
	private static CSVWriter writer;
	private Path path;

	public CSVFileWriter(String filePath) {
		try {
			path = Paths.get(filePath);
			if (Files.exists(path))
				Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeToFile(String[] entries) {
		try {
			writer = new CSVWriter(new FileWriter(path.toString(), true), ',');
			writer.writeNext(entries);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
