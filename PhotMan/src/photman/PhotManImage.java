/**   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package photman;

import java.io.File;
import java.util.Date;

import javax.swing.Icon;

import com.drew.metadata.exif.ExifDirectory;

/**
 * <p>
 * This class contains all the information needed to describe a image file.
 * </p>
 * <pre>
 * Change history:
 *   2014-02-17 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManImage implements Comparable<PhotManImage>{
	private File m_originalFile;
	private String m_finalName;
	private String m_cameraModel;
	private Icon m_thumbnail;
	private ExifDirectory m_exif;
	private Date m_creationDate;
	
	/**
	 * Returns the original file object.
	 * @return the original file
	 */
	protected File getOriginalFile() {
		return m_originalFile;
	}
	
	/**
	 * Sets the original file object.
	 * @param originalFile the original file to set
	 */
	protected void setOriginalFile(File originalFile) {
		m_originalFile = originalFile;
	}
	
	/**
	 * Returns the final file name.
	 * @return the final name
	 */
	protected String getFinalName() {
		return m_finalName;
	}
	
	/**
	 * Sets the final file name.
	 * @param finalName the final name to set
	 */
	protected void setFinalName(String finalName) {
		m_finalName = finalName;
	}
	
	/**
	 * Returns the thumbnail associated to this image file.
	 * @return the thumbnail
	 */
	protected Icon getThumbnail() {
		return m_thumbnail;
	}
	
	/**
	 * Sets the thumbnail associated to this image file.
	 * @param thumbnail the thumbnail to set
	 */
	protected void setThumbnail(Icon thumbnail) {
		m_thumbnail = thumbnail;
	}

	/**
	 * Returns the EXIF meta-data associated to this image file.
	 * @return the EXIF meta-data
	 */
	protected ExifDirectory getExif() {
		return m_exif;
	}

	/**
	 * Sets the EXIF meta-data associated to this image file.
	 * @param exif the EXIF meta-data to set
	 */
	protected void setExif(ExifDirectory exif) {
		m_exif = exif;
	}

	/**
	 * Returns the image creation date.
	 * @return the creation date
	 */
	protected Date getCreationDate() {
		return m_creationDate;
	}

	/**
	 * Sets the image creation date.
	 * @param creationDate the creation date to set
	 */
	protected void setCreationDate(Date creationDate) {
		m_creationDate = creationDate;
	}

	/**
	 * Returns the camera make and/or model.
	 * @return the camera model
	 */
	protected String getCameraModel() {
		return m_cameraModel;
	}

	/**
	 * Sets the camera make and/or model.
	 * @param cameraModel the camera model to set
	 */
	protected void setCameraModel(String cameraModel) {
		m_cameraModel = cameraModel;
	}

	/**
	 * Compares this object with a another object of this class, by comparing their image creation date.
	 * @param pmi the other object to compare to
	 * @return -1, 0 or 1 if this object is older, of the same date and time or newer than the compared object
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PhotManImage pmi) {
		Date otherDate = pmi.getCreationDate();
		if (m_creationDate == null)
			if (otherDate == null) return 0;
			else return 1;
		else if (otherDate == null) return -1;
		return m_creationDate.compareTo(otherDate);
	}
}
