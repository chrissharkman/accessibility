package ch.chrissharkman.accessibility.rcp.application.poc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Class to manage content which is meant do be displayed like text/html or xml. 
 * @author ChristianHeimann
 *
 */
public class ContentManager {

	private static Logger logger = Logger.getLogger(ContentManager.class);
	private static ContentManager instance = null;
	
	private ContentManager() {
		// Exists only to defeat instantiation.
	}
	
	private String contentBundle;
	private String platformIndication = "platform:/plugin/";
	
	/**
	 * Function to get Singleton instance of ContentManager.
	 * @return ContentManager instance.
	 */
	public static ContentManager instance() {
		if (instance == null) {
			instance = new ContentManager();
		}
		return instance;
	}
	
	/**
	 * Function to set the contentBundle name for the contentManager.
	 * @param contentBundle the name of the content bundle.
	 */
	public void setContentBundle(String contentBundle) {
		this.contentBundle = contentBundle;
		logger.info("setContentBundle: " + this.contentBundle);
	}
	
	/**
	 * Function to get the name of the content bundle.
	 * @return String the name of the content bundle.
	 */
	public String getContentBundle() {
		return contentBundle;
	}
	
	/**
	 * Function to get content of a file from given path.
	 * The package from where the content is read is defined in the ContentManager.contentBundle object property.
	 * An Exception is thrown when file cannot be read.
	 * 
	 * @param path the path of the file to read.
	 * @return the content of the file as a String.
	 * @exception IOException if no file under the given path can be found and an empty String is returned.
	 */
	public String getContent(String path) {
		String content = new String("");
		try {
			logger.info("get resource: " + this.platformIndication + this.contentBundle + "/" + path);
			File file = null;
			URI resolvedURI;
			String inputLine;
			StringBuilder sb = new StringBuilder();			
			Bundle bundle = Platform.getBundle(this.contentBundle);
			URL fileURL = bundle.getEntry(path);
			URL resolvedFileURL = FileLocator.toFileURL(fileURL);
			try {
				resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null);
				file = new File(resolvedURI);
			} catch (URISyntaxException e) {
				logger.info("Resolved URI error: " + e, e);
			}
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			
			br.close();
			content = sb.toString();
		} catch (IOException e) {
			// nothing to do
			//e.printStackTrace();
			logger.warn("Exception when trying to get: " + path);
		}
		return content;
	}
	
	/**
	 * Method to get resolved file URI
	 * @param contentBundle
	 * @param path
	 * @return URI or null
	 */
	public URI getResolvedFileURI(String contentBundle, String path) {
		URI resolvedURI = null;
		Bundle bundle = Platform.getBundle(contentBundle);
		URL fileURL = bundle.getEntry(path);
		try {
			URL resolvedFileURL = FileLocator.toFileURL(fileURL);
			resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null);
		} catch (Exception e) {
			logger.info("Resolved URI error: " + e, e);
		}
		return resolvedURI;
	}
	
	public URL getResolvedFileURL(String contentBundle, String path) {
		URL resolvedURL = null;
		Bundle bundle = Platform.getBundle(contentBundle);
		URL fileURL = bundle.getEntry(path);
		try {
			resolvedURL = FileLocator.toFileURL(fileURL);
		} catch (Exception e) {
			logger.info("Resolved URL error: " + e, e);
		}
		return resolvedURL;
	}
	
	/**
	 * Function to get an xml document.
	 * @param path the path of the file to read.
	 * @return Document an xml parsed document.
	 * @exception Exception thrown if something went wrong and null object returned.
	 */
	public Document getXml(String path) {
		Document xmlDoc = null;
		try {
			String xmlString = ContentManager.instance().getContent(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			xmlDoc = dBuilder.parse(new InputSource(new StringReader(xmlString)));
		} catch (Exception e) {
			logger.info("getXml Exception: " + e, e);
		}
		return xmlDoc;
	}
}
