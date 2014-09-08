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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * <p>
 * This class is used to ask the user about how to rename the images files. The user is asked for a prefix,
 * to which the program will add the index of the image file in the list shown on the screen, to form
 * the final image file name.
 * </p>
 * <pre>
 * Change history:
 *   2014-02-21 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManNewFilename extends JDialog {
	private static final long serialVersionUID = 6923872560389112826L;

	private JPanel m_contentPane;

	private String m_newFilename = "";
	private String m_nameInit;
	
	/**
	 * Class constructor.
	 */
	public PhotManNewFilename(String nameInit) {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			m_nameInit = nameInit;
			photManNewFilenameInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the file name prefix type in by the user.
	 * @return the file name prefix
	 */
	protected String getNewFilename() {
		return m_newFilename;
	}

	/**
	 * Initializes all the window components.
	 */
	private void photManNewFilenameInit() {
		m_contentPane = (JPanel) getContentPane();
		m_contentPane.setLayout(new BorderLayout());
		Image applIcon = getToolkit().createImage(getClass().getResource("photman.png"));
		setIconImage(applIcon);
		setTitle("New Files Names");
		setModal(true);
		setSize(new Dimension(320,160));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width) frameSize.width = screenSize.width;
		int x0 = (screenSize.width - frameSize.width) / 2;
		int y0 = (screenSize.height - frameSize.height) / 2;
		setLocation(x0,y0);
		createMainPane();
		validate();
		setVisible(true);
	}

	/**
	 * Creates the content pane components, so the user can type in the new file name prefix.
	 */
	private void createMainPane() {
		String info = "<html><b>Give the prefix of the new files names. "
				+ "The prefix will be combined with the rank of the image in the list, "
				+ "to form the new files names.</b></html>";
		JLabel infoLabel = new JLabel(info);
		infoLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		m_contentPane.add(infoLabel,BorderLayout.NORTH);
		final JTextField fnField = new JTextField(m_nameInit,20);
		JLabel fnLabel = new JLabel("New files names prefix");
		JPanel fnPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fnPane.add(fnLabel);
		fnPane.add(fnField);
		m_contentPane.add(fnPane,BorderLayout.CENTER);
		JButton okButton = new JButton();
		okButton.setText("Ok");
		final Window thisWindow = this;
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				m_newFilename = fnField.getText();
				processWindowEvent(new WindowEvent(thisWindow,WindowEvent.WINDOW_CLOSING));
			}});
		JButton clButton = new JButton();
		clButton.setText("Cancel");
		clButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				m_newFilename = "";
				processWindowEvent(new WindowEvent(thisWindow,WindowEvent.WINDOW_CLOSING));
			}});
		FlowLayout fl = new FlowLayout(FlowLayout.CENTER);
		fl.setHgap(20);
		JPanel southPane = new JPanel(fl);
		southPane.add(okButton);
		southPane.add(clButton);
		m_contentPane.add(southPane,BorderLayout.SOUTH);		
	}
}
