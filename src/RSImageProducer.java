import java.util.*;
import java.awt.*;
import java.awt.image.*;

public final class RSImageProducer {

	public RSImageProducer(int width, int height, Component component) {
		this.width = (short) width;
		this.height = (short) height;
		this.component = component;
		int count = width * height;
		anIntArray315 = new int[count];
		image = new BufferedImage(COLOR_MODEL, Raster.createWritableRaster(COLOR_MODEL.createCompatibleSampleModel(width, height),
				new DataBufferInt(anIntArray315, count), null), false, new Hashtable<Object, Object>());
		initDrawingArea();
	}

	public void drawGraphics(int y, Graphics gfx, int x) {
		gfx.drawImage(image, x, y, component);
	}

	public void draw(Graphics gfx, int x, int y, int clipX, int clipY, int clipWidth, int clipHeight) {
		Shape tmp = gfx.getClip();
		try {
			clip.x = clipX;
			clip.y = clipY;
			clip.width = clipWidth;
			clip.height = clipHeight;
			gfx.setClip(clip);
			gfx.drawImage(image, x, y, component);
		} finally {
			gfx.setClip(tmp);
		}
	}

	public void initDrawingArea() {
		RSRaster.initDrawingArea(height, width, anIntArray315);
	}

	public final int[] anIntArray315;
	public final short width;
	public final short height;
	public final BufferedImage image;
	public final Component component;
	private final Rectangle clip = new Rectangle();
	private static final ColorModel COLOR_MODEL = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);
}