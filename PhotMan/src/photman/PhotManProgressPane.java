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

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * <p>
 * This class is used to show a progress bar on the screen.
 * </p>
 * <pre>
 * Change history:
 *   2014-03-01 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManProgressPane extends JDialog {
	private static final long serialVersionUID = 8424041433793089333L;

	private PhotManProgressBar m_progress;
	private String m_message;
	private Icon m_icon;
	private int m_step;
	private Timer m_timer;

	/**
	 * Constructor of the class.
	 * @param owner the owner frame
	 * @param title the title for the frame
	 * @param message the message to be shown in the bar
	 * @param icon the icon to show
	 * @throws HeadlessException when necessary
	 */
	public PhotManProgressPane(Frame owner, String title, String message, Icon icon) throws HeadlessException {
		super(owner,title,false);
		m_message = message;
		m_icon = icon;
		progressFrameInit();
		m_timer = new Timer(1000,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				repaint();
			}
		});
	}

	/**
	 * Constructor of the class.
	 * @param owner the owner dialog frame
	 * @param title the title for the frame
	 * @param message the message to be shown in the bar
	 * @param icon the icon to show
	 * @throws HeadlessException when necessary
	 */
	public PhotManProgressPane(JDialog owner, String title, String message, Icon icon) throws HeadlessException {
		super(owner,title,false);
		m_message = message;
		m_icon = icon;
		progressFrameInit();
		m_timer = new Timer(1000,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				repaint();
			}
		});
	}

	/**
	 * Constructor of the class.
	 * @param owner the owner window
	 * @param title the title for the frame
	 * @param message the message to be shown in the bar
	 * @param icon the icon to show
	 * @throws HeadlessException when necessary
	 */
	public PhotManProgressPane(Window owner, String title, String message, Icon icon) throws HeadlessException {
		super(owner,title);
		m_message = message;
		m_icon = icon;
		progressFrameInit();
		m_timer = new Timer(1000,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				repaint();
			}
		});
	}

	/**
	 * Creates the frame and the necessary components.
	 */
	private void progressFrameInit() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);   
		setLayout(new GridBagLayout());
		m_progress = new PhotManProgressBar();
		m_progress.setValue(0);
		m_progress.setString("");
		m_progress.setStringPainted(true);
		getContentPane().add(panel, 
				new GridBagConstraints(0,0,1,1,1.0,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		getContentPane().add(m_progress, 
				new GridBagConstraints(0,1,1,1,1.0,1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,1,0,1),0,0));

		JLabel iconLabel = new JLabel(m_icon);

		JLabel title = new JLabel(m_message);
		title.setFont(new Font(title.getFont().getName(), Font.BOLD, title.getFont().getSize()));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(title, 
				new GridBagConstraints(0,0,1,1,1.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		panel.add(iconLabel, 
				new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));

		setSize(350,70);
		setLocation(getOwner().getLocationOnScreen().x+(getOwner().getWidth() - getWidth())/2,
				getOwner().getLocationOnScreen().y+(getOwner().getHeight() - getHeight())/2);
	}

	/**
	 * Returns the progress bar object
	 * @return the progress bar object
	 */
	protected PhotManProgressBar getProgress() {
		return m_progress;
	}

	/**
	 * Goes one step further and shows it.
	 */
	protected void step() {
		m_step++;
		m_progress.setValue(m_step);
	}

	/**
	 * Starts the display of the progress bar.
	 */
	protected void start() {
		m_step = 0;
		m_progress.setValue(m_step);
		m_timer.start();
		setVisible(true);
	}

	/**
	 * Stops the display of the progress bar.
	 */
	protected void stop() {
		setVisible(false);
		m_timer.stop();
	}
}
