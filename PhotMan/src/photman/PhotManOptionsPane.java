package photman;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.text.NumberFormatter;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

/**
 * <p>
 * This class is used to ask the user about the options values. The options managed by this class are:
 * <pre>
 *  - the default pictures name start (default is IMG)
 *  - if available, show picture's registered thumbnail (default is Yes)
 *  - the scaling method to generate the thumbnail (default is SPEED)
 *  - the size of the thumbnail (default is 96 pixels)
 * </pre>
 * </p>
 * <pre>
 * Change history:
 *   2014-08-07 GEB  Initial coding.
 *   2014-09-07 GEB  Added the show picture's registered thumbnail option.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManOptionsPane extends JDialog {
	private static final long serialVersionUID = -4906873883767726610L;
	private JPanel m_contentPane;

	private String m_defaultName;
	private boolean m_originalThumbnail;
	private Method m_generateMethod;
	private int m_thumbnailSize;
	
	private JTextField m_nameText;
	private JList<Method> m_methodList;
	private JFormattedTextField m_sizeText;
	private JRadioButton m_yesButton;
	private JRadioButton m_noButton;
	
	private final int m_minThumbnailSize = 20;
	private final int m_maxThumbnailSize = 512;
	
	/**
	 * Class constructor.
	 * @param dName the actual default pictures name start
	 * @param gMethod the actual scaling method to generate the thumbnail
	 * @param tSize the actual size of the thumbnail
	 */
	public PhotManOptionsPane(String dName, boolean oThumbnail, Method gMethod, int tSize) {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			m_defaultName = dName;
			m_originalThumbnail = oThumbnail;
			m_generateMethod = gMethod;
			m_thumbnailSize = tSize;
			photManOptionsInit();
		}
		catch(Exception e) {
			e.printStackTrace();
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
	 * The size must be between 20 and 512 pixels.
	 * @return the m_thumbnailSize
	 */
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
		setSize(new Dimension(400,240));
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
	 * Creates the main content pane components.
	 */
	private void createMainPane() {
		createCenterPane();
		createSouthPane();		
	}

	/**
	 * Creates the center pane that shows the different options values.
	 */
	private void createCenterPane() {
		JLabel nameLabel = new JLabel("Pictures default name start with");
		m_nameText = new JTextField(m_defaultName, 12);
		
		JLabel originalLabel = new JLabel("Use picture registered thumbnail");
		m_yesButton = new JRadioButton("Yes", m_originalThumbnail);
		m_noButton = new JRadioButton("No", !m_originalThumbnail);
		ButtonGroup bg = new ButtonGroup();
		bg.add(m_yesButton);
		bg.add(m_noButton);
		JPanel originalPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		originalPane.add(m_yesButton);
		originalPane.add(m_noButton);
		
		JLabel methodLabel = new JLabel("Scaling method to generate thumbnails");
		m_methodList = new JList<Method>(Scalr.Method.values());
		m_methodList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		m_methodList.setSelectedValue(m_generateMethod, true);
		m_methodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JLabel sizeLabel = new JLabel("Thumbnails size (in pixels)");
		NumberFormat format = NumberFormat.getInstance();
	    NumberFormatter formatter = new NumberFormatter(format);
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(m_minThumbnailSize);
	    formatter.setMaximum(m_maxThumbnailSize);
	    formatter.setCommitsOnValidEdit(true);
	    m_sizeText = new JFormattedTextField(formatter);
	    m_sizeText.setValue(new Integer(m_thumbnailSize));
	    
		JPanel centerPane = new JPanel();
		centerPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(1,5,1,5);
		gbc.weightx = 0.0;
	    centerPane.add(nameLabel,gbc);
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1.0;
	    centerPane.add(m_nameText,gbc);
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
	    centerPane.add(originalLabel,gbc);
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1.0;
	    centerPane.add(originalPane,gbc);
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
	    centerPane.add(methodLabel,gbc);
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1.0;
	    centerPane.add(m_methodList,gbc);
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
	    centerPane.add(sizeLabel,gbc);
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1.0;
	    centerPane.add(m_sizeText,gbc);
		
		m_contentPane.add(centerPane,BorderLayout.CENTER);
	}

	/**
	 * Creates the pane at the bottom of the window, that shows the buttons used to accept 
	 * the shown values or to cancel the operation. 
	 */
	private void createSouthPane() {
		JButton okButton = new JButton();
		okButton.setText("Ok");
		final Window thisWindow = this;
		okButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (checkOptions()) {
					m_defaultName = m_nameText.getText();
					m_originalThumbnail = m_yesButton.isSelected();
					m_generateMethod = m_methodList.getSelectedValue();
					m_thumbnailSize = (Integer) m_sizeText.getValue();
					processWindowEvent(new WindowEvent(thisWindow,WindowEvent.WINDOW_CLOSING));
				}
			}});
		JButton clButton = new JButton();
		clButton.setText("Cancel");
		clButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				m_defaultName = null;
				m_originalThumbnail = true;
				m_generateMethod = null;
				m_thumbnailSize = -1;
				processWindowEvent(new WindowEvent(thisWindow,WindowEvent.WINDOW_CLOSING));
			}});
		FlowLayout fl = new FlowLayout(FlowLayout.CENTER);
		fl.setHgap(20);
		JPanel southPane = new JPanel(fl);
		southPane.add(okButton);
		southPane.add(clButton);
		m_contentPane.add(southPane,BorderLayout.SOUTH);
	}
	
	/**
	 * Checks the validity of the options values.
	 * @return true if all options have valid values, false otherwise
	 */
	private boolean checkOptions() {
		String dName = m_nameText.getText();
		if ("".equals(dName)) {
			showError("The default picture start name cannot be empty !");
			return false;
		}
		Method gMethod = m_methodList.getSelectedValue();
		if (gMethod == null) {
			showError("You must select a scaling method to generate the thumbnails !");
			return false;		
		}
		int tSize = (Integer) m_sizeText.getValue();
		if ((tSize < m_minThumbnailSize) || (tSize > m_maxThumbnailSize)) {
			showError("The thumbnail size must be between "+m_minThumbnailSize+ " and "+m_maxThumbnailSize+ " !");
			return false;
		}
		return true;
	}
	
	/**
	 * Shows an error message on the screen.
	 * @param txt the error text to be displayed
	 */
	private void showError(String txt) {
		JOptionPane.showMessageDialog(this,txt,"PhotMan - Error",JOptionPane.ERROR_MESSAGE);
	}
}
