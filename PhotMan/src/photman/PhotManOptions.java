package photman;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

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
	private Preferences m_prefs;
	
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
			setPreferences();
		}
	}
	
	private void initOptions() {
		m_defaultName = "IMG";
		m_generateMethod = Method.SPEED;
		m_thumbnailSize = 96;		
	}

	private void getPreferences() {
		m_defaultName = m_prefs.get("defaultName",null);
		String generateMethod = m_prefs.get("generateMethod",null);
		if (generateMethod == null) m_generateMethod = null;
		else m_generateMethod = setGenerateMethod(generateMethod);
		String thumbnailSize = m_prefs.get("thumbnailSize",null);
		if (thumbnailSize == null) m_thumbnailSize = -1;
		else m_thumbnailSize = setThumbnailSize(thumbnailSize);
		if (isOptionInvalid()) initOptions();
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
	
	private boolean isOptionInvalid() {
		return (m_defaultName == null) || (m_generateMethod == null) || (m_thumbnailSize < 0);
	}
	
	private void setPreferences() {
		m_prefs.put("defaultName",m_defaultName);
		m_prefs.put("generateMethod",m_generateMethod.name());
		m_prefs.put("thumbnailSize",Integer.toString(m_thumbnailSize));
	}
}
