package ca.concordia.javascript.crawler;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;
import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.BrowserConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration.CrawljaxConfigurationBuilder;
import com.crawljax.plugins.crawloverview.CrawlOverview;

/**
 * Uses Crawljax in order to crawl the web page
 * 
 * @author Davood Mazinanian Taken from css-analyzer by Davood Mazinanian
 *
 */
public class Crawler {

	private final String websiteURI;
	private final String outputFolder;

	public Crawler(String URI, String outputFolderPath) {
		websiteURI = URI;
		outputFolder = outputFolderPath;
	}

	public void start() {
		CrawljaxConfigurationBuilder builder = CrawljaxConfiguration.builderFor(websiteURI);
		configureCrawljax(builder);
		CrawljaxRunner crawljax = new CrawljaxRunner(builder.build());
		crawljax.call();
	}

	/**
	 * Set Crawljax configuration here
	 * 
	 * @param builder
	 */
	private void configureCrawljax(CrawljaxConfigurationBuilder builder) {
		JSCatcher jsCatcher = new JSCatcher();
		jsCatcher.setOutputFolder(outputFolder + "js/");

		builder.addPlugin(new CrawlOverview());
		builder.addPlugin(jsCatcher);

		//builder.addPlugin(new LoginPlugin());

		//builder.crawlRules().clickDefaultElements();
		//builder.crawlRules().dontClick("input").withAttribute("value", "I don't recognize");
		//builder.crawlRules().click("input").withAttribute("type", "submit");
		//builder.crawlRules().dontClick("a").underXPath("//*[@id='pageFooter']");
		//builder.crawlRules().dontClick("a").underXPath("//*[@id='content']/div/div[2]");
		//System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver.exe");
		//builder.setBrowserConfig(new BrowserConfiguration(BrowserType.CHROME, 2));
		builder.setBrowserConfig(new BrowserConfiguration(BrowserType.FIREFOX, 1));
		builder.crawlRules().insertRandomDataInInputForms(false);
		builder.crawlRules().clickElementsInRandomOrder(false);
		builder.crawlRules().crawlFrames(true);
		builder.crawlRules().dontClick("*");

		//com.crawljax.browser.WebDriverBackedEmbeddedBrowser s;

		builder.setOutputDirectory(new File(outputFolder + "/crawljax"));

		builder.setMaximumDepth(1);
		builder.setMaximumStates(2);

		builder.crawlRules().waitAfterReloadUrl(20, TimeUnit.MILLISECONDS);
		builder.crawlRules().waitAfterEvent(200, TimeUnit.MILLISECONDS);

	}

	public Collection<String> getInitialJSHrefs() {

		Set<String> allHrefs = new HashSet<>();
		WebDriver driver = new FirefoxDriver();
		driver.get(websiteURI);
		JavascriptExecutor js = (JavascriptExecutor) driver;

		int javaScriptLenght = Integer.valueOf(js.executeScript("return document.scripts.lenght").toString());
		for (int i = 0; i < javaScriptLenght; i++) {
			Object hrefObj = js.executeScript("return document.scripts[" + i + "].href");
			if (hrefObj == null)
				continue;
			String href = hrefObj.toString();
			if (href != null) {
				allHrefs.add(href);
			}
		}

		// Close the browser
		driver.quit();

		return allHrefs;
	}
}
