package photman;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

/**
 * <p>
 * This class allows the user to store options, so that the same options values are
 * in use between different program runs. As I am using the Java preferences to
 * register the options, these are only stored on the local computer. The options
 * managed by this class are :
 * <pre>
 *  - the default pictures name start (default is IMG)
 *  - the scaling method to generate the thumbnail (default is SPEED)
 *  - the size of the thumbnail (default is 96 pixels)
 * </pre>
 * </p>
 * <pre>
 * Change history:
 *   2014-08-05 GEB  Initial coding.
 *   2014-09-07 GEB  Added the show picture's registered thumbnail option.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManOptions {

	private String m_defaultName;
	private boolean m_originalThumbnail;
	private Method m_generateMethod;
	private int m_thumbnailSize;
	private Preferences m_prefs;
	
	/**
	 * Class contructor.
	 */
	public PhotManOptions() {
		try {
			m_prefs = Preferences.userRoot().node("PhotMan");
			if (m_prefs.nodeExists("")) getPreferences();
			else initOptions();
		} catch (BackingStoreException e) {
			initOptions();
		}
	}
	
	/**
	 * Returns the pictures default name start.
	 * @return the default name start
	 */
	protected String getDefaultName() {
		return m_defaultName;
	}

	/**
	 * Returns the information if the program should try to show the thumbnail registered with
	 * the picture as a first attempt.
	 * @return true if the original thumbnail should be used, false otherwise
	 */
	protected boolean isOriginalThumbnail() {
		return m_originalThumbnail;
	}
	
	/**
	 * Returns the scaling method to generate the thumbnail. For a list of the scaling methods please see 
	 * http://www.thebuzzmedia.com/software/imgscalr-java-image-scaling-library/
	 * @return the scaling method
	 */
	protected Method getGenerateMethod() {
		return m_generateMethod;
	}

	/**
	 * Returns the thumbnail size, either horizontal or vertical, depending on the picture orientation.
	 * @return the m_thumbnailSize
	 */
	protected int getThumbnailSize() {
		return m_thumbnailSize;
	}
	
	/**
	 * Calls the pop-up used to manage the options.
	 */
	protected void setOptions() {
		PhotManOptionsPane pmop = new PhotManOptionsPane(m_defaultName,m_originalThumbnail,m_generateMethod,m_thumbnailSize);
		int thSize = pmop.getThumbnailSize();
		if (thSize > 0) {
			m_defaultName = pmop.getDefaultName();
			m_originalThumbnail = pmop.isOriginalThumbnail();
			m_generateMethod = pmop.getGenerateMethod();
			m_thumbnailSize = thSize;
			setPreferences();
		}
	}
	
	/**
	 * Initializes the options values with default values.
	 */
	private void initOptions() {
		m_defaultName = "IMG";
		m_originalThumbnail = true;
		m_generateMethod = Method.SPEED;
		m_thumbnailSize = 96;		
	}

	/**
	 * Reads the options values from the Java user preferences system (which is implementation dependent).
	 */
	private void getPreferences() {
		m_defaultName = m_prefs.get("defaultName",null);
		m_originalThumbnail = m_prefs.getBoolean("originalThumbnail",true);
		String generateMethod = m_prefs.get("generateMethod",null);
		if (generateMethod == null) m_generateMethod = null;
		else m_generateMethod = setGenerateMethod(generateMethod);
		String thumbnailSize = m_prefs.get("thumbnailSize",null);
		if (thumbnailSize == null) m_thumbnailSize = -1;
		else m_thumbnailSize = setThumbnailSize(thumbnailSize);
		if (isOptionInvalid()) initOptions();
	}
	
	/**
	 * Returns the scaling method object defined by its name. If the name does not correspond
	 * to any scaling method, returns null.
	 * @param mName the method name
	 * @return the method object or null
	 */
	private Method setGenerateMethod(String mName) {
		try {
			return Scalr.Method.valueOf(mName);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Returns an integer representing the thumbnail size in pixels, from a string. If the given 
	 * string does not contain an integer, returns -1.
	 * @param size the string containing the size
	 * @return the integer thumbnail size or -1
	 */
	private int setThumbnailSize(String size) {
		try {
		return Integer.valueOf(size);
		}
		catch (Exception e) {
			return -1;
		}		
	}
	
	/**
	 * Checks if one of the options has an invalid value. 
	 * @return true if one of the options is invalid, false otherwise
	 */
	private boolean isOptionInvalid() {
		return (m_defaultName == null) || (m_generateMethod == null) || (m_thumbnailSize < 0);
	}
	
	/**
	 * Writes the options values into the Java user preferences system (which is implementation dependent).
	 */
	private void setPreferences() {
		m_prefs.put("defaultName",m_defaultName);
		m_prefs.putBoolean("originalThumbnail",m_originalThumbnail);
		m_prefs.put("generateMethod",m_generateMethod.name());
		m_prefs.put("thumbnailSize",Integer.toString(m_thumbnailSize));
	}
}
