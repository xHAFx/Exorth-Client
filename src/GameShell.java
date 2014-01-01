import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class GameShell extends Applet implements Runnable, MouseListener, MouseMotionListener, KeyListener, FocusListener, WindowListener,
		MouseWheelListener {

	public RSClient client;
	public ScreenManager screenManager;
	public JPanel gamePanel;
	private Insets window_insets = new Insets(0, 0, 0, 0);

	public void setClient(RSClient client) {
		this.client = client;
	}

	public void enter_screen_mode(byte mode) {
		screenManager.enter_screen_mode(mode);
	}

	/**
	 * gameFrame = null >> using Applet gameFrame != null >> using Frame
	 */
	final void init_client(int y, int x) {
		myWidth = x;
		myHeight = y;

		if (RSClient.isWidget())
			gameFrame = new RSFrame(this, x, y);

		gamePanel = new JPanel();

		gamePanel.setFocusTraversalKeysEnabled(false);

		// Start the Screen Manager
		screenManager = new ScreenManager(gameFrame, this);

		graphics = getGameComponent().getGraphics();
		fullGameScreen = new RSImageProducer(myWidth, myHeight, getGameComponent());

		startRunnable(this, 8);
	}

	public void run() {
		Thread.currentThread().setName("MainEngine");
		getGameComponent().addMouseWheelListener(this);
		getGameComponent().addMouseListener(this);
		getGameComponent().addMouseMotionListener(this);
		getGameComponent().addKeyListener(this);
		getGameComponent().addFocusListener(this);
		if (gameFrame != null)
			gameFrame.addWindowListener(this);
		startUp();
		int i = 0;
		int j = 256;
		int k = 1;
		int i1 = 0;
		for (int k1 = 0; k1 < 10; k1++)
			aLongArray7[k1] = System.currentTimeMillis();
		while (anInt4 >= 0) {
			if (anInt4 > 0) {
				anInt4--;
				if (anInt4 == 0) {
					exit();
					return;
				}
			}
			int i2 = j;
			int j2 = k;
			j = 300;
			k = 1;
			long l1 = System.currentTimeMillis();
			if (aLongArray7[i] == 0L) {
				j = i2;
				k = j2;
			} else if (l1 > aLongArray7[i])
				j = (int) ((long) (2560 * delayTime) / (l1 - aLongArray7[i]));
			if (j < 25)
				j = 25;
			if (j > 256) {
				j = 256;
				k = (int) ((long) delayTime - (l1 - aLongArray7[i]) / 10L);
			}
			if (k > delayTime)
				k = delayTime;
			aLongArray7[i] = l1;
			i = (i + 1) % 10;
			if (k > 1) {
				for (int k2 = 0; k2 < 10; k2++)
					if (aLongArray7[k2] != 0L)
						aLongArray7[k2] += k;
			}
			if (k < minDelay)
				k = minDelay;
			try {
				Thread.sleep(k);
			} catch (InterruptedException _ex) {
			}
			for (; i1 < 256; i1 += j) {
				clickMode3 = clickMode1;
				saveClickX = clickX;
				saveClickY = clickY;
				aLong29 = clickTime;
				clickMode1 = 0;
				processGameLoop();
				readIndex = writeIndex;
			}
			i1 &= 0xff;
			if (delayTime > 0)
				fps = (1000 * j) / (delayTime * 256);
			processDrawing();
		}
		if (anInt4 == -1)
			exit();
	}

	public void exit() {
		anInt4 = -2;
		System.exit(0);
	}

	final void method4(int i) {
		delayTime = 1000 / i;
	}

	public final void start() {
		if (anInt4 >= 0)
			anInt4 = 0;
	}

	public final void stop() {
		if (anInt4 >= 0)
			anInt4 = 4000 / delayTime;
	}

	public final void destroy() {
		anInt4 = -1;
		if (anInt4 == -1)
			exit();
	}

	public final void update(Graphics g) {
		if (graphics == null)
			graphics = g;
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}

	public final void paint(Graphics g) {
		if (graphics == null)
			graphics = g;
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}

	public void handleInterfaceScrolling(MouseWheelEvent event) {
		int rotation = event.getWheelRotation();
		int positionX = 0;
		int positionX2 = 0;
		int positionY2 = 0;
		int positionY = 0;
		int width = 0;
		int width2 = 0;
		int height2 = 0;
		int height = 0;
		int offsetX = 0;
		int offsetY = 0;
		int childID = 0;
		int tabInterfaceID = RSClient.tabInterfaceIDs[RSClient.tabID];
		if (tabInterfaceID != -1 && !client.invHidden) {
			RSInterface tab = RSInterface.interfaceCache[tabInterfaceID];
			if (RSClient.clientSize == 0) {
				offsetX = 765 - 218;
				offsetY = 503 - 298;
			} else if (RSClient.clientWidth >= 1006) {
				offsetX = RSClient.clientWidth - 199;
				offsetY = RSClient.clientHeight - 304;
			} else {
				offsetX = RSClient.clientWidth - 212;
				offsetY = RSClient.clientHeight - 342;
			}
			for (int index = 0; index < tab.children.length; index++) {
				if (RSInterface.interfaceCache[tab.children[index]].scrollMax > 0) {
					childID = index;
					positionX = tab.childX[index];
					positionY = tab.childY[index];
					width = RSInterface.interfaceCache[tab.children[index]].width;
					height = RSInterface.interfaceCache[tab.children[index]].height;
					break;
				}
			}
			int percent = (int) (((float) RSInterface.interfaceCache[tab.children[childID]].scrollMax / 20F));
			if (mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width
					&& mouseY < offsetY + positionY + height) {
				if (((RSInterface.interfaceCache[tab.children[childID]].scrollPosition + (rotation * percent)) > RSInterface.interfaceCache[tab.children[childID]].scrollMax
						- RSInterface.interfaceCache[tab.children[childID]].height)) {
					RSInterface.interfaceCache[tab.children[childID]].scrollPosition = (short) (RSInterface.interfaceCache[tab.children[childID]].scrollMax - RSInterface.interfaceCache[tab.children[childID]].height);
				} else if ((RSInterface.interfaceCache[tab.children[childID]].scrollPosition + (rotation * percent)) <= 0) {
					RSInterface.interfaceCache[tab.children[childID]].scrollPosition = 0;
				} else
					RSInterface.interfaceCache[tab.children[childID]].scrollPosition += rotation * percent;
				client.needDrawTabArea = true;
			}
		}
		if (client.openInterfaceID != -1) {
			RSInterface rsi = RSInterface.interfaceCache[client.openInterfaceID];
			offsetX = (RSClient.clientSize == 0 ? 4 : client.returnGeneralInterfaceOffsetX());
			offsetY = (RSClient.clientSize == 0 ? 4 : (RSClient.clientHeight / 2) - 256);
			for (int index = 0; index < rsi.children.length; index++) {
				if (RSInterface.interfaceCache[rsi.children[index]].scrollMax > 0) {
					childID = index;
					positionX = rsi.childX[index];
					positionY = rsi.childY[index];
					if (rsi.id == 6575 || rsi.id == 3443 || rsi.id == 6412) {
						positionX2 = rsi.childX[index + 1];
						positionY2 = rsi.childY[index + 1];
						width2 = RSInterface.interfaceCache[rsi.children[index + 1]].width;
						height2 = RSInterface.interfaceCache[rsi.children[index + 1]].height;
					}
					width = RSInterface.interfaceCache[rsi.children[index]].width;
					height = RSInterface.interfaceCache[rsi.children[index]].height;
					break;
				}
			}
			int percent = (int) (((float) RSInterface.interfaceCache[rsi.children[childID]].scrollMax / 20F));
			if ((mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width && mouseY < offsetY
					+ positionY + height)) {
				if (((RSInterface.interfaceCache[rsi.children[childID]].scrollPosition + (rotation * percent)) > RSInterface.interfaceCache[rsi.children[childID]].scrollMax
						- RSInterface.interfaceCache[rsi.children[childID]].height)) {
					RSInterface.interfaceCache[rsi.children[childID]].scrollPosition = (short) (RSInterface.interfaceCache[rsi.children[childID]].scrollMax - RSInterface.interfaceCache[rsi.children[childID]].height);
				} else if ((RSInterface.interfaceCache[rsi.children[childID]].scrollPosition + (rotation * percent)) <= 0) {
					RSInterface.interfaceCache[rsi.children[childID]].scrollPosition = 0;
				} else
					RSInterface.interfaceCache[rsi.children[childID]].scrollPosition += rotation * percent;
				client.needDrawTabArea = true;
			}
			if ((mouseX > offsetX + positionX2 && mouseY > offsetY + positionY2 && mouseX < offsetX + positionX2 + width2 && mouseY < offsetY
					+ positionY2 + height2)) {
				switch (rsi.id) {
				case 6575:
				case 3443:
				case 6412:
					RSInterface.interfaceCache[rsi.children[childID + 1]].scrollPosition += rotation * (rsi.id == 6412 ? 10 : percent);
					break;
				}
				client.needDrawTabArea = true;
			}
		}
	}

	public boolean mouseIsWithin(int X, int Y, int Width, int Height) {
		if (mouseX >= X && mouseX <= X + Width && mouseY >= Y && mouseY <= Y + Height) {
			return true;
		} else {
			return false;
		}
	}

	public void mouseWheelMoved(MouseWheelEvent event) {
		int rotation = event.getWheelRotation();
		handleInterfaceScrolling(event);
		if (event.isControlDown()) {
			client.client_zoom -= (rotation * 30);
			if (client.client_zoom <= -268)
				client.client_zoom = -268;

			if (client.client_zoom >= 568)
				client.client_zoom = 568;
		}
		if (!client.chatHidden && mouseX >= 6 && mouseX <= 513
				&& mouseY >= (RSClient.clientSize == 0 ? 503 - 159 : RSClient.clientHeight - 160)
				&& mouseY <= (RSClient.clientSize == 0 ? 503 - 30 : RSClient.clientHeight - 31)) {
			int percent = (int) (((float) RSClient.chatScrollMax / 20F));
			int scrollPos = RSClient.chatScrollPos;
			scrollPos -= rotation * percent;
			if (scrollPos < 0)
				scrollPos = 0;
			if (scrollPos > RSClient.chatScrollMax - 114)
				scrollPos = RSClient.chatScrollMax - 114;
			if (RSClient.chatScrollPos != scrollPos) {
				RSClient.chatScrollPos = scrollPos;
				RSClient.inputTaken = true;
			}
		}
		if (!RSClient.isWidget())
			gameFrame.setFocusable(false);
	}

	public final void mousePressed(MouseEvent mouseevent) {
		int i = mouseevent.getX();
		int j = mouseevent.getY();
		if (gameFrame != null) {
			i -= getWindowInsets().left;
			j -= getWindowInsets().top;
		}
		idleTime = 0;
		clickX = i;
		clickY = j;
		clickTime = System.currentTimeMillis();
		if (SwingUtilities.isMiddleMouseButton(mouseevent)) {
		} else if (SwingUtilities.isLeftMouseButton(mouseevent)) {
			clickMode1 = 1;
			clickMode2 = 1;
		} else {
			clickMode1 = 2;
			clickMode2 = 2;
		}
	}

	public final void mouseReleased(MouseEvent mouseevent) {
		idleTime = 0;
		clickMode2 = 0;
	}

	public final void mouseClicked(MouseEvent mouseevent) {
	}

	public final void mouseEntered(MouseEvent mouseevent) {
	}

	public final void mouseExited(MouseEvent mouseevent) {
		idleTime = 0;
		mouseX = -1;
		mouseY = -1;
	}

	public final void mouseDragged(MouseEvent mouseevent) {
		int x = mouseevent.getX();
		int y = mouseevent.getY();
		if (gameFrame != null) {
			x -= getWindowInsets().left;
			y -= getWindowInsets().top;
		}
		if (System.currentTimeMillis() - clickTime >= 250L || Math.abs(saveClickX - x) > 5 || Math.abs(saveClickY - y) > 5) {
			idleTime = 0;
			mouseX = x;
			mouseY = y;
		}
	}

	public final void mouseMoved(MouseEvent mouseevent) {
		int i = mouseevent.getX();
		int j = mouseevent.getY();
		if (gameFrame != null) {
			i -= getWindowInsets().left;
			j -= getWindowInsets().top;
		}
		if (System.currentTimeMillis() - clickTime >= 250L || Math.abs(saveClickX - i) > 5 || Math.abs(saveClickY - j) > 5) {
			idleTime = 0;
			mouseX = i;
			mouseY = j;
		}
	}

	/**
	 * Screenshot related stuff
	 */
	public static final String[] DATE_FORMAT = { "dd-MM-yyyy", "MM-dd-yyyy", "dd.MM.yyyy", "MM.dd.yyyy", "dd/MM/yyyy", "MM/dd/yyyy",
			"yyyy-MM-dd", "yyyy-dd-MM", "yyyy.MM.dd", "yyyy.dd.MM", "yyyy/MM/dd", "yyyy/dd/MM" };
	public static final int[] DATE_COLOURS = { 0xffffff, 0xff0000, 0xffff00, 0x00FF00, 0x0000FF, 0xFF00FF, 0x00FFFF };
	public static final String[] DATE_COLOURS_NAME = { "White", "Red", "Yellow", "Green", "Blue", "Pink", "Cyan" };

	public static String getDate(int format) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat date = new SimpleDateFormat(DATE_FORMAT[format]);
		return date.format(cal.getTime());
	}

	public static void browseFolders(File url) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(url);
			} catch (IOException e) {
			}
		}
	}

	public void updateScreenShotStrings() {

		// Disable the game crop on resizable.
		if (pictureRegionID == 2 && RSClient.clientSize != 0)
			pictureRegionID = 0;

		client.sendString("Date format: " + DATE_FORMAT[DATE_LOCAL].toUpperCase(), 1370);
		client.sendString("Prefix: '" + pictureFileName + "'", 1294);
		client.sendString("File format: '" + pictureFormat[pictureFormatQuality].toUpperCase() + "'", 1253);
		client.sendString("Picture folder: \\n" + Signlink.cacheLocation() + "pictures" + System.getProperty("file.separator"), 1293);
		client.sendString("Picture count: " + pictureCount, 1254);
		client.sendString("Date color: [" + DATE_COLOURS_NAME[dateColour].toUpperCase() + "]", 1363);
		client.sendString("Region: " + pictureRegion[pictureRegionID], 1393);
		RSInterface rsi = RSInterface.interfaceCache[1370];
		TextDrawingArea tda = client.chatText;
		rsi.width = (short) tda.getTextWidth("Date format: " + DATE_FORMAT[DATE_LOCAL].toUpperCase());
		RSInterface rsi1 = RSInterface.interfaceCache[1294];
		rsi1.width = (short) tda.getTextWidth("Prefix: '" + pictureFileName + "'");
		RSInterface rsi2 = RSInterface.interfaceCache[1253];
		rsi2.width = (short) tda.getTextWidth("File format: '" + pictureFormat[pictureFormatQuality].toUpperCase() + "'");
		RSInterface rsi3 = RSInterface.interfaceCache[1363];
		rsi3.width = (short) tda.getTextWidth("Date color: [" + DATE_COLOURS_NAME[dateColour].toUpperCase() + "]");
		RSInterface rsi4 = RSInterface.interfaceCache[1254];
		rsi4.width = (short) tda.getTextWidth("Picture count: " + pictureCount);
		RSInterface rsi6 = RSInterface.interfaceCache[1393];
		rsi6.width = (short) tda.getTextWidth("Region: " + pictureRegion[pictureRegionID]);
		RSInterface rsi5 = RSInterface.interfaceCache[1293];
		rsi5.width = (short) tda.getTextWidth("Picture folder: \\n" + Signlink.cacheLocation() + "pictures"
				+ System.getProperty("file.separator"));
		RSClient.inputTaken = true;
	}

	public boolean screenDate;
	public int screenDateTimer;
	public static int DATE_LOCAL = 0;
	public static boolean useDate = true;
	public static int pictureCount = 0;
	public static int dateColour = 0;
	public static String[] pictureFormat = { "jpeg", "png", "bmp" };
	public static String[] pictureRegion = { "Normal", "Bank", "Game", "Inventory", "Chat" };
	public static int[][] pictureRegionSize = { { 765, 503, 0, 0 }, { 488, 305, 16, 24 }, { 512, 334, 4, 4 }, { 241, 335, 522, 168 },
			{ 518, 141, 1, 339 } };
	public static int pictureRegionID = 0;
	public static int pictureFormatQuality = 1;
	public static String pictureFileName = getDate(DATE_LOCAL) + "_";

	public void preScreenShot() {
		screenDate = true;
		screenDateTimer = 2;
		RSClient.reportAbuseText = getDate(DATE_LOCAL);
		RSClient.inputTaken = true;
	}

	public void screenShot() {
		File file = new File(Signlink.cacheLocation() + "pictures" + System.getProperty("file.separator"));
		if (!file.exists()) {
			file.mkdir();
		}
		// No work for fullscreen
		if (RSClient.clientSize == 2)
			return;

		String fileName = pictureFileName.replaceAll("/", "-");
		try {
			Rectangle screenRectangle = null;

			if (RSClient.clientSize == 0)
				screenRectangle = new Rectangle(getGameComponent().getLocationOnScreen().x + getWindowInsets().left
						+ pictureRegionSize[pictureRegionID][2], getGameComponent().getLocationOnScreen().y + getWindowInsets().top
						+ pictureRegionSize[pictureRegionID][3], pictureRegionSize[pictureRegionID][0],
						pictureRegionSize[pictureRegionID][1]);
			else {
				short x = 0;
				short y = 0;
				short width = 0;
				short height = 0;

				switch (pictureRegionID) {
				case 0: // Normal
					width = (short) RSClient.clientWidth;
					height = (short) RSClient.clientHeight;
					break;
				case 1: // Bank
					width = 488;
					height = 305;
					x = (short) (client.returnGeneralInterfaceOffsetX() + 12);
					y = (short) ((RSClient.clientHeight / 2) - 256 + 20);
					break;
				case 3: // Inventory
					width = 204;
					height = 275;
					x = (short) (RSClient.clientWidth >= 1006 ? (RSClient.clientWidth - 206) : (RSClient.clientWidth - 219));
					y = (short) (RSClient.clientWidth >= 1006 ? (RSClient.clientHeight - 313) : (RSClient.clientHeight - 351));
					break;
				case 4: // Chat
					width = 518;
					height = 141;
					x = 1;
					y = (short) (RSClient.clientHeight - 164);
					break;
				}
				screenRectangle = new Rectangle(getGameComponent().getLocationOnScreen().x + getWindowInsets().left + x, getGameComponent()
						.getLocationOnScreen().y + getWindowInsets().top + y, width, height);
			}

			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screenRectangle);
			ImageIO.write(image, pictureFormat[pictureFormatQuality],
					new File(Signlink.cacheLocation() + "pictures" + System.getProperty("file.separator") + fileName + pictureCount + "."
							+ pictureFormat[pictureFormatQuality]));
			pictureCount += 1;
			RSClient.writeSettings();
			updateScreenShotStrings();
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void keyPressed(KeyEvent keyevent) {
		idleTime = 0;
		int i = keyevent.getKeyCode();
		int j = keyevent.getKeyChar();
		if (i == KeyEvent.VK_TAB && client.loggedIn)
			client.responsePrivateMessage();
		if (keyevent.isControlDown() && i == KeyEvent.VK_F && client.loggedIn)
			preScreenShot();
		if (i == KeyEvent.VK_ESCAPE && RSClient.clientSize == 2)
			client.toggleSize(1);
		if (client.invOverlayInterfaceID == -1) {
			if (i == KeyEvent.VK_F1)
				client.setTab(0);
			if (i == KeyEvent.VK_F2)
				client.setTab(1);
			if (i == KeyEvent.VK_F3)
				client.setTab(2);
			if (i == KeyEvent.VK_F4)
				client.setTab(3);
			if (i == KeyEvent.VK_F5)
				client.setTab(4);
			if (i == KeyEvent.VK_F6)
				client.setTab(5);
			if (i == KeyEvent.VK_F7)
				client.setTab(6);
			if (i == KeyEvent.VK_F8)
				client.setTab(8);
			if (i == KeyEvent.VK_F9)
				client.setTab(9);
			if (i == KeyEvent.VK_F10)
				client.setTab(10);
			if (i == KeyEvent.VK_F11)
				client.setTab(11);
			if (i == KeyEvent.VK_F12)
				client.setTab(12);
		}
		if (j < 30)
			j = 0;
		if (i == 37)
			j = 1;
		if (i == 39)
			j = 2;
		if (i == 38)
			j = 3;
		if (i == 40)
			j = 4;
		if (i == 17)
			j = 5;
		if (i == 8)
			j = 8;
		if (i == 127)
			j = 8;
		if (i == 9)
			j = 9;
		if (i == 10)
			j = 10;
		if (i >= 112 && i <= 123)
			j = (1008 + i) - 112;
		if (i == 36)
			j = 1000;
		if (i == 35)
			j = 1001;
		if (i == 33)
			j = 1002;
		if (i == 34)
			j = 1003;
		if (j > 0 && j < 128)
			keyArray[j] = 1;
		if (j > 4) {
			charQueue[writeIndex] = j;
			writeIndex = writeIndex + 1 & 0x7f;
		}
	}

	public final void keyReleased(KeyEvent keyevent) {
		idleTime = 0;
		int i = keyevent.getKeyCode();
		char c = keyevent.getKeyChar();
		if (c < '\036')
			c = '\0';
		if (i == 37)
			c = '\001';
		if (i == 39)
			c = '\002';
		if (i == 38)
			c = '\003';
		if (i == 40)
			c = '\004';
		if (i == 17)
			c = '\005';
		if (i == 8)
			c = '\b';
		if (i == 127)
			c = '\b';
		if (i == 9)
			c = '\t';
		if (i == 10)
			c = '\n';
		if (c > 0 && c < '\200')
			keyArray[c] = 0;
	}

	public final void keyTyped(KeyEvent keyevent) {
	}

	final int readChar(int dummy) {
		while (dummy >= 0) {
			for (int j = 1; j > 0; j++)
				;
		}
		int k = -1;
		if (writeIndex != readIndex) {
			k = charQueue[readIndex];
			readIndex = readIndex + 1 & 0x7f;
		}
		return k;
	}

	public final void focusGained(FocusEvent focusevent) {
		awtFocus = true;
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}

	public final void focusLost(FocusEvent focusevent) {
		awtFocus = false;
		for (int i = 0; i < 128; i++)
			keyArray[i] = 0;
	}

	public final void windowActivated(WindowEvent windowevent) {
	}

	public final void windowStateChanged(WindowEvent windowevent) {
	}

	public final void windowClosed(WindowEvent windowevent) {
	}

	public final void windowClosing(WindowEvent windowevent) {
		System.exit(0);
	}

	public final void windowDeactivated(WindowEvent windowevent) {
	}

	public final void windowDeiconified(WindowEvent windowevent) {
	}

	public final void windowIconified(WindowEvent windowevent) {
	}

	public final void windowOpened(WindowEvent windowevent) {
	}

	void startUp() {
	}

	void processGameLoop() {
	}

	void cleanUpForQuit() {
	}

	void processDrawing() {
	}

	void raiseWelcomeScreen() {
	}

	Component getGameComponent() {
		if (gameFrame != null)
			return gameFrame;
		else
			return gamePanel;
	}

	public void startRunnable(Runnable runnable, int priority) {
		Thread thread = new Thread(runnable);
		thread.start();
		thread.setPriority(priority);
	}

	void drawLoadingText(int i, String s) {
		while (graphics == null) {
			graphics = getGameComponent().getGraphics();
			try {
				getGameComponent().repaint();
			} catch (Exception _ex) {
			}
			try {
				Thread.sleep(1000L);
			} catch (Exception _ex) {
			}
		}
		Font font = new Font("Helvetica", 1, 13);
		FontMetrics fontmetrics = getGameComponent().getFontMetrics(font);
		Font font1 = new Font("Helvetica", 0, 13);
		getGameComponent().getFontMetrics(font1);
		if (shouldClearScreen) {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, myWidth, myHeight);
			shouldClearScreen = false;
		}
		Color color = new Color(140, 17, 17); // a crimson type color.
		int j = myHeight / 2 - 18;
		graphics.setColor(color);
		graphics.drawRect(myWidth / 2 - 152, j, 304, 34);
		graphics.fillRect(myWidth / 2 - 150, j + 2, i * 3, 30);
		graphics.setColor(Color.BLACK);
		graphics.fillRect((myWidth / 2 - 150) + i * 3, j + 2, 300 - i * 3, 30);
		graphics.setFont(font);
		graphics.setColor(Color.white);
		graphics.drawString(s, (myWidth - fontmetrics.stringWidth(s)) / 2, j + 22);
	}

	GameShell() {
		delayTime = 20;
		minDelay = 1;
		aLongArray7 = new long[10];
		shouldClearScreen = true;
		awtFocus = true;
		keyArray = new int[128];
		charQueue = new int[128];
	}

	public void setWindowInsets(Insets insets) {
		this.window_insets = insets;
	}

	public Insets getWindowInsets() {
		return window_insets;
	}

	private int anInt4;
	private int delayTime;
	int minDelay;
	private final long[] aLongArray7;
	int fps;
	int myWidth;
	int myHeight;
	Graphics graphics;
	RSImageProducer fullGameScreen;
	RSFrame gameFrame;
	private boolean shouldClearScreen;
	boolean awtFocus;
	int idleTime;
	int clickMode2;
	public static int mouseX;
	public static int mouseY;
	private int clickMode1;
	private int clickX;
	private int clickY;
	private long clickTime;
	static int clickMode3;
	int saveClickX;
	int saveClickY;
	long aLong29;
	final int[] keyArray;
	private final int[] charQueue;
	private int readIndex;
	private int writeIndex;
	public static int anInt34;
}
