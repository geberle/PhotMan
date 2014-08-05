package photman;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.imgscalr.Scalr.Method;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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
	        				case "defaultName": break;
	        				case "generateMethod": break;
	        				case "thumbnailSize": break;
	        				default: initOptions();
	        				}
	        			}
	        		}
	        	}
	        }
	        else initOptions();
	      }
	      catch (Exception e) {
	    	  initOptions();
	      }
	}
}
