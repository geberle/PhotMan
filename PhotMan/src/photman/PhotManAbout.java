package photman;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <p>
 * This class shows the versions and copyright information.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * <pre>
 * Change history:
 *   2014-02-10 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManAbout extends JDialog {
	private static final long serialVersionUID = 2684457001250898635L;
	private JPanel m_contentPane;
	private Image m_applIcon;

	/**
	 * Class constructor.
	 */
	public PhotManAbout() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			photManAboutInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes all the window components.
	 */
	private void photManAboutInit() {
		m_contentPane = (JPanel) getContentPane();
		m_contentPane.setLayout(new BorderLayout());
		setTitle("About");
		m_applIcon = getToolkit().createImage(getClass().getResource("photman.png"));
		setIconImage(m_applIcon);
		setModal(true);
		setSize(new Dimension(320,160));
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
	 * Creates the content pane components.
	 */
	private void createMainPane() {
		JLabel iLabel = new JLabel(new ImageIcon(m_applIcon));
		m_contentPane.add(iLabel, BorderLayout.WEST);
		JLabel vLabel = new JLabel();
		String txt = "<html><b>Photographies Manager</b><br>";
		txt += "<br>Copyrigth Gérald Eberle<br>";
		txt += "<br>Version 1.0.0</htm>";
		vLabel.setText(txt);
		JPanel centerPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		centerPane.add(vLabel);
		centerPane.setBackground(Color.WHITE);
		centerPane.setOpaque(true);
		m_contentPane.add(centerPane,BorderLayout.CENTER);
		JButton but = new JButton();
		but.setText("Ok");
		final Window thisWindow = this;
		but.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				processWindowEvent(new WindowEvent(thisWindow,WindowEvent.WINDOW_CLOSING));
			}});
		JPanel southPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPane.add(but);
		southPane.setBackground(Color.WHITE);
		southPane.setOpaque(true);
		m_contentPane.add(southPane,BorderLayout.SOUTH);		
	}
}
