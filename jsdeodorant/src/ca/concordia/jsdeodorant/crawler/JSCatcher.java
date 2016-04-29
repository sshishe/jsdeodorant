package ca.concordia.jsdeodorant.crawler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.Logger;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.CrawlerContext;
import com.crawljax.core.plugin.GeneratesOutput;
import com.crawljax.core.plugin.OnNewStatePlugin;
import com.crawljax.core.state.StateVertex;

import ca.concordia.jsdeodorant.analysis.util.FileLogger;
import ca.concordia.jsdeodorant.analysis.util.IOHelper;
import ca.concordia.jsdeodorant.analysis.util.StringUtil;

/**
 * This plugin, written for Crawljax, is responsible for downloading all CSS
 * files (including those which are added dynamically at runtime) for analysing
 * purposes.
 * 
 * @author Davood Mazinanian
 * 
 */
public class JSCatcher implements OnNewStatePlugin, GeneratesOutput {

	private static final Logger LOGGER = FileLogger.getLogger(JSCatcher.class);

	private final Set<String> jsHrefs;
	private String outputPatch = "";

	public JSCatcher() {
		jsHrefs = new HashSet<>();
	}

	@Override
	public void onNewState(CrawlerContext arg0, StateVertex arg1) {
		if ("".equals(getOutputFolder())) {
			LOGGER.warn("Output folder for JSCather has not been set. " + "So there will be no output for JSCatcher. " + "Use JSCather.setOutputFolder() before CrawljaxRunner.call()");
		}
		EmbeddedBrowser browser = arg0.getBrowser();
		int javaScriptLength = Integer.valueOf(browser.executeJavaScript("return document.scripts.length").toString());
		for (int i = 0; i < javaScriptLength; i++) {
			System.out.println("SCRIPT " +i);
			Object hrefObj = browser.executeJavaScript("return document.scripts[" + i + "].src");
			if (StringUtil.isNullOrEmpty(hrefObj.toString())) {
				File rootFile = new File(getOutputFolder());
				if (!rootFile.exists() || !rootFile.isDirectory())
					rootFile.mkdir();
				File path = new File(getOutputFolder() + "/" +  arg1.getName());
				if (!path.exists() || !path.isDirectory())
					path.mkdir();
				String content = browser.executeJavaScript("return document.scripts[" + i + "].text").toString();
				IOHelper.writeStringToFile(content, getOutputFolder() + "/" +  arg1.getName() + File.separator + i + ".js");
			} else {
				if (hrefObj instanceof RemoteWebElement) {
					RemoteWebElement remoteWebElement = ((RemoteWebElement) hrefObj);
				} else {
					String href = hrefObj.toString();
					jsHrefs.add(href);
					fetchAndWriteFile(href, arg1.getName(), arg1.getUrl());
				}
			}
		}
	}

	private void fetchAndWriteFile(String href, String stateName, String forWebSite) {
		File rootFile = new File(getOutputFolder());
		if (!rootFile.exists() || !rootFile.isDirectory())
			rootFile.mkdir();

		String folderPath = getOutputFolder() + "/" + stateName;

		// Create the desired folder. One folder for each state
		File outputFolder = new File(folderPath);
		if (!outputFolder.exists() || !outputFolder.isDirectory())
			outputFolder.mkdir();

		int questionMark = href.indexOf("?");
		if (questionMark >= 0)
			href = href.substring(0, questionMark);

		int lastSlashPosition = href.lastIndexOf('/');

		// Get the name of file and append it to the desired folder
		String jsFileName = href.substring(lastSlashPosition + 1).replaceAll("[\\\\\\/:\\*\\?\\\"\\<\\>\\|]", "_");
		if (jsFileName.length() > 128)
			jsFileName = jsFileName.substring(0, 128);

		if (!jsFileName.endsWith(".js"))
			jsFileName = jsFileName + ".js";

		String jsFilePath = folderPath + "/" + jsFileName;

		while ((new File(jsFilePath)).exists())
			jsFilePath += "_.js";

		try {
			StringBuilder builder = new StringBuilder();
			if (!href.startsWith("file://")) {
				getRemoteFileContents(href, builder);
			} else {
				String localFile = IOHelper.readFileToString(href.replaceFirst("file://[/]?", ""));
				builder.append(localFile);
			}
			if (builder.length() > 0) {
				final String EOL_CHAR = "\n";
				// Lets add some information to the head of this CSS file
				String headerText = String.format("/* " + EOL_CHAR + " * Created by JSCatcher plugin for Crawljax" + EOL_CHAR + " * JS file is for Crawljax DOM state %s" + EOL_CHAR + " * JS file was contained in %s" + EOL_CHAR + " * Downloaded from %s" + EOL_CHAR + " */" + EOL_CHAR + EOL_CHAR, forWebSite, stateName, href);
				IOHelper.writeStringToFile(headerText + builder.toString().replace("\r\n", EOL_CHAR), jsFilePath);
			}
		} catch (MalformedURLException e) {
			LOGGER.warn("Malformed url for file:" + href);
		} catch (IOException e) {
			LOGGER.warn("IOException for file:" + href);
			e.printStackTrace();

		}
	}

	private void getRemoteFileContents(String href, StringBuilder builder) {
		try {
			URLConnection urlConnection = (new URL(href)).openConnection();
			urlConnection.connect();
			int contentLength = urlConnection.getContentLength();
			if (!urlConnection.getContentType().contains("application/javascript") &&
					!urlConnection.getContentType().contains("application/x-javascript") &&
					!urlConnection.getContentType().contains("text/javascript") ||
					contentLength == -1) {
				LOGGER.warn("{} is not a js file, skipping", href);
			} else {
				InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
				byte[] data = new byte[contentLength];
				int bytesRead = 0;
				int offset = 0;
				while (offset < contentLength) {
					bytesRead = inputStream.read(data, offset, data.length - offset);
					if (bytesRead == -1)
						break;
					offset += bytesRead;
				}
				inputStream.close();

				if (offset != contentLength) {
					throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
				}
				builder.append(new String(data));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public Collection<String> getAllJSHrefs() {
		return jsHrefs;
	}

	@Override
	public String getOutputFolder() {
		return outputPatch;
	}

	@Override
	public void setOutputFolder(String path) {

		File folder = new File(path);
		if (folder.exists()) {
			LOGGER.warn(String.format("JSCatcher: output folder %s is not empty. Existing files would be overwriten.", path));
		} else {
			folder.mkdir();
			LOGGER.info(String.format("Created folder %s", path));
		}
		outputPatch = folder.getAbsolutePath();

	}

}