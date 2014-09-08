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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.imgscalr.Scalr;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;

/**
 * <p>
 * This class manages the user to program interactions.
 * </p>
 * <pre>
 * Change history:
 *   2014-02-08 GEB  Initial coding.
 *   2014-08-05 GEB  Added the options management and a clear all function.
 *   2014-09-07 GEB  Added the show picture's registered thumbnail option and the computation of the
 *                   time offsets by selecting pictures.
 * </pre>
 * @author Gérald Eberle (GEB)
 */
public class PhotManFrame extends JFrame {
	private static final long serialVersionUID = 7704302123437787768L;
	private JPanel m_contentPane;
	private JLabel m_source;
	private JLabel m_destination;
	private JLabel m_information;
	private PhotManProgressPane m_progress;
	
	private JButton m_dstButton;
	private JButton m_offButton;
	private JButton m_ordButton;
	private JButton m_renButton;
	private JButton m_savButton;
	private JMenuItem m_dstMenu;
	private JMenuItem m_offMenu;
	private JMenuItem m_ordMenu;
	private JMenuItem m_renMenu;
	private JMenuItem m_savMenu;
	
	private ArrayList<File> m_sourceDir = new ArrayList<File>();
	private File m_destinationDir = null;
	private JList<PhotManImage> m_thumbnails;
	private HashMap<String,String> m_cameras;
	private boolean m_notSaved = true;
	private String m_prefix = "";
	private PhotManOptions m_options;
	
	private final String m_sourceTitle = "Source directory: ";
	private final String m_destinationTitle = "Destination directory: ";
	private final String m_timeInit = "00:00:00";
	private final DecimalFormat m_dec2 = new DecimalFormat("00");

	/**
	 * Class constructor.
	 */
	public PhotManFrame() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			photManInit();
		}
		catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"An error occurred during program initialization."
					+ "\nPlease contact the author(s)."
					+ "\nError is "+e.getMessage()+".",
					"PhotMan - Error",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	/**
	 * Initializes all the window components.
	 */
	private void photManInit() {
		m_contentPane = (JPanel) getContentPane();
		m_contentPane.setLayout(new BorderLayout());
		setTitle("PhotMan - Photographies Manager");
		Image applIcon = getToolkit().createImage(getClass().getResource("photman.png"));
		setIconImage(applIcon);
		setSize(new Dimension(1024,768));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width) frameSize.width = screenSize.width;
		int x0 = (screenSize.width - frameSize.width) / 2;
		int y0 = (screenSize.height - frameSize.height) / 2;
		setLocation(x0,y0);
		m_options = new PhotManOptions();
		createMenu();
		setNorthPane();
		setCenterPane();
		setSouthPane();
		setButtonsEnabled();
	}
	
	/**
	 * Creates a menu bar.
	 */
	private void createMenu() {
		JMenuBar mainMenuBar = new JMenuBar();
		setJMenuBar(mainMenuBar);
		JMenu fMenu = createMenu("File",KeyEvent.VK_F);
		mainMenuBar.add(fMenu);
		JMenu aMenu = createMenu("Action",KeyEvent.VK_A);
		mainMenuBar.add(aMenu);
		JMenu hMenu = createMenu("Help",KeyEvent.VK_H);
		mainMenuBar.add(hMenu);
		JMenuItem exiMenu = createMenuItem("Exit","exitWindow",KeyEvent.VK_E);
		fMenu.add(exiMenu);
		JMenuItem srcMenu = createMenuItem("Set Pictures Source...","setSource",KeyEvent.VK_S);
		aMenu.add(srcMenu);
		m_dstMenu = createMenuItem("Set Pictures Destination...","setDestination",KeyEvent.VK_D);
		aMenu.add(m_dstMenu);
		m_offMenu = createMenuItem("Set Time Offsets...","timeOffsets",KeyEvent.VK_T);
		aMenu.add(m_offMenu);
		m_ordMenu = createMenuItem("Reorder Pictures","orderFiles",KeyEvent.VK_O);
		aMenu.add(m_ordMenu);
		m_renMenu = createMenuItem("Rename Pictures...","renameFiles",KeyEvent.VK_R);
		aMenu.add(m_renMenu);
		m_savMenu = createMenuItem("Save Pictures","saveFiles",KeyEvent.VK_A);
		aMenu.add(m_savMenu);
		JMenuItem clsMenu = createMenuItem("Clear All","clearAll",KeyEvent.VK_C);
		hMenu.add(clsMenu);
		JMenuItem optMenu = createMenuItem("Set Options...","setOptions",KeyEvent.VK_O);
		hMenu.add(optMenu);
		JMenuItem imgMenu = createMenuItem("About PhotMan","aboutProgram",KeyEvent.VK_A);
		hMenu.add(imgMenu);
	}

	/**
	 * Creates the top pane that shows the buttons and the settings.
	 */
	private void setNorthPane() {
		JButton setsButton = setButton("Set Source...","defines the source directory of the pictures","setSource");
		m_dstButton = setButton("Set Destination...","defines the destination directory of the pictures","setDestination");
		m_offButton = setButton("Set Time Offsets...","set time offsets between different cameras","timeOffsets");
		m_ordButton = setButton("Reorder Pictures","reorders the pictures in the list by taken date","orderFiles");
		m_renButton = setButton("Rename Pictures...","renames the pictures in the list","renameFiles");
		m_savButton = setButton("Save","saves the pictures in the destination directory","saveFiles");
		JButton exitButton = setButton("Exit","exits from program","exitWindow");

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPane.add(setsButton);
		buttonPane.add(m_dstButton);
		buttonPane.add(m_offButton);
		buttonPane.add(m_ordButton);
		buttonPane.add(m_renButton);
		buttonPane.add(m_savButton);
		buttonPane.add(exitButton);
		buttonPane.setBorder(BorderFactory.createRaisedBevelBorder());
		m_source = new JLabel(m_sourceTitle);
		m_source.setBorder(BorderFactory.createEmptyBorder(2,5,0,0));
		m_destination = new JLabel(m_destinationTitle);
		m_destination.setBorder(BorderFactory.createEmptyBorder(2,5,2,0));
		JPanel dirPane = new JPanel(new GridLayout(2,1));
		dirPane.add(m_source);
		dirPane.add(m_destination);
		dirPane.setBorder(BorderFactory.createLoweredBevelBorder());
		JPanel northPane = new JPanel(new GridLayout(0,1));
		northPane.add(buttonPane);
		northPane.add(dirPane);
		m_contentPane.add(northPane,BorderLayout.NORTH);    
	}
	
	/**
	 * Creates a new menu object.
	 * @param txt the menu text
	 * @param key the menu mnemonic character
	 * @return the menu object
	 */
	private JMenu createMenu(String txt, int key) {
		JMenu m = new JMenu(txt);
		m.setMnemonic(key);
		return m;
	}

	/**
	 * Creates a new menu item.
	 * @param txt the text of the menu item
	 * @param cmd the action command for the menu item
	 * @param key the mnemonic character for the menu item
	 * @return the menu item
	 */
	private JMenuItem createMenuItem(String txt, String cmd, int key) {
		JMenuItem mi = new JMenuItem(txt,key);
		mi.setActionCommand(cmd);
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				generalActionPerformed(ae);
			}
		}); 
		return mi;
	}

	/**
	 * Creates a new button object. 
	 * @param txt the button text
	 * @param tt the button tool-tip
	 * @param cmd the button command string
	 * @return the new button object
	 */
	private JButton setButton(String txt, String tt, String cmd) {
		JButton but = new JButton();
		but.setText(txt);
		but.setToolTipText(tt);
		but.setActionCommand(cmd);
		but.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				generalActionPerformed(ae);
			}});
		return but;
	}
	
	/**
	 * Creates the center pane that shows the pictures thumbnail list.
	 */
	private void setCenterPane() {
		DefaultListModel<PhotManImage> dlm = new DefaultListModel<>();
		m_thumbnails = new JList<>(dlm);
		m_thumbnails.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		m_thumbnails.setTransferHandler(new PhotManListItemTransferHandler());
		m_thumbnails.setDropMode(DropMode.INSERT);
		m_thumbnails.setDragEnabled(true);
		m_thumbnails.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		m_thumbnails.setVisibleRowCount(0);
		m_thumbnails.setFixedCellWidth(m_options.getThumbnailSize()+10);
		m_thumbnails.setFixedCellHeight(m_options.getThumbnailSize()+30);
		m_thumbnails.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		m_thumbnails.setCellRenderer(new PhotManListCellRenderer());
		JScrollPane imagesScroll = new JScrollPane(m_thumbnails);
		m_contentPane.add(imagesScroll,BorderLayout.CENTER); 		
	}

	/**
	 * Creates the components at the bottom of the screen, that show the information messages.
	 */
	private void setSouthPane() {
		m_information = new JLabel(" ");
		m_information.setBorder(BorderFactory.createEmptyBorder(2,5,2,0));
		JPanel southPane = new JPanel(new GridLayout(0,2));
		southPane.add(m_information);
		m_contentPane.add(southPane,BorderLayout.SOUTH);    
	}
	
	/**
	 * Calls the methods triggered by a given action event.
	 * @param ae the reference to the action event
	 */
	private void generalActionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		if ("setSource".equals(command)) {
			setSourceDirectory();
		}
		else if ("setDestination".equals(command)) {
			if (m_sourceDir.isEmpty()) return;
			setDestinationDirectory();
			setButtonsEnabled();
		}
		else if ("timeOffsets".equals(command)) {
			if ((m_thumbnails == null) || (m_thumbnails.getModel().getSize() == 0)) return;
			getCamerasOffsets();
			setTimeOffsets();
		}
		else if ("orderFiles".equals(command)) {
			if ((m_thumbnails == null) || (m_thumbnails.getModel().getSize() == 0)) return;
			reorderFilesList();
		}
		else if ("renameFiles".equals(command)) {
			if ((m_thumbnails == null) || (m_thumbnails.getModel().getSize() == 0)) return;
			renameFilesList();
		}
		else if ("saveFiles".equals(command)) {
			if ((m_thumbnails == null) || (m_thumbnails.getModel().getSize() == 0)) return;
			if (checkDestinationFiles()) copyFiles();
		}
		else if ("clearAll".equals(command)) {
			clearAll();
		}
		else if ("setOptions".equals(command)) {
			m_options.setOptions();
			if (!m_options.isOriginalThumbnail()) {
				m_thumbnails.setFixedCellWidth(m_options.getThumbnailSize()+10);
				m_thumbnails.setFixedCellHeight(m_options.getThumbnailSize()+30);
			}
		}
		else if ("aboutProgram".equals(command)) {
			new PhotManAbout();
		}
		else if ("exitWindow".equals(command)) {
			processWindowEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
		}    
	}
	
	/**
	 * Enables or disables the actions buttons in function of the program status.
	 */
	private void setButtonsEnabled() {
		boolean dst = (!m_sourceDir.isEmpty());
		m_dstButton.setEnabled(dst);
		m_dstMenu.setEnabled(dst);
		boolean off = (m_cameras != null) && (m_cameras.size() > 0);
		m_offButton.setEnabled(off);
		m_offMenu.setEnabled(off);
		m_ordButton.setEnabled(off);
		m_ordMenu.setEnabled(off);
		boolean ren = (m_thumbnails != null) && (m_thumbnails.getModel().getSize() > 0);
		m_renButton.setEnabled(ren);
		m_renMenu.setEnabled(ren);
		boolean sav = (m_destinationDir != null);
		m_savButton.setEnabled(sav);
		m_savMenu.setEnabled(sav);
	}
	
	/**
	 * Calls a file chooser so that the user can select a directory as the images source directory.
	 */
	private void setSourceDirectory() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);;
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File sourceDir = fc.getSelectedFile();
			if (sourceDir != null) {
				if (sourceDir.exists()) {
					File[] fList = sourceDir.listFiles(new PhotManFilenameFilter(sourceDir.getAbsolutePath()));
					if (fList.length == 0) {
						showError("The selected source directory does not contain any allowed image file !");
						return;
					}
					if (m_sourceDir.isEmpty()) {
						m_source.setText(m_sourceTitle+sourceDir.getAbsolutePath());
					}
					else {
						m_source.setText(m_source.getText()+", "+sourceDir.getAbsolutePath());
					}
					addSourceFiles(sourceDir);
					m_sourceDir.add(sourceDir);
				}
				else {
					showError("The selected source directory does not exist !");
				}
			}
		}
	}
	
	/**
	 * Calls a file chooser so that the user can select a destination directory where he/she can manipulate
	 * the images. The destination directory can be the same as the source directory. If the destination
	 * directory already contains images, they can be kept and merged with the images of the source
	 * directory.
	 */
	private void setDestinationDirectory() {
		JFileChooser fc = new JFileChooser(m_destinationDir);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);;
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			m_destinationDir = fc.getSelectedFile();
			if (m_destinationDir.exists()) {
				if (differentDirs()) {
					File[] fList = m_destinationDir.listFiles(new PhotManFilenameFilter(m_destinationDir.getAbsolutePath()));
					if ((fList.length > 0) && (askDeleteFiles())) {
						for (File f : fList) f.delete();
					}
				}
			}
			else {
				m_destinationDir.mkdir();
			}
			m_destination.setText(m_destinationTitle+m_destinationDir.getAbsolutePath());
		}
	}
	
	/**
	 * Checks if all source directories are different from the destination directory.
	 * @return true if all the source directories are different form the destination directory, false otherwise
	 */
	private boolean differentDirs() {
		String dest = m_destinationDir.getAbsolutePath();
		for (File f : m_sourceDir)
			if (f.getAbsolutePath().equalsIgnoreCase(dest)) return false;
		return true;
	}
	
	/**
	 * Adds a new list of images to the internal list and shows them on the screen.
	 */
	private void addSourceFiles(File sourceDir) {
		if (m_progress == null) m_progress = new PhotManProgressPane(this,"PhotMan","Loading pictures...",null);
		final File[] images = sourceDir.listFiles(new PhotManFilenameFilter(sourceDir.getAbsolutePath()));
		int max = images.length;
		final DefaultListModel<PhotManImage> dlm = (DefaultListModel<PhotManImage>) m_thumbnails.getModel();
		barInit(max);
		m_progress.start();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		SwingWorker<Void,Void> work = new SwingWorker<Void, Void>() {			
			@Override
			protected Void doInBackground() throws Exception {
				for (File f : images) {
					PhotManImage pmi = new PhotManImage();
					pmi.setOriginalFile(f);
					pmi.setFinalName(f.getName());
					ExifDirectory ed = createExifMap(f);
					pmi.setExif(ed);
					if (m_options.isOriginalThumbnail()) pmi.setThumbnail(extractThumbnail(ed,f));
					else pmi.setThumbnail(createThumbnail(ed,f));
					dlm.addElement(pmi);
					barStep();
				}
				return null;
			}

			@Override
			protected void done() {
				barStop();
				analyseImages();		
				setButtonsEnabled();
				showInformation("There are "+dlm.getSize()+" pictures in the list.");
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		};
		work.execute();
	}
	
	/**
	 * Checks if the destination does already contain one or several files with the same name as the 
	 * ones to be copied. If this is the case, ask the user he/she wants to replace the existing file(s).
	 * @return true if the files can be copied, false otherwise
	 */
	private boolean checkDestinationFiles() {
		DefaultListModel<PhotManImage> dlm = (DefaultListModel<PhotManImage>) m_thumbnails.getModel();
		File dstFile;
		boolean fileExists = false;
		boolean diffDir;
		for (int i1 = 0; i1 < dlm.getSize(); i1++) {
			PhotManImage pmi = dlm.get(i1);
			dstFile = new File(m_destinationDir.getAbsolutePath(),pmi.getFinalName());
			diffDir = !pmi.getOriginalFile().getParent().equalsIgnoreCase(m_destinationDir.getAbsolutePath());
			fileExists = fileExists || (dstFile.exists() && diffDir);
		}
		if (fileExists) return askReplaceFiles();
		return true;
	}

	/**
	 * If the destination directory is not the same as the source directory, copies the files containing
	 * the images from the source to the destination directory. If two files have the same name, the 
	 * destination file is overwritten by the source file.
	 */
	private void copyFiles() {
		try {
			boolean sameDir;
			ArrayList<PhotManImage> sameDirImages = new ArrayList<PhotManImage>();
			DefaultListModel<PhotManImage> dlm = (DefaultListModel<PhotManImage>) m_thumbnails.getModel();
			for (int i1 = 0; i1 < dlm.getSize(); i1++) {
				PhotManImage pmi = dlm.get(i1);
				sameDir = pmi.getOriginalFile().getParent().equalsIgnoreCase(m_destinationDir.getAbsolutePath());
				if (sameDir) sameDirImages.add(pmi);
				else {
					Path srcPath = pmi.getOriginalFile().toPath();
					Path destPath = FileSystems.getDefault().getPath(m_destinationDir.getAbsolutePath(),pmi.getFinalName());
					Files.copy(srcPath,destPath,StandardCopyOption.REPLACE_EXISTING);
				}
			}
			if (sameDirImages.size() > 0) renameFiles(sameDirImages);
			showInformation("The pictures files have been saved.");
			m_notSaved = false;
		} 
		catch (IOException e) {
			e.printStackTrace();
			showError("An I/O error occurred during file copying.\nError is "+e.getMessage()+".");
		}
	}
	
	/**
	 * If the destination directory is the same as the source directory, renames the files in the given
	 * list. If the old and the new files have common names, first rename the old files to temporary names,
	 * then rename the temporary names to the new names.
	 * @param iList the list of the files to be renamed
	 */
	private void renameFiles(ArrayList<PhotManImage> iList) {
		ArrayList<String> oldNames = new ArrayList<String>();
		ArrayList<String> newNames = new ArrayList<String>();
		for (PhotManImage pmi : iList) {
			oldNames.add(pmi.getOriginalFile().getName());
			newNames.add(pmi.getFinalName());
		}
		boolean sameName = false;
		for (String name : newNames)
			if (oldNames.contains(name)) sameName = true;
		if (sameName) {
			for (PhotManImage pmi : iList) {
				File f = pmi.getOriginalFile();
				File nf = new File(f.getParent(),f.getName()+".tmp");
				f.renameTo(nf);
			}
			for (PhotManImage pmi : iList) {
				File f = pmi.getOriginalFile();
				File of = new File(f.getParent(),f.getName()+".tmp");
				File nf = new File(f.getParent(),pmi.getFinalName());
				of.renameTo(nf);
			}
		}
		else {
			for (PhotManImage pmi : iList) {
				File f = pmi.getOriginalFile();
				File nf = new File(f.getParent(),pmi.getFinalName());
				f.renameTo(nf);
			}
		}
	}
	
	/**
	 * Extracts from the EXIF meta-data the thumbnail registered with the picture.
	 * If this thumbnail does not exists, creates one from the image data.
	 * @param ed the EXIF meta-data
	 * @param f the file containing the image
	 * @return the thumbnail image or null if the thumbnail image could not be created
	 */
	private ImageIcon extractThumbnail(ExifDirectory ed, File f) {
		try {
			if (ed.containsThumbnail()) {
				ImageIcon thumbnail = new ImageIcon(ed.getThumbnailData());
				int actWidth = m_thumbnails.getFixedCellWidth();
				int shdWidth = thumbnail.getIconWidth() + 10;
				if (actWidth < shdWidth) m_thumbnails.setFixedCellWidth(shdWidth);
				int actHeight = m_thumbnails.getFixedCellHeight();
				int shdHeight = thumbnail.getIconHeight() + 30;
				if (actHeight < shdHeight) m_thumbnails.setFixedCellHeight(shdHeight);
				return thumbnail;
			}
		}
		catch (MetadataException me) {
			// Nothing to do here
		}
		return createThumbnail(ed,f);
	}
	
	/**
	 * Creates a small image (thumbnail) with fixed size from a given real image.
	 * @param ed the EXIF meta-data
	 * @param f the file containing the image to be resized
	 * @return the thumbnail image or null if the thumbnail image could not be created
	 */
	private ImageIcon createThumbnail(ExifDirectory ed, File f) {
		try {
			BufferedImage bi = ImageIO.read(f);
			bi = Scalr.resize(bi,m_options.getGenerateMethod(),m_options.getThumbnailSize(),
					Scalr.OP_ANTIALIAS,Scalr.OP_BRIGHTER);
			int orientation = ed.getInt(ExifDirectory.TAG_ORIENTATION);
			switch (orientation) {
			case 3: bi = Scalr.rotate(bi,Scalr.Rotation.CW_180); break;
			case 6: bi = Scalr.rotate(bi,Scalr.Rotation.CW_90); break;
			case 8: bi = Scalr.rotate(bi,Scalr.Rotation.CW_270); break;
			default:
			}			
			return new ImageIcon(bi);
		} 
		catch (IOException e) {
			e.printStackTrace();
			showError("An I/O error occurred during thumbnail generation.\nError is "+e.getMessage()+".");
		} catch (MetadataException e) {
			e.printStackTrace();
			showError("An I/O error occurred during thumbnail generation.\nError is "+e.getMessage()+".");
		}
		return null;
	}
	
	/**
	 * Extracts from the image file the EXIF meta-data.
	 * @param f the file containing the image
	 * @return the EXIF meta-data or null if no meta-data were found
	 */
	private ExifDirectory createExifMap(File f) {		
		try {
			Metadata md = JpegMetadataReader.readMetadata(f);
			ExifDirectory edir = (ExifDirectory) md.getDirectory(ExifDirectory.class);
			if (edir.getTagCount() > 0) return edir;
		} 
		catch (JpegProcessingException e) {
			e.printStackTrace();
			showError("An error occurred during EXIF reading.\nError is "+e.getMessage()+".");
		}
		return null;
	}
	
	/**
	 * Creates a list of all the cameras that were used to take all the pictures in the internal list.
	 */
	private void analyseImages() {
		if (m_cameras == null) m_cameras = new HashMap<>();
		DefaultListModel<PhotManImage> dlm = (DefaultListModel<PhotManImage>) m_thumbnails.getModel();
		for (int i1 = 0; i1 < dlm.getSize(); i1++) {
			PhotManImage pmi = dlm.get(i1);
			ExifDirectory ed = pmi.getExif();
			if (ed == null) continue;
			String cameraModel = getCameraModel(ed);
			if (cameraModel != null) cameraModel = cameraModel.trim();
			Date imageDate = getImageCreationDate(ed);
			pmi.setCameraModel(cameraModel);
			pmi.setCreationDate(imageDate);
			if ((cameraModel != null) && (imageDate != null))
				if (!m_cameras.containsKey(cameraModel)) m_cameras.put(cameraModel,m_timeInit);
		}
	}
	
	/**
	 * Extracts from the given EXIF meta-data the camera make and model names.
	 * @param ed the EXIF meta-data
	 * @return the camera make and/or model or null if none were found
	 */
	private String getCameraModel(ExifDirectory ed) {
		String cameraMake = null;
		if (ed.containsTag(ExifDirectory.TAG_MAKE)) cameraMake = ed.getString(ExifDirectory.TAG_MAKE);
		String cameraModel = null; 
		if (ed.containsTag(ExifDirectory.TAG_MODEL)) cameraModel = ed.getString(ExifDirectory.TAG_MODEL);
		if (cameraMake == null)
			if (cameraModel == null) return null;
			else return cameraModel;
		else
			if (cameraModel == null) return cameraMake;
			else return cameraMake + " " + cameraModel;
	}
	
	/**
	 * Extracts from the given EXIF meta-data the date and time the picture was taken.
	 * @param ed the EXIF meta-data
	 * @return the date and time the picture was taken
	 */
	private Date getImageCreationDate(ExifDirectory ed) {
		try {
			if (ed.containsTag(ExifDirectory.TAG_DATETIME_ORIGINAL)) {
				Date imageDate = ed.getDate(ExifDirectory.TAG_DATETIME_ORIGINAL);
				return imageDate;
			}
		} catch (MetadataException e) {
			e.printStackTrace();
			showError("An error occurred during EXIF date reading.\nError is "+e.getMessage()+".");
		}
		return null;
	}
	
	/**
	 * Calls the methods that will allow the user to put time offsets between the different cameras
	 * that took pictures found in the destination directory.
	 */
	private void getCamerasOffsets() {
		if ((m_cameras == null) || (m_cameras.size() == 0)) return;
		List<PhotManImage> selected = m_thumbnails.getSelectedValuesList();
		if (selected.size() > 1) presetTimeOffsets(selected);
		PhotManCameras pmc = new PhotManCameras(m_cameras);
		HashMap<String,String> cameras = pmc.getCameras();
		if (cameras != null) m_cameras = cameras;
	}
	
	/**
	 * If the user selects two or more pictures taken at the same time by different cameras,
	 * this method extracts from the list of selected pictures the time difference between the cameras.
	 * @param pmis the list of selected pictures
	 */
	private void presetTimeOffsets(List<PhotManImage> pmis) {
		if (m_cameras.size() < 2) return;
		String baseModel = null;
		Date baseDate = null;
		HashMap<String,Date> modelDates = new HashMap<String,Date>();
		for (PhotManImage pmi : pmis) {
			if ((baseModel == null) && (baseDate == null)) {
				baseModel = pmi.getCameraModel();
				baseDate = pmi.getCreationDate();
			}
			else if (!baseModel.equals(pmi.getCameraModel())) modelDates.put(pmi.getCameraModel(),pmi.getCreationDate());
		}
		if ((baseModel == null) || (baseDate == null)) return;
		for (String model : modelDates.keySet()) {
			String offset = encodeOffset(baseDate,modelDates.get(model));
			m_cameras.put(model,offset);
		}
	}
	
	/**
	 * Computes the time difference between two dates and creates a string with format hh:mm:ss.
	 * @param bDate the base date
	 * @param eDate the compared date
	 * @return the resulting string
	 */
	private String encodeOffset(Date bDate, Date eDate) {
		String offset = "";
		if (bDate.before(eDate)) offset += "+";
		else offset += "-";
		long diff = bDate.getTime() - eDate.getTime();
		if (diff < 0) diff = -diff;
		diff = diff / 1000;
		double h = diff / 3600.0;
		int hours = (int) Math.floor(h);
		diff = diff - (hours * 3600);
		double m = diff / 60.0;
		int minutes = (int) Math.floor(m);
		diff = diff - (minutes * 60);
		offset += m_dec2.format(hours) + ":" + m_dec2.format(minutes) + ":" + m_dec2.format(diff);
		return offset;
	}
	
	/**
	 * Modifies the date and time the pictures were taken, with the time offsets given by the user.
	 */
	private void setTimeOffsets() {
		if ((m_cameras == null) || (m_cameras.size() == 0)) return;
		HashMap<String,Object[]> cams = new HashMap<String,Object[]>();
		for (String camera : m_cameras.keySet()) {
			String offset = m_cameras.get(camera);
			Object[] offs = decodeOffset(offset);
			cams.put(camera,offs);
		}
		DefaultListModel<PhotManImage> dlm = (DefaultListModel<PhotManImage>) m_thumbnails.getModel();
		for (int i1 = 0; i1 < dlm.getSize(); i1++) {
			PhotManImage pmi = dlm.get(i1);
			Date cDate = getImageCreationDate(pmi.getExif());
			if (cDate == null) continue;
			String cModel = pmi.getCameraModel();
			cDate = adjustDate(cDate,cams.get(cModel));
			pmi.setCreationDate(cDate);
		}		
		showInformation("The dates have been adjusted.");
	}
	
	/**
	 * Decodes the time offset given as a string in a real time offset (sign,hour,minute,second) object.
	 * @param off the time offset as a string
	 * @return the real time offset as an object
	 */
	private Object[] decodeOffset(String off) {
		Object[] offs = null;
		String str = off;
		String sign = str.substring(0,1);
		if ("+".equals(sign) || "-".equals(sign)) str = str.substring(1);
		else sign = "+";
		if (str.length() == 8) {
			Integer hours = Integer.parseInt(str.substring(0,2));
			Integer minutes = Integer.parseInt(str.substring(3,5));
			Integer seconds = Integer.parseInt(str.substring(7));
			if ((hours > 0) || (minutes > 0) || (seconds > 0)) {
				offs = new Object[4];
				offs[0] = sign;
				offs[1] = hours;
				offs[2] = minutes;
				offs[3] = seconds;
			}
		}
		return offs;
	}
	
	/**
	 * Adjusts a given date and time with a given time offset.
	 * @param dt the date and time to be adjusted
	 * @param offs the time offset
	 * @return the adjusted date and time
	 */
	private Date adjustDate(Date dt, Object[] offs) {
		if (offs == null) return dt;
		GregorianCalendar gc = new GregorianCalendar();
		gc.setLenient(true);
		gc.setTime(dt);
		String sign = (String) offs[0];
		int iSign = 1;
		if ("-".equals(sign)) iSign = -1;
		int hours = (Integer) offs[1];
		if (hours > 0) gc.set(Calendar.HOUR_OF_DAY,gc.get(Calendar.HOUR_OF_DAY)+iSign*hours);
		int minutes = (Integer) offs[1];
		if (minutes > 0) gc.set(Calendar.MINUTE,gc.get(Calendar.MINUTE)+iSign*minutes);
		int seconds = (Integer) offs[1];
		if (seconds > 0) gc.set(Calendar.SECOND,gc.get(Calendar.SECOND)+iSign*seconds);
		return new Date(gc.getTimeInMillis());
	}
	
	/**
	 * Reorders the files containing the images in increasing order the date and time the pictures
	 * were taken.
	 */
	private void reorderFilesList() {
		DefaultListModel<PhotManImage> dlm = (DefaultListModel<PhotManImage>) m_thumbnails.getModel();
		PhotManImage[] pmis = new PhotManImage[dlm.getSize()];
		for (int i1 = 0; i1 < dlm.getSize(); i1++) pmis[i1] = dlm.get(i1);
		Arrays.sort(pmis);
		for (int i1 = 0; i1 < pmis.length; i1++) dlm.setElementAt(pmis[i1],i1);
		showInformation("The pictures have been ordered by adjusted taken date.");
	}
	
	/**
	 * Renames the files in the order the pictures are shown on the screen. The new files names prefix
	 * is given by the user. 
	 */
	private void renameFilesList() {
		String prefix;
		if ("".equals(m_prefix)) prefix = m_options.getDefaultName();
		else prefix = m_prefix;
		PhotManNewFilename pmnf = new PhotManNewFilename(prefix);
		prefix = pmnf.getNewFilename();
		if ("".equals(prefix)) return;
		m_prefix = prefix;
		DecimalFormat df = new DecimalFormat("0000");
		DefaultListModel<PhotManImage> dlm = (DefaultListModel<PhotManImage>) m_thumbnails.getModel();
		for (int i1 = 0; i1 < dlm.getSize(); i1++) {
			PhotManImage pmi = dlm.get(i1);
			String name = prefix + "_" + df.format(i1) + ".jpg";
			pmi.setFinalName(name);
			dlm.set(i1,pmi);
		}
		showInformation("The pictures have been renamed.");
	}
	
	/**
	 * Resets the program to its initial state.
	 */
	private void clearAll() {
		m_sourceDir.clear();;
		m_destinationDir = null;
		((DefaultListModel<PhotManImage>) m_thumbnails.getModel()).clear();
		m_thumbnails.setFixedCellWidth(m_options.getThumbnailSize()+10);
		m_thumbnails.setFixedCellHeight(m_options.getThumbnailSize()+30);
		m_cameras.clear();
		m_notSaved = true;
		m_prefix = "";
		m_source.setText(m_sourceTitle);
		m_destination.setText(m_destinationTitle);
		showInformation("");
		setButtonsEnabled();
	}
	
	  /**
	   * Initializes the progress bar.
	   * @param length the maximal length of the bar
	   */
	  private void barInit(final int length) {
	    SwingUtilities.invokeLater(new Runnable() {
	      @Override
	      public void run() {
	        int len = length;
	        if (len == 0) len = 1;
	        m_progress.getProgress().setMaximum(len);          
	      }
	    });
	  }
	  
	  /**
	   * Advances the progress bar by one step unit.
	   */
	  private void barStep() {
	    SwingUtilities.invokeLater(new Runnable() {
	      @Override
	      public void run() {
	        m_progress.step();
	      }
	    });
	  }
	  
	  /**
	   * Removes the progress bar from the screen.
	   */
	  private void barStop() {
	    SwingUtilities.invokeLater(new Runnable() {
	      @Override
	      public void run() {
	        m_progress.stop();
	      }
	    });
	  }
	  
	/**
	 * If the destination directory already contains images files, and the destination directory
	 * is not the same as the source directory, asks the user if the destination files must be 
	 * deleted (i.e. not merged with the source files).
	 * @return true if the destination files must be deleted, false otherwise 
	 */
	private boolean askDeleteFiles() {
		return JOptionPane.showConfirmDialog(this,
				"This directory does already contain images files.\nDo you want to delete them ?",
				"PhotMan - Question",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
	
	/**
	 * If the destination directory already contains images files with the same name as those in
	 * the internal list, asks the user if the files in the destination directory must be replaced
	 * by those of the internal list.
	 * @return true if the destination files must be replaced, false otherwise 
	 */
	private boolean askReplaceFiles() {
		return JOptionPane.showConfirmDialog(this,
				"The destination directory does already contain files with the same name as files in the list."
				+ "\nDo you want to replace them by the files in the list ?",
				"PhotMan - Question",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
	
	/**
	 * If there is some work that were not saved, asks the user if he/she really wants to leave
	 * the program.
	 * @return true if the user want to exit the program, false otherwise
	 */
	private boolean askExit() {
		return JOptionPane.showConfirmDialog(this,
				"The files list have not been saved.\nDo you really want to exit ?",
				"PhotMan - Question",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
	
	/**
	 * Shows an error message on the screen.
	 * @param txt the error text to be displayed
	 */
	private void showError(String txt) {
		JOptionPane.showMessageDialog(this,txt,"PhotMan - Error",JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Shows an information message at the bottom of the screen, for 5 seconds.
	 * @param txt the information to be displayed
	 */
	private void showInformation(String txt) {
		m_information.setText(txt);
		Timer timer = new Timer(5000,new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				m_information.setText("");
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	/**
	 * Overridden so we can exit when window is closed. 
	 * @param we the window event to be trapped
	 */
	@Override
	protected void processWindowEvent(WindowEvent we) {
		if (we.getID() == WindowEvent.WINDOW_CLOSING) {
			if ((m_destinationDir != null) && m_notSaved) {
				if (askExit()) System.exit(0);
				else return;
			}
			else System.exit(0);
		}
		super.processWindowEvent(we);
	}
}
