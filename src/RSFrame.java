import java.awt.*;

@SuppressWarnings("serial")
final class RSFrame extends Frame {

	public RSFrame(GameShell RSApplet_, int i, int j) {
		rsApplet = RSApplet_;
		setTitle("Exorth");
		setResizable(false);
		setVisible(true);
		setFocusTraversalKeysEnabled(false);
		toFront();
		Insets insets = getInsets();
		setSize(i + insets.left + insets.right, j + insets.top + insets.bottom);
		setLocationRelativeTo(null);
		rsApplet.setWindowInsets(insets);
	}

	public Graphics getGraphics() {
		Graphics g = super.getGraphics();
		g.translate(rsApplet.getWindowInsets().left, rsApplet.getWindowInsets().top);
		return g;
	}

	public void update(Graphics g) {
		rsApplet.update(g);
	}

	public void paint(Graphics g) {
		rsApplet.paint(g);
	}

	private final GameShell rsApplet;
}
