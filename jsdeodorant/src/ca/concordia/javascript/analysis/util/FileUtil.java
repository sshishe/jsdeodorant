package ca.concordia.javascript.analysis.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.io.Files;

public class FileUtil {
	public static List<String> getFilesInDirectory(String directoryPath) throws FileNotFoundException {
		List<String> jsFiles = new ArrayList<>();
		if (!Strings.isNullOrEmpty(directoryPath)) {
			File rootDir = new File(directoryPath);

			if (!rootDir.exists())
				throw new FileNotFoundException("The directory path is not valid");

			for (File f : Files.fileTreeTraverser().preOrderTraversal(rootDir)) {
				if (f.isFile() && Files.getFileExtension(f.toPath().toString()).toLowerCase().equals("js"))
					jsFiles.add(f.toPath().toString());
			}
			return jsFiles;
		}
		return null;
	}

	public static String getElementsOf(String[] source, int from, int to) {
		StringBuilder path = new StringBuilder();
		for (int index = from; index <= to; index++) {
			path.append(source[index]);
			if (from != to)
				path.append("/");
		}
		return path.toString();
	}
}
