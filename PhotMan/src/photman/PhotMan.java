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

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * <p>
 * This program is used to manipulate photos files, i.e. rename files, insert
 * files in given location, reorder files by pictures creation date, and so on.
 * </p>
 * <pre>
 * Change history:
 *   2014-02-08 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle
 */
public class PhotMan {
	private PhotManFrame m_pFrame;

  /**
   * Constructor of the class.
   */
	public PhotMan() {
		m_pFrame = new PhotManFrame();
		m_pFrame.validate();
		m_pFrame.setVisible(true);
	}
	
	/**
	 * Checks if the current Java version is 7 or higher. This is because some features from 
	 * Java 7 is used in this program.
	 * @return true if Java version is 7 or higher, false otherwise
	 */
	private static boolean checkVersion() {
		boolean isOk = false;
		String version = System.getProperty("java.version");
		String[] vers = version.split("\\.");
		if (vers.length > 1) {
			int vers0 = Integer.parseInt(vers[0]);
			int vers1 = Integer.parseInt(vers[1]);
			isOk = ((vers0 == 1) && (vers1 >= 7)) || (vers0 > 1);
		}
		return isOk;
	}

  /**
   * Main entry point. No arguments.
   * 
   * @param args
   */
  public static void main(String[] args) {
    try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		if (checkVersion()) new PhotMan();
		else {
			JOptionPane.showMessageDialog(null,"Sorry, this program can only run on Java version 7 or higher.",
					"PhotMan - Warning",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	} catch (InstantiationException e) {
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		e.printStackTrace();
	} catch (UnsupportedLookAndFeelException e) {
		e.printStackTrace();
	}
  }
}
