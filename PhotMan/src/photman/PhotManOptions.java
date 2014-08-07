package photman;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <p>
 * This class is used to ask the user about cameras time offsets. When you take pictures of the same events
 * with different cameras, the date and time of the several cameras are usually not synchronized. If you want
 * to look at the pictures in the order the pictures were taken, you have to introduce time offsets between
 * the different cameras, taking one of the camera as a time base.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * <pre>
 * Change history:
 *   2014-08-05 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManOptions {

	private String m_defaultName;
	private Method m_generateMethod;
	private int m_thumbnailSize;
	private File m_optionsFile;
	
	private final String m_fileName = "%userprofile%\\AppData\\Local\\PhotMan\\photman.xml";
	
	public PhotManOptions() {
		m_optionsFile = new File(m_fileName);
		if (m_optionsFile.exists()) readOptionsFile();
		else initOptions();
	}
	
	/**
	 * Returns the m_defaultName.
	 * @return the m_defaultName
	 */
	protected String getDefaultName() {
		return m_defaultName;
	}

	/**
	 * Returns the m_generateMethod.
	 * @return the m_generateMethod
	 */
	protected Method getGenerateMethod() {
		return m_generateMethod;
	}

	/**
	 * Returns the m_thumbnailSize.
	 * @return the m_thumbnailSize
	 */
	protected int getThumbnailSize() {
		return m_thumbnailSize;
	}
	
	protected void setOptions() {
		PhotManOptionsPane pmop = new PhotManOptionsPane(m_defaultName,m_generateMethod,m_thumbnailSize);
		int thSize = pmop.getThumbnailSize();
		if (thSize > 0) {
			m_defaultName = pmop.getDefaultName();
			m_generateMethod = pmop.getGenerateMethod();
			m_thumbnailSize = thSize;
			writeOptionsFile();
		}
	}
	
	private void initOptions() {
		m_defaultName = "IMG";
		m_generateMethod = Method.SPEED;
		m_thumbnailSize = 96;		
	}

	private void readOptionsFile() {
	    try {
	        DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
	        Document document = docBuilder.parse(m_optionsFile);
	        Node mainNode = document.getFirstChild();
	        if (mainNode.getNodeName().equals("photman")) {
	        	Node optionsNode = mainNode.getFirstChild();
	        	while (optionsNode != null) {
	        		if (optionsNode.getNodeName().equals("options")) {
	        			Node optionNode = optionsNode.getFirstChild();
	        			while (optionNode != null) {
	        				String optName = optionNode.getNodeName();
	        				switch (optName) {
	        				case "defaultName": m_defaultName = optionNode.getNodeValue(); break;
	        				case "generateMethod": m_generateMethod = setGenerateMethod(optionNode.getNodeValue()); break;
	        				case "thumbnailSize": m_thumbnailSize = setThumbnailSize(optionNode.getNodeValue());break;
	        				default: initOptions();
	        				}
	        				optionNode = optionNode.getNextSibling();
	        			}
	        			if ((m_defaultName == null) || (m_generateMethod == null) || (m_thumbnailSize <= 0)) initOptions();
	        		}
	        	}
	        }
	        else initOptions();
	      }
	      catch (Exception e) {
	    	  initOptions();
	      }
	}
	
	private Method setGenerateMethod(String mName) {
		try {
			return Scalr.Method.valueOf(mName);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	private int setThumbnailSize(String size) {
		try {
		return Integer.valueOf(size);
		}
		catch (Exception e) {
			return -1;
		}		
	}
	
	private void writeOptionsFile() {
		
	}
}
