/*
 * This class contains utility functions related to GUIs.
 *
 * @author Marty Stepp
 * @version 2014/05/26
 * - added centerWindowWithin method
 * @version 2014/05/22
 * - added methods for creating radio buttons, sliders, etc.
 * @version 2014/06/05
 * - original version
 * 
 * This file and its contents are copyright (C) Stanford University and Marty Stepp,
 * licensed under Creative Commons Attribution 2.5 License.  All rights reserved.
 */


// TO DO: This file's documentation is insufficient due to time constraints.
// Improve documentation in future quarters for better style and maintainability.

package stanford.cs106.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import acm.program.Program;
import stanford.cs106.io.*;

public class GuiUtils {
	public static final String SETTINGS_FILENAME = "autograder-window-settings.sav";
	private static Properties props = new Properties();
	private static String tempDir = System.getProperty("java.io.tmpdir");
	
	public static void addKeyListenerRecursive(Component component, KeyListener listener) {
		if (component.isFocusable() || component instanceof Window) {
			// javax.swing.JOptionPane.showMessageDialog(null, "attaching key listener to: " + component);
			component.addKeyListener(listener);
		}
		if (component instanceof Container) {
			Container container = (Container) component;
			for (Component subcomponent : container.getComponents()) {
				addKeyListenerRecursive(subcomponent, listener);
			}
		}
	}
	
	/**
	 * Sets up the given program so that its componentResized method will be
	 * called whenever the window resizes.
	 * The given program must implement the ResizeListener interface.
	 * @param program the program to listen to
	 * @throws ClassCastException if the given program does not implement the
	 *                            ResizeListener interface.
	 */
	public static void addResizeListener(Program program) {
		if (!(program instanceof ResizeListener)) {
			throw new ClassCastException("Your program class must implement the ResizeListener interface.");
		}
		final ResizeListener listener = (ResizeListener) program;
		program.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent event) {
				listener.componentResized(event);
			}
		});
	}
	
	public static void centerWindow(Window window) {
		if (window != null) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			window.setLocation(screenSize.width/2 - window.getWidth()/2,
					screenSize.height/2 - window.getHeight()/2);
		}
	}

	public static void centerWindowWithin(Window window, Window parent) {
		if (window != null && parent != null) {
			window.setLocation(
					parent.getX() + (parent.getWidth() - window.getWidth()) / 2,
					parent.getY() + (parent.getHeight() - window.getHeight()) / 2);
		}
	}

	public static JButton createButton(String text, char mnemonic, ActionListener listener) {
		return createButton(text, null, null, mnemonic, listener, /* container */ null);
	}
	
	public static JButton createButton(String text, char mnemonic, ActionListener listener, Container container) {
		return createButton(text, null, null, mnemonic, listener, container);
	}
	
	public static JButton createButton(String text, String actionCommand, char mnemonic, ActionListener listener) {
		return createButton(text, actionCommand, null, mnemonic, listener, /* container */ null);
	}
	
	public static JButton createButton(String text, String actionCommand, char mnemonic, ActionListener listener, Container container) {
		return createButton(text, actionCommand, null, mnemonic, listener, container);
	}
	
	public static JButton createButton(String text, String actionCommand, String icon, char mnemonic, ActionListener listener) {
		return createButton(text, actionCommand, icon, mnemonic, listener, /* container */ null);
	}
		
	public static JButton createButton(String text, String actionCommand, String icon, char mnemonic, ActionListener listener, Container container) {
		JButton button = new JButton(text);
		if (actionCommand == null || actionCommand.isEmpty()) {
			actionCommand = text;
		}
		button.setActionCommand(actionCommand);
		
		if (icon != null && icon.length() > 0) {
			try {
				if (ResourceUtils.fileExists(icon)) {
					button.setIcon(new ImageIcon(ResourceUtils.filenameToURL(icon)));
				}
			} catch (Exception e) {
				try {
					button.setIcon(new ImageIcon(ResourceUtils.filenameToURL(icon)));
				} catch (IORuntimeException ioe) {
					// empty
				}
			}
		}
		
		if (mnemonic != '\0' && mnemonic != ' ') {
			button.setMnemonic(mnemonic);
		}
		
		button.addActionListener(listener);
		
		if (container != null) {
			container.add(button);
		}
		
		return button;
	}
	
	public static JCheckBox createCheckBox(String actionCommand, ActionListener listener) {
		return createCheckBox(actionCommand, /* checked */ true, listener);
	}
	
	public static JCheckBox createCheckBox(String actionCommand, boolean checked, ActionListener listener) {
		char mnemonic = (actionCommand != null && !actionCommand.isEmpty() ? actionCommand.charAt(0) : '\0');
		return createCheckBox(actionCommand, mnemonic, checked, listener);
	}
	
	public static JCheckBox createCheckBox(String actionCommand, char mnemonic, boolean checked) {
		return createCheckBox(actionCommand, mnemonic, checked, /* listener */ null);
	}
	
	public static JCheckBox createCheckBox(String actionCommand, char mnemonic, boolean checked, ActionListener listener) {
		return createCheckBox(actionCommand, mnemonic, checked, listener, /* container */ null);
	}
	
	public static JCheckBox createCheckBox(String actionCommand, char mnemonic, ActionListener listener, Container container) {
		return createCheckBox(actionCommand, mnemonic, /* checked */ false, listener, container);
	}
	
	public static JCheckBox createCheckBox(String actionCommand, char mnemonic, boolean checked, ActionListener listener, Container container) {
		JCheckBox box = new JCheckBox(actionCommand);
		box.setSelected(checked);
		if (mnemonic != '\0' && mnemonic != ' ') {
			box.setMnemonic(mnemonic);
		}
		if (listener != null) {
			box.addActionListener(listener);
		}
		if (container != null) {
			container.add(box);
		}
		return box;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JComboBox createComboBox(String actionCommand, ActionListener listener, String... items) {
		JComboBox box = new JComboBox();
		box.setEditable(false);
		for (String item : items) {
			box.addItem(item);
		}
		if (listener != null) {
			box.addActionListener(listener);
			box.setActionCommand(actionCommand);
		}
		return box;
	}
	
	/*
	 * adds a JPanel to the layout
	 */
	public static JPanel createPanel(Component... components) {
		return createPanel(new FlowLayout(FlowLayout.CENTER), components);
	}
	
	/*
	 * adds a JPanel to the layout
	 */
	public static JPanel createPanel(LayoutManager layout, Component... components) {
		JPanel panel = new JPanel(layout);
		for (Component comp : components) {
			panel.add(comp);
		}
		return panel;
	}
	

	/* Helper method to create a JRadioButton with the given properties. */
	public static JRadioButton createRadioButton(String text, char mnemonic,
			boolean selected, ButtonGroup group, ActionListener listen,
			Container panel) {
		JRadioButton button = new JRadioButton(text, selected);
		if (mnemonic != '\0') {
			button.setMnemonic(mnemonic);
		}
		button.addActionListener(listen);
		if (panel != null) {
			panel.add(button);
		}
		if (group != null) {
			group.add(button);
		}
		return button;
	}
	
	/* Helper method to create a JSlider with the given properties. */
	public static JSlider createSlider(int min, int max, int initial,
			int majorTick, int minorTick, ChangeListener listen, Container panel) {
		JSlider slider = new JSlider(min, max, initial);
		slider.setMajorTickSpacing(majorTick);
		slider.setMinorTickSpacing(minorTick);
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		// slider.setPaintLabels(true);
		slider.addChangeListener(listen);
		if (panel != null) {
			panel.add(slider);
		}
		return slider;
	}
	
	public static FileFilter getExtensionFileFilter(String description, String... extensions) {
		return new ExtensionFileFilter(description, extensions);
	}
	
	public static JLabel createLabel(String text, int width) {
		return createLabel(text, width, /* rightAligned */ false);
	}
	
	public static JLabel createLabel(String text, int width, boolean rightAligned) {
		JLabel label = new JLabel(text);
		Dimension size = label.getPreferredSize();
		if (size.width < width) {
			size.width = width;
		}
		if (rightAligned) {
			label.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		if (width > 0) {
			label.setPreferredSize(size);
		}
		return label;
	}
	
	@SuppressWarnings("unchecked")
	public static <JC extends JComponent> Set<JC> getDescendents(Container container, Class<JC> type) {
		if (container == null) {
			return null;
		}
		Set<JC> components = new HashSet<JC>();
		for (Component component : container.getComponents()) {
			if (component instanceof JComponent) {
				if (type == null || type == component.getClass() || type.isAssignableFrom(component.getClass())) {
					components.add((JC) component);
				}
			}
			if (component instanceof Container) {
				Set<JC> sub = getDescendents((Container) component, type);
				components.addAll(sub);
			}
		}
		return components;
	}
		
	public static Icon extractOptionPaneIcon(String text) {
		JOptionPane opt = new JOptionPane("message", JOptionPane.INFORMATION_MESSAGE);
		return extractHelper(opt, text);
	}
	
	public static void forgetWindowLocation(final Frame window) {
		for (ComponentListener listener : window.getComponentListeners()) {
			if (listener instanceof WindowSettingsComponentAdapter) {
				window.removeComponentListener(listener);
				String title = window.getTitle();
				props.remove(title + "-x");
				props.remove(title + "-y");
				props.remove(title + "-w");
				props.remove(title + "-h");
			}
		}
	}
	
	public static void growFont(JComponent button) {
		growFont(button, 1);
	}
	
	public static void growFont(JComponent button, int amount) {
		Font font = button.getFont();
		font = font.deriveFont((float) (font.getSize() + amount));
		button.setFont(font);
	}
	
	public static void loadWindowLocation(Frame window) {
		if (window == null) {
			return;
		}
		synchronized (props) {
			try {
				String settingsFile = tempDir + "/" + SETTINGS_FILENAME;
				if (new File(settingsFile).exists()) {
					InputStream input = new FileInputStream(settingsFile);
					props.load(input);
				}
			} catch (IOException ioe) {
				System.err.println("I/O error trying to load window settings: " + ioe);
			} catch (Exception e) {
				System.err.println("Error trying to save window settings: " + e);
			}
		}
		String title = window.getTitle(); 
		if (props.containsKey(title + "-x") && props.containsKey(title + "-y")) {
			int x = Integer.parseInt(props.getProperty(title + "-x"));
			int y = Integer.parseInt(props.getProperty(title + "-y"));
			window.setLocation(x, y);
			// System.out.println("Loaded location of window \"" + window.getTitle() + "\".");
		}
		if (props.containsKey(title + "-w") && props.containsKey(title + "-h")) {
			int w = Integer.parseInt(props.getProperty(title + "-w"));
			int h = Integer.parseInt(props.getProperty(title + "-h"));
			window.setSize(w, h);
			// System.out.println("Loaded size of window \"" + window.getTitle() + "\".");
		}
	}
	
	public static void pad(JComponent component, int w, int h) {
		Dimension size = component.getPreferredSize();
		size.width += w;
		size.height += h;
		component.setPreferredSize(size);
	}
	
	public static void rememberWindowLocation(final Frame window) {
		if (window != null) {
			window.addComponentListener(new WindowSettingsComponentAdapter());
			loadWindowLocation(window);
		}
	}
	
	public static void setSystemLookAndFeel() {
		try {
			String lnf = UIManager.getSystemLookAndFeelClassName();
			if (lnf == null || lnf.contains("MetalLookAndFeel")) {
				// workaround because system L&F seems to fail on Linux boxes
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			} else {
				UIManager.setLookAndFeel(lnf);
			}
			
			UIManager.getLookAndFeelDefaults().put("Slider.paintValue", false);
			UIManager.put("Slider.paintValue", false);
		} catch (Exception e) {
			// empty
		}
	}
	
	public static void shrinkFont(JComponent button) {
		shrinkFont(button, 1);
	}
	
	public static void shrinkFont(JComponent button, int amount) {
		Font font = button.getFont();
		font = font.deriveFont((float) (font.getSize() - amount));
		button.setFont(font);
	}

	public static void heighten(JComponent component, int px) {
		pad(component, 0, px);
	}
	
	public static void widen(JComponent component, int px) {
		pad(component, px, 0);
	}
	
	private GuiUtils() {
		// empty
	}
	
	private static Icon extractHelper(Component comp, String text) {
		if (comp instanceof JButton) {
			JButton button = (JButton) comp;
			String buttonText = String.valueOf(button.getText());
			if (buttonText.toUpperCase().contains(text.toUpperCase())) {
				return button.getIcon();
			}
		} else if (comp instanceof JLabel) {
			JLabel label = (JLabel) comp;
			String labelText = String.valueOf(label.getText());
			if (labelText.toUpperCase().contains(text.toUpperCase())) {
				return label.getIcon();
			}
		} else if (comp instanceof Container) {
			for (Component subcomp : ((Container) comp).getComponents()) {
				Icon icon = extractHelper(subcomp, text);
				if (icon != null) {
					return icon;
				}
			}
		}
		return null;
	}
	
	private static class ExtensionFileFilter extends FileFilter {
		private String description;
		private String[] extensions;
		
		public ExtensionFileFilter(String description, String[] extensions) {
			this.description = description;
			this.extensions = extensions;
		}
		
		@Override
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			}
			String filename = file.getName().toLowerCase();
			for (String extension : extensions) {
				extension = "." + extension.toLowerCase();
				if (filename.endsWith(extension)) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public String getDescription() {
			return description;
		}
	}
	
	private static class WindowSettingsComponentAdapter extends ComponentAdapter {
		public void componentMoved(ComponentEvent event) {
			Component component = event.getComponent();
			if (!(component instanceof Frame)) return;
			Frame window = (Frame) component; 
			int x = window.getX();
			int y = window.getY();
			int w = window.getWidth();
			int h = window.getHeight();
			synchronized (props) {
				props.setProperty(window.getTitle() + "-x", String.valueOf(x));
				props.setProperty(window.getTitle() + "-y", String.valueOf(y));
				props.setProperty(window.getTitle() + "-w", String.valueOf(w));
				props.setProperty(window.getTitle() + "-h", String.valueOf(h));
				try {
					// String tempDir = System.getProperty("user.dir");
					String settingsFile = tempDir + "/" + SETTINGS_FILENAME;
					OutputStream out = new FileOutputStream(settingsFile);
					props.store(out, "Stanford Autograder window location settings");
					out.close();
					// System.out.println("Saved size/location of window \"" + window.getTitle() + "\".");
				} catch (IOException ioe) {
					System.err.println("I/O error trying to save window settings: " + ioe);
				} catch (Exception e) {
					System.err.println("Error trying to save window settings: " + e);
				}
			}
		}
	}
}
