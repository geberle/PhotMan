package photman;

import java.io.File;
import java.io.FilenameFilter;

/**
 * <p>
 * This class is used to filter the files names in the process of selecting the directories containing
 * image files. Actually only JPEG files are allowed.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * <pre>
 * Change history:
 *   2014-02-11 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManFilenameFilter implements FilenameFilter {
	private final String[] m_allowed = {"jpg","jpeg"};
	private String m_currentDir;
	
	/**
	 * Class constructor.
	 * @param curDir the current directory (to be filtered)
	 */
	public PhotManFilenameFilter(String curDir) {
		m_currentDir = curDir;
	}

	/**
	 * Returns true if the given file can be accepted, false otherwise. Actually only JPEG files are accepted
	 * @param f the directory containing the file to be tested
	 * @param s the file name
	 * @return true if the file has an accepted extension, false otherwise
	 * 
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File f, String s) {
		if (!f.getAbsolutePath().equalsIgnoreCase(m_currentDir)) return false;
	    String extension = getExtension(s);
	    if ((extension != null) && (m_allowed != null))
	      for (String ext : m_allowed)
	        if (ext.equalsIgnoreCase(extension)) return true;							
		return false;
	}
	
	/**
	 * Extracts the extension of a given file name.
	 * @param s the file name
	 * @return the file extension
	 */
	private String getExtension(String s) {
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) return s.substring(i + 1).toLowerCase();
		return null;
	}
}
