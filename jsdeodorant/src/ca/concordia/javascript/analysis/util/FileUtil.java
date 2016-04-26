package ca.concordia.javascript.analysis.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.io.Files;

public class FileUtil {
	public static List<String> getFilesInDirectory(String directoryPath, String extension) throws FileNotFoundException {
		List<String> jsFiles = new ArrayList<>();
		if (!Strings.isNullOrEmpty(directoryPath)) {
			File rootDir = new File(directoryPath);

			if (!rootDir.exists())
				throw new FileNotFoundException("The directory path is not valid");

			for (File f : Files.fileTreeTraverser().preOrderTraversal(rootDir)) {
				if (f.isFile() && Files.getFileExtension(f.toPath().toString()).toLowerCase().equals(extension))
					jsFiles.add(f.toPath().toString());
			}
			return jsFiles;
		}
		return null;
	}

	public static List<String> getDirectoriesInDirectory(String directoryPath) {
		File file = new File(directoryPath);
		String[] names = file.list();
		List<String> files = new ArrayList<>();
		if (names == null)
			return null;
		for (String name : names) {
			File f = new File(directoryPath + File.separator + name);
			if (f.isDirectory()) {
				try {
					files.add(f.getCanonicalPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return files;
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
