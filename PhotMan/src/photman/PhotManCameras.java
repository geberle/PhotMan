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
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

/**
 * <p>
 * This class is used to ask the user about cameras time offsets. When you take pictures of the same events
 * with different cameras, the date and time of the several cameras are usually not synchronized. If you want
 * to look at the pictures in the order the pictures were taken, you have to introduce time offsets between
 * the different cameras, taking one of the camera as a time base.
 * </p>
 * <pre>
 * Change history:
 *   2014-02-19 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManCameras extends JDialog {
	private static final long serialVersionUID = 883951658235489817L;
	private JPanel m_contentPane;

	private HashMap<String,String> m_cameras;
	
	/**
	 * Class constructor.
	 * @param cameras the cameras-time offsets map to be managed
	 */
	public PhotManCameras(HashMap<String,String> cameras) {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			m_cameras = cameras;
			photManCamerasInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the cameras-time offsets map, modified by the user.
	 * @return the cameras-time offsets map
	 */
	public HashMap<String,String> getCameras() {
		return m_cameras;
	}

	/**
	 * Initializes all the window components.
	 */
	private void photManCamerasInit() {
		m_contentPane = (JPanel) getContentPane();
		m_contentPane.setLayout(new BorderLayout());
		Image applIcon = getToolkit().createImage(getClass().getResource("photman.png"));
		setIconImage(applIcon);
		setTitle("Cameras Time Offsets");
		setModal(true);
		setSize(new Dimension(400,300));
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
	 * Creates the content pane components, particularly the table used to type in the time offsets
	 * for the different cameras.
	 */
	private void createMainPane() {
		final PhotManCamerasTableModel ctm = new PhotManCamerasTableModel();
        ctm.setData(m_cameras);
        final JTable cameras = new JTable(ctm);
        cameras.getColumnModel().getColumn(0).setPreferredWidth(250);
        cameras.getColumnModel().getColumn(1).setPreferredWidth(150);
		m_contentPane.add(new JScrollPane(cameras),BorderLayout.CENTER);
		JButton okButton = new JButton();
		okButton.setText("Ok");
		final Window thisWindow = this;
		okButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (cameras.isEditing()) cameras.getCellEditor().stopCellEditing();
				m_cameras = ctm.getData();
				processWindowEvent(new WindowEvent(thisWindow,WindowEvent.WINDOW_CLOSING));
			}});
		JButton clButton = new JButton();
		clButton.setText("Cancel");
		clButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				m_cameras = null;
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
