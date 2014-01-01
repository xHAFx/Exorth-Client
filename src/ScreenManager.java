import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import javax.swing.JFrame;

/**
 * The screen engine stuff here..
 * 
 * @author AkZu
 * 
 */
public class ScreenManager {

	// Configure the class..
	public ScreenManager(Frame window, GameShell rsApplet) {
		this.rsApplet = rsApplet;
		this.window = window;
		for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
			if (device != null && device.getDisplayModes().length > 0) {
				this.device = device;
				this.desired_mode = device.getDisplayMode();
				break;
			}
		}
		getSupportedResolutions();
	}

	public Frame window;
	public JFrame frame;
	public final GameShell rsApplet;

	public byte current_screen_pos = 0;

	// Main graphic device
	GraphicsDevice device;

	// A list of avaible display modes for the current screen
	DisplayMode avaible_modes[] = null;

	// The desired mode we want to use as default
	DisplayMode desired_mode;

	// Lists the modes to the interface
	public String mode_to_string(byte index) {
		return "[" + avaible_modes[index].getWidth() + "x" + avaible_modes[index].getHeight() + "x" + avaible_modes[index].getBitDepth()
				+ "]";
	}

	/**
	 * @param mode
	 *            0 = Fixed, 1 = Resizable, 2 = Fullscreen
	 */
	public void enter_screen_mode(byte mode) {
		if (mode == 2) {

			// No support for fullscreen.
			if (desired_mode == null)
				return;

			// Web applet mode
			if (window == null) {
				frame = new JFrame();
				frame.setContentPane(rsApplet.gamePanel);
				frame.setFocusTraversalKeysEnabled(false);
				rsApplet.setEnabled(false);

				// -- Add the input handlers..
				frame.addMouseWheelListener(rsApplet);
				frame.addMouseWheelListener(rsApplet);
				frame.addMouseListener(rsApplet);
				frame.addMouseMotionListener(rsApplet);
				frame.addKeyListener(rsApplet);
				frame.addFocusListener(rsApplet);

				frame.setUndecorated(true);
				frame.setResizable(false);
			} else {
				// Normal desktop mode
				window.dispose();
				window.setUndecorated(true);
				window.setResizable(false);
			}
			device.setFullScreenWindow((window == null ? frame : window));
			if (device.isDisplayChangeSupported())
				device.setDisplayMode(desired_mode);
			rsApplet.setWindowInsets(new Insets(0, 0, 0, 0));
			rsApplet.graphics = device.getFullScreenWindow().getGraphics();
			RSClient.clientWidth = desired_mode.getWidth();
			RSClient.clientHeight = desired_mode.getHeight();
		} else {
			if (device.getFullScreenWindow() != null) {
				device.getFullScreenWindow().dispose();
				device.setFullScreenWindow(null);
				if (window == null) {
					rsApplet.setEnabled(true);
					rsApplet.requestFocus();
				}
			}
			if (window != null) {
				window.setTitle("Exorth");
				window.setResizable(!window.isResizable() && mode == 1 ? true : false);
				if (window.isUndecorated())
					window.setUndecorated(false);
				window.setVisible(true);
				window.toFront();
				Insets insets = window.getInsets();
				window.setSize(RSClient.clientWidth + insets.left + insets.right, RSClient.clientHeight + insets.top + insets.bottom);
				rsApplet.setWindowInsets(insets);
				window.requestFocus();
			}
			rsApplet.graphics = rsApplet.getGameComponent().getGraphics();
			if (mode == 0 && !RSClient.isWidget())
				rsApplet.client.setSize(765, 503);
		}
	}

	private void getSupportedResolutions() {
		try {

			DisplayMode all_avaible_modes[] = device.getDisplayModes();
			DisplayMode temp_modes[] = new DisplayMode[100];

			byte loop0 = 0;
			byte loop1 = 0;
			byte loop2 = 0;

			// Loop through all our display modes on our current screen device..
			// Filter all modes that are below 800x600
			for (DisplayMode mode : all_avaible_modes) {
				if (mode.getWidth() >= 800 && mode.getHeight() >= 600) {
					if (temp_modes[0] == null || mode.getWidth() != temp_modes[loop0 - 1].getWidth()
							&& mode.getHeight() != temp_modes[loop0 - 1].getHeight()) {
						temp_modes[loop0] = mode;
						loop0++;
					}
				}
			}

			// Loop through all found display modes and only count non nulled
			// ones..
			for (DisplayMode mode : temp_modes) {
				if (mode != null)
					loop1++;
			}

			// Setup our avaible display modes size..
			avaible_modes = new DisplayMode[loop1];

			for (DisplayMode mode : temp_modes) {
				if (mode != null) {
					avaible_modes[loop2] = mode;
					loop2++;
				}
			}

			// Reset memory off our temporary found modes
			temp_modes = null;
			all_avaible_modes = null;

			// Default fullscreen mode is same as our active mode
			for (byte mode2 = 0; mode2 < avaible_modes.length; mode2++) {
				if (avaible_modes[mode2].getWidth() == device.getDisplayMode().getWidth()
						&& avaible_modes[mode2].getHeight() == device.getDisplayMode().getHeight()
						&& avaible_modes[mode2].getBitDepth() == device.getDisplayMode().getBitDepth()) {
					desired_mode = avaible_modes[mode2];
					current_screen_pos = mode2;
				}
			}
		} catch (Exception e) {
			System.err.println("[ERROR]: No display modes found! Fullscreen is DISABLED!");
		}
	}
}
