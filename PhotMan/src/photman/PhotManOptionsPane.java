package photman;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.NumberFormatter;

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
 *   2014-08-07 GEB  Initial coding.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManOptionsPane extends JDialog {
	private static final long serialVersionUID = -4906873883767726610L;
	private JPanel m_contentPane;

	private String m_defaultName;
	private Method m_generateMethod;
	private int m_thumbnailSize;
	
	/**
	 * Class constructor.
	 * @param cameras the cameras-time offsets map to be managed
	 */
	public PhotManOptionsPane(String dName, Method gMethod, int tSize) {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			m_defaultName = dName;
			m_generateMethod = gMethod;
			m_thumbnailSize = tSize;
			photManOptionsInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the cameras-time offsets map, modified by the user.
	 * @return the cameras-time offsets map
	 */
	protected String getDefaultName() {
		return m_defaultName;
	}
	
	protected Method getGenerateMethod() {
		return m_generateMethod;
	}
	
	protected int getThumbnailSize() {
		return m_thumbnailSize;
	}

	/**
	 * Initializes all the window components.
	 */
	private void photManOptionsInit() {
		m_contentPane = (JPanel) getContentPane();
		m_contentPane.setLayout(new BorderLayout());
		Image applIcon = getToolkit().createImage(getClass().getResource("photman.png"));
		setIconImage(applIcon);
		setTitle("Manage Options");
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
		createCenterPane();
		createSouthPane();		
	}

	private void createCenterPane() {
		JLabel nameLabel = new JLabel("Pictures default name start with");
		JTextField nameText = new JTextField(m_defaultName, 12); 
		JLabel methodLabel = new JLabel("Method to generate thumbnails");
		JList<Method> methodList = new JList<Method>(Scalr.Method.values());
		JLabel sizeLabel = new JLabel("Thumbnails size (in pixels)");
		NumberFormat format = NumberFormat.getInstance();
	    NumberFormatter formatter = new NumberFormatter(format);
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(0);
	    formatter.setMaximum(512);
	    formatter.setCommitsOnValidEdit(true);
	    JFormattedTextField sizeText = new JFormattedTextField(formatter);
	    sizeText.setValue(new Integer(m_thumbnailSize));
	    
		JPanel centerPane = new JPanel();
		centerPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(1,5,1,5);
		gbc.weightx = 0.0;
	    centerPane.add(nameLabel,gbc);
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1.0;
	    centerPane.add(nameText,gbc);
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
	    centerPane.add(methodLabel,gbc);
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1.0;
	    centerPane.add(methodList,gbc);
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
	    centerPane.add(sizeLabel,gbc);
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1.0;
	    centerPane.add(sizeText,gbc);
		
		m_contentPane.add(centerPane,BorderLayout.CENTER);
	}

	private void createSouthPane() {
		JButton okButton = new JButton();
		okButton.setText("Ok");
		final Window thisWindow = this;
		okButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				processWindowEvent(new WindowEvent(thisWindow,WindowEvent.WINDOW_CLOSING));
			}});
		JButton clButton = new JButton();
		clButton.setText("Cancel");
		clButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
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
