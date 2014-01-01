import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;

import javax.swing.ImageIcon;

public final class DirectImage extends RSRaster {

	public DirectImage(int width, int height) {
		myPixels = new int[width * height];
		myWidth = maxWidth = (short) width;
		myHeight = maxHeight = (short) height;
		drawOffsetX = drawOffsetY = 0;
	}

	public void drawSprite(int i, int k, int color) {
		int tempWidth = myWidth + 2;
		int tempHeight = myHeight + 2;
		int[] tempArray = new int[tempWidth * tempHeight];
		for (int x = 0; x < myWidth; x++) {
			for (int y = 0; y < myHeight; y++) {
				if (myPixels[x + y * myWidth] != 0)
					tempArray[(x + 1) + (y + 1) * tempWidth] = myPixels[x + y * myWidth];
			}
		}
		for (int x = 0; x < tempWidth; x++) {
			for (int y = 0; y < tempHeight; y++) {
				if (tempArray[(x) + (y) * tempWidth] == 0) {
					if (x < tempWidth - 1 && tempArray[(x + 1) + ((y) * tempWidth)] > 0
							&& tempArray[(x + 1) + ((y) * tempWidth)] != 0xffffff) {
						tempArray[(x) + (y) * tempWidth] = color;
					}
					if (x > 0 && tempArray[(x - 1) + ((y) * tempWidth)] > 0 && tempArray[(x - 1) + ((y) * tempWidth)] != 0xffffff) {
						tempArray[(x) + (y) * tempWidth] = color;
					}
					if (y < tempHeight - 1 && tempArray[(x) + ((y + 1) * tempWidth)] > 0
							&& tempArray[(x) + ((y + 1) * tempWidth)] != 0xffffff) {
						tempArray[(x) + (y) * tempWidth] = color;
					}
					if (y > 0 && tempArray[(x) + ((y - 1) * tempWidth)] > 0 && tempArray[(x) + ((y - 1) * tempWidth)] != 0xffffff) {
						tempArray[(x) + (y) * tempWidth] = color;
					}
				}
			}
		}
		i--;
		k--;
		i += drawOffsetX;
		k += drawOffsetY;
		int l = i + k * RSRaster.width;
		int i1 = 0;
		int j1 = tempHeight;
		int k1 = tempWidth;
		int l1 = RSRaster.width - k1;
		int i2 = 0;
		if (k < RSRaster.topY) {
			int j2 = RSRaster.topY - k;
			j1 -= j2;
			k = RSRaster.topY;
			i1 += j2 * k1;
			l += j2 * RSRaster.width;
		}
		if (k + j1 > RSRaster.bottomY) {
			j1 -= (k + j1) - RSRaster.bottomY;
		}
		if (i < RSRaster.topX) {
			int k2 = RSRaster.topX - i;
			k1 -= k2;
			i = RSRaster.topX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (i + k1 > RSRaster.bottomX) {
			int l2 = (i + k1) - RSRaster.bottomX;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (!(k1 <= 0 || j1 <= 0)) {
			method349(RSRaster.pixels, tempArray, i1, l, k1, j1, l1, i2);
		}
	}

	/**
	 * Reading HD (ARGB) Sprites from the cache! ;)
	 * 
	 * Credits PB600 + AkZu
	 */
	/*
	 * public Sprite(CacheArchive streamLoader, String s, int i, boolean dummy)
	 * { Image image =
	 * Toolkit.getDefaultToolkit().getImage("C:/Users/AkZu/.rs2cache/test.png");
	 * ImageIcon sprite = new ImageIcon(image); myWidth = (short)
	 * sprite.getIconWidth(); myHeight = (short) sprite.getIconHeight();
	 * maxWidth = myWidth; maxHeight = myHeight; drawOffsetX = 0; drawOffsetY =
	 * 0; myPixels = new int[myWidth * myHeight]; PixelGrabber pixelgrabber =
	 * new PixelGrabber(image, 0, 0, myWidth, myHeight, myPixels, 0, myWidth);
	 * try { pixelgrabber.grabPixels(); } catch (InterruptedException e) {
	 * e.printStackTrace(); } image = null; //setTransparency(255, 0, 255);
	 * 
	 * RSBuffer stream = new RSBuffer(streamLoader.getDataForName(s + ".dat"));
	 * RSBuffer stream_1 = new
	 * RSBuffer(streamLoader.getDataForName("index.dat")); stream_1.pointer =
	 * stream.readUShort(); maxWidth = (short) stream_1.readUShort(); maxHeight
	 * = (short) stream_1.readUShort(); int j = stream_1.readInt(); int ai[] =
	 * new int[j]; for (int k = 0; k < j - 1; k++) { ai[k + 1] =
	 * stream_1.read24Int(); if (ai[k + 1] == 0) ai[k + 1] = 1; } for (int l =
	 * 0; l < i; l++) { stream_1.pointer += 2; stream.pointer +=
	 * stream_1.readUShort() * stream_1.readUShort(); stream_1.pointer++; }
	 * drawOffsetX = (byte) stream_1.readUByte(); drawOffsetY = (byte)
	 * stream_1.readUByte(); myWidth = (short) stream_1.readUShort(); myHeight =
	 * (short) stream_1.readUShort(); int i1 = stream_1.readUByte(); int j1 =
	 * myWidth * myHeight; myPixels = new int[j1]; if (i1 == 0) { for (int k1 =
	 * 0; k1 < j1; k1++) myPixels[k1] = ai[stream.readInt()]; return; } if (i1
	 * == 1) { for (int l1 = 0; l1 < myWidth; l1++) { for (int i2 = 0; i2 <
	 * myHeight; i2++) myPixels[l1 + i2 * myWidth] = ai[stream.readInt()]; } } }
	 */

	/**
	 * Run trough all Sprite pixels searching for pixels 100% Transparent(Alpha
	 * = 0); Then make it "Invisible";
	 * 
	 * @param a
	 *            the alpha value necessary to make pixel invisible.
	 */
	public void setAlphaTransparency(int a) {
		for (int pixel = 0; pixel < myPixels.length; pixel++) {
			if (((myPixels[pixel] >> 24) & 255) == a)
				myPixels[pixel] = 0;
		}
	}

	public void drawAdvancedSprite(int i, int j) {
		int k = 256;
		i += drawOffsetX;
		j += drawOffsetY;
		int i1 = i + j * RSRaster.width;
		int j1 = 0;
		int k1 = myHeight;
		int l1 = myWidth;
		int i2 = RSRaster.width - l1;
		int j2 = 0;
		if (j < RSRaster.topY) {
			int k2 = RSRaster.topY - j;
			k1 -= k2;
			j = RSRaster.topY;
			j1 += k2 * l1;
			i1 += k2 * RSRaster.width;
		}
		if (j + k1 > RSRaster.bottomY)
			k1 -= (j + k1) - RSRaster.bottomY;
		if (i < RSRaster.topX) {
			int l2 = RSRaster.topX - i;
			l1 -= l2;
			i = RSRaster.topX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (i + l1 > RSRaster.bottomX) {
			int i3 = (i + l1) - RSRaster.bottomX;
			l1 -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(l1 <= 0 || k1 <= 0)) {
			drawAlphaSprite(j1, l1, RSRaster.pixels, myPixels, j2, k1, i2, k, i1);
		}
	}

	private void drawAlphaSprite(int i, int j, int ai[], int ai1[], int l, int i1, int j1, int k1, int l1) {
		int k;// was parameter
		int j2;
		for (int k2 = -i1; k2 < 0; k2++) {
			for (int l2 = -j; l2 < 0; l2++) {
				k1 = ((myPixels[i] >> 24) & 255);
				j2 = 256 - k1;
				k = ai1[i++];
				if (k != 0) {
					int i3 = ai[l1];
					ai[l1++] = ((k & 0xff00ff) * k1 + (i3 & 0xff00ff) * j2 & 0xff00ff00)
							+ ((k & 0xff00) * k1 + (i3 & 0xff00) * j2 & 0xff0000) >> 8;
				} else {
					l1++;
				}
			}

			l1 += j1;
			i += l;
		}
	}
	
	public DirectImage(String img) {
		try {			
			Image image = Toolkit.getDefaultToolkit().getImage(Signlink.cacheLocation()+"sprites/" + img + ".png");
			ImageIcon sprite = new ImageIcon(image);
			myWidth = (short) sprite.getIconWidth();
			myHeight = (short) sprite.getIconHeight();
			maxWidth = myWidth;
			maxHeight = myHeight;
			drawOffsetX = 0;
			drawOffsetY = 0;
			myPixels = new int[myWidth * myHeight];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, myWidth, myHeight, myPixels, 0, myWidth);
			pixelgrabber.grabPixels();
			image = null;
			setTransparency(255, 0, 255);				
		} catch(Exception e) {
			System.out.println(e);
		}
	}
		
	
	public DirectImage(String img, int width, int height) {
		try {
			String ext = ".png";
			Image image = Toolkit.getDefaultToolkit().createImage(FileOperations.ReadFile(Signlink.cacheLocation()+"sprites/" + img + ext));
			myWidth = (short) width;
			myHeight = (short) height;
			maxWidth = myWidth;
			maxHeight = myHeight;
			drawOffsetX = 0;
			drawOffsetY = 0;
			myPixels = new int[myWidth * myHeight];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, myWidth, myHeight, myPixels, 0, myWidth);
			pixelgrabber.grabPixels();
			image = null;
			setTransparency(255, 0, 255);
		} catch (Exception _ex) {
			System.out.println(_ex);
		}
	}
		
	public void setTransparency(int transRed, int transGreen, int transBlue) {
		for (int index = 0; index < myPixels.length; index++)
			if (((myPixels[index] >> 16) & 255) == transRed && ((myPixels[index] >> 8) & 255) == transGreen && (myPixels[index] & 255) == transBlue)
				myPixels[index] = 0;
	}
	
	public DirectImage(CacheArchive streamLoader, String s, int i) {
		RSBuffer stream = new RSBuffer(streamLoader.getDataForName(s + ".dat"));
		RSBuffer stream_1 = new RSBuffer(streamLoader.getDataForName("index.dat"));
		stream_1.pointer = stream.readUShort();
		maxWidth = (short) stream_1.readUShort();
		maxHeight = (short) stream_1.readUShort();
		int j = stream_1.readUByte();
		int ai[] = new int[j];
		for (int k = 0; k < j - 1; k++) {
			ai[k + 1] = stream_1.read24Int();
			if (ai[k + 1] == 0)
				ai[k + 1] = 1;
		}
		for (int l = 0; l < i; l++) {
			stream_1.pointer += 2;
			stream.pointer += stream_1.readUShort() * stream_1.readUShort();
			stream_1.pointer++;
		}
		drawOffsetX = (byte) stream_1.readUByte();
		drawOffsetY = (byte) stream_1.readUByte();
		myWidth = (short) stream_1.readUShort();
		myHeight = (short) stream_1.readUShort();
		int i1 = stream_1.readUByte();
		int j1 = myWidth * myHeight;
		myPixels = new int[j1];
		if (i1 == 0) {
			for (int k1 = 0; k1 < j1; k1++)
				myPixels[k1] = ai[stream.readUByte()];
			return;
		}
		if (i1 == 1) {
			for (int l1 = 0; l1 < myWidth; l1++) {
				for (int i2 = 0; i2 < myHeight; i2++)
					myPixels[l1 + i2 * myWidth] = ai[stream.readUByte()];
			}
		}
	}

	public void method343() {
		RSRaster.initDrawingArea(myHeight, myWidth, myPixels);
	}

	public void shiftColours(int r, int g, int b) {
		for (int i1 = 0; i1 < myPixels.length; i1++) {
			int j1 = myPixels[i1];
			if (j1 != 0) {
				int k1 = j1 >> 16 & 0xff;
				k1 += r;
				if (k1 < 1)
					k1 = 1;
				else if (k1 > 255)
					k1 = 255;
				int l1 = j1 >> 8 & 0xff;
				l1 += g;
				if (l1 < 1)
					l1 = 1;
				else if (l1 > 255)
					l1 = 255;
				int i2 = j1 & 0xff;
				i2 += b;
				if (i2 < 1)
					i2 = 1;
				else if (i2 > 255)
					i2 = 255;
				myPixels[i1] = (k1 << 16) + (l1 << 8) + i2;
			}
		}
	}

	public void method345() {
		try {
			int totalPixels[] = new int[maxWidth * maxHeight];
			for (int h = 0; h < myHeight; h++) {
				for (int w = 0; w < myWidth; w++) {
					totalPixels[(h + drawOffsetY) * maxWidth + (w + drawOffsetX)] = myPixels[h * myWidth + w];
				}
			}
			myPixels = totalPixels;
			myWidth = maxWidth;
			myHeight = maxHeight;
			drawOffsetX = 0;
			drawOffsetY = 0;
			return;
		} catch (RuntimeException runtimeexception) {
			System.err.println("26341, " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	public void method346(int i, int j) {
		i += drawOffsetX;
		j += drawOffsetY;
		int l = i + j * RSRaster.width;
		int i1 = 0;
		int j1 = myHeight;
		int k1 = myWidth;
		int l1 = RSRaster.width - k1;
		int i2 = 0;
		if (j < RSRaster.topY) {
			int j2 = RSRaster.topY - j;
			j1 -= j2;
			j = RSRaster.topY;
			i1 += j2 * k1;
			l += j2 * RSRaster.width;
		}
		if (j + j1 > RSRaster.bottomY)
			j1 -= (j + j1) - RSRaster.bottomY;
		if (i < RSRaster.topX) {
			int k2 = RSRaster.topX - i;
			k1 -= k2;
			i = RSRaster.topX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (i + k1 > RSRaster.bottomX) {
			int l2 = (i + k1) - RSRaster.bottomX;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (k1 <= 0 || j1 <= 0) {
		} else {
			method347(l, k1, j1, i2, i1, l1, myPixels, RSRaster.pixels);
		}
	}

	private void method347(int i, int j, int k, int l, int i1, int k1, int ai[], int ai1[]) {
		int l1 = -(j >> 2);
		j = -(j & 3);
		for (int i2 = -k; i2 < 0; i2++) {
			for (int j2 = l1; j2 < 0; j2++) {
				ai1[i++] = ai[i1++];
				ai1[i++] = ai[i1++];
				ai1[i++] = ai[i1++];
				ai1[i++] = ai[i1++];
			}
			for (int k2 = j; k2 < 0; k2++)
				ai1[i++] = ai[i1++];
			i += k1;
			i1 += l;
		}
	}

	public void drawSpriteWithOpacity(int xPos, int yPos, int o) {
		int opacity = o;
		xPos += drawOffsetX;
		yPos += drawOffsetY;
		int i1 = xPos + yPos * RSRaster.width;
		int j1 = 0;
		int k1 = myHeight;
		int l1 = myWidth;
		int i2 = RSRaster.width - l1;
		int j2 = 0;
		if (yPos < RSRaster.topY) {
			int k2 = RSRaster.topY - yPos;
			k1 -= k2;
			yPos = RSRaster.topY;
			j1 += k2 * l1;
			i1 += k2 * RSRaster.width;
		}
		if (yPos + k1 > RSRaster.bottomY)
			k1 -= (yPos + k1) - RSRaster.bottomY;
		if (xPos < RSRaster.topX) {
			int l2 = RSRaster.topX - xPos;
			l1 -= l2;
			xPos = RSRaster.topX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (xPos + l1 > RSRaster.bottomX) {
			int i3 = (xPos + l1) - RSRaster.bottomX;
			l1 -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(l1 <= 0 || k1 <= 0)) {
			method351(j1, l1, RSRaster.pixels, myPixels, j2, k1, i2, opacity, i1);
		}
	}

	public void drawSprite1(int i, int j) {
		int k = 128;
		i += drawOffsetX;
		j += drawOffsetY;
		int i1 = i + j * RSRaster.width;
		int j1 = 0;
		int k1 = myHeight;
		int l1 = myWidth;
		int i2 = RSRaster.width - l1;
		int j2 = 0;
		if (j < RSRaster.topY) {
			int k2 = RSRaster.topY - j;
			k1 -= k2;
			j = RSRaster.topY;
			j1 += k2 * l1;
			i1 += k2 * RSRaster.width;
		}
		if (j + k1 > RSRaster.bottomY)
			k1 -= (j + k1) - RSRaster.bottomY;
		if (i < RSRaster.topX) {
			int l2 = RSRaster.topX - i;
			l1 -= l2;
			i = RSRaster.topX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (i + l1 > RSRaster.bottomX) {
			int i3 = (i + l1) - RSRaster.bottomX;
			l1 -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(l1 <= 0 || k1 <= 0)) {
			method351(j1, l1, RSRaster.pixels, myPixels, j2, k1, i2, k, i1);
		}
	}

	public void drawSpriteTrans(int i, int j, int k) {
		i += drawOffsetX;
		j += drawOffsetY;
		int i1 = i + j * RSRaster.width;
		int j1 = 0;
		int k1 = myHeight;
		int l1 = myWidth;
		int i2 = RSRaster.width - l1;
		int j2 = 0;
		if (j < RSRaster.topY) {
			int k2 = RSRaster.topY - j;
			k1 -= k2;
			j = RSRaster.topY;
			j1 += k2 * l1;
			i1 += k2 * RSRaster.width;
		}
		if (j + k1 > RSRaster.bottomY)
			k1 -= (j + k1) - RSRaster.bottomY;
		if (i < RSRaster.topX) {
			int l2 = RSRaster.topX - i;
			l1 -= l2;
			i = RSRaster.topX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (i + l1 > RSRaster.bottomX) {
			int i3 = (i + l1) - RSRaster.bottomX;
			l1 -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(l1 <= 0 || k1 <= 0)) {
			method351(j1, l1, RSRaster.pixels, myPixels, j2, k1, i2, k, i1);
		}
	}

	public void drawSprite(int i, int k) {
		i += drawOffsetX;
		k += drawOffsetY;
		int l = i + k * RSRaster.width;
		int i1 = 0;
		int j1 = myHeight;
		int k1 = myWidth;
		int l1 = RSRaster.width - k1;
		int i2 = 0;
		if (k < RSRaster.topY) {
			int j2 = RSRaster.topY - k;
			j1 -= j2;
			k = RSRaster.topY;
			i1 += j2 * k1;
			l += j2 * RSRaster.width;
		}
		if (k + j1 > RSRaster.bottomY)
			j1 -= (k + j1) - RSRaster.bottomY;
		if (i < RSRaster.topX) {
			int k2 = RSRaster.topX - i;
			k1 -= k2;
			i = RSRaster.topX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (i + k1 > RSRaster.bottomX) {
			int l2 = (i + k1) - RSRaster.bottomX;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (!(k1 <= 0 || j1 <= 0)) {
			method349(RSRaster.pixels, myPixels, i1, l, k1, j1, l1, i2);
		}
	}

	private void method349(int ai[], int ai1[], int j, int k, int l, int i1, int j1, int k1) {
		int i;
		int l1 = -(l >> 2);
		l = -(l & 3);
		for (int i2 = -i1; i2 < 0; i2++) {
			for (int j2 = l1; j2 < 0; j2++) {
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
			}
			for (int k2 = l; k2 < 0; k2++) {
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
			}
			k += j1;
			j += k1;
		}
	}

	private void method351(int i, int j, int ai[], int ai1[], int l, int i1, int j1, int k1, int l1) {
		int k;
		int j2 = 256 - k1;
		for (int k2 = -i1; k2 < 0; k2++) {
			for (int l2 = -j; l2 < 0; l2++) {
				k = ai1[i++];
				if (k != 0) {
					int i3 = ai[l1];
					ai[l1++] = ((k & 0xff00ff) * k1 + (i3 & 0xff00ff) * j2 & 0xff00ff00)
							+ ((k & 0xff00) * k1 + (i3 & 0xff00) * j2 & 0xff0000) >> 8;
				} else {
					l1++;
				}
			}
			l1 += j1;
			i += l;
		}
	}

	public void method352(int i, int j, int ai[], int k, int ai1[], int i1, int j1, int k1, int l1, int i2) {
		try {
			int j2 = -l1 / 2;
			int k2 = -i / 2;
			int l2 = (int) (Math.sin((double) j / 326.11000000000001D) * 65536D);
			int i3 = (int) (Math.cos((double) j / 326.11000000000001D) * 65536D);
			l2 = l2 * k >> 8;
			i3 = i3 * k >> 8;
			int j3 = (i2 << 16) + (k2 * l2 + j2 * i3);
			int k3 = (i1 << 16) + (k2 * i3 - j2 * l2);
			int l3 = k1 + j1 * RSRaster.width;
			for (j1 = 0; j1 < i; j1++) {
				int i4 = ai1[j1];
				int j4 = l3 + i4;
				int k4 = j3 + i3 * i4;
				int l4 = k3 - l2 * i4;
				for (k1 = -ai[j1]; k1 < 0; k1++) {
					RSRaster.pixels[j4++] = myPixels[(k4 >> 16) + (l4 >> 16) * myWidth];
					k4 += i3;
					l4 -= l2;
				}
				j3 += l2;
				k3 += i3;
				l3 += RSRaster.width;
			}
		} catch (Exception _ex) {
		}
	}

	public void method353(int i, double d, int l1) {
		int j = 15;
		int k = 20;
		int l = 15;
		int j1 = 256;
		int k1 = 20;
		try {
			int i2 = -k / 2;
			int j2 = -k1 / 2;
			int k2 = (int) (Math.sin(d) * 65536D);
			int l2 = (int) (Math.cos(d) * 65536D);
			k2 = k2 * j1 >> 8;
			l2 = l2 * j1 >> 8;
			int i3 = (l << 16) + (j2 * k2 + i2 * l2);
			int j3 = (j << 16) + (j2 * l2 - i2 * k2);
			int k3 = l1 + i * RSRaster.width;
			for (i = 0; i < k1; i++) {
				int l3 = k3;
				int i4 = i3;
				int j4 = j3;
				for (l1 = -k; l1 < 0; l1++) {
					int k4 = myPixels[(i4 >> 16) + (j4 >> 16) * myWidth];
					if (k4 != 0)
						RSRaster.pixels[l3++] = k4;
					else
						l3++;
					i4 += l2;
					j4 -= k2;
				}
				i3 += k2;
				j3 += l2;
				k3 += RSRaster.width;
			}
		} catch (Exception _ex) {
		}
	}

	public void method354(IndexedImage background, int i, int j) {
		j += drawOffsetX;
		i += drawOffsetY;
		int k = j + i * RSRaster.width;
		int l = 0;
		int i1 = myHeight;
		int j1 = myWidth;
		int k1 = RSRaster.width - j1;
		int l1 = 0;
		if (i < RSRaster.topY) {
			int i2 = RSRaster.topY - i;
			i1 -= i2;
			i = RSRaster.topY;
			l += i2 * j1;
			k += i2 * RSRaster.width;
		}
		if (i + i1 > RSRaster.bottomY)
			i1 -= (i + i1) - RSRaster.bottomY;
		if (j < RSRaster.topX) {
			int j2 = RSRaster.topX - j;
			j1 -= j2;
			j = RSRaster.topX;
			l += j2;
			k += j2;
			l1 += j2;
			k1 += j2;
		}
		if (j + j1 > RSRaster.bottomX) {
			int k2 = (j + j1) - RSRaster.bottomX;
			j1 -= k2;
			l1 += k2;
			k1 += k2;
		}
		if (!(j1 <= 0 || i1 <= 0)) {
			method355(myPixels, j1, background.imgPixels, i1, RSRaster.pixels, 0, k1, k, l1, l);
		}
	}

	public void mirrorHorizontal() {
		int newPixels[] = new int[myWidth * myHeight];
		int j = 0;
		for (int k = 0; k < myHeight; k++) {
			for (int l = myWidth - 1; l >= 0; l--)
				newPixels[j++] = myPixels[l + k * myWidth];
		}
		myPixels = newPixels;
		drawOffsetX = (byte) (maxWidth - myWidth - drawOffsetX);
	}

	public void mirrorVertical() {
		int newPixels[] = new int[myWidth * myHeight];
		int i = 0;
		for (int j = myHeight - 1; j >= 0; j--) {
			for (int k = 0; k < myWidth; k++)
				newPixels[i++] = myPixels[k + j * myWidth];
		}
		myPixels = newPixels;
		drawOffsetY = (byte) (maxHeight - myHeight - drawOffsetY);
	}

	private void method355(int ai[], int i, byte abyte0[], int j, int ai1[], int k, int l, int i1, int j1, int k1) {
		int l1 = -(i >> 2);
		i = -(i & 3);
		for (int j2 = -j; j2 < 0; j2++) {
			for (int k2 = l1; k2 < 0; k2++) {
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0)
					ai1[i1++] = k;
				else
					i1++;
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0)
					ai1[i1++] = k;
				else
					i1++;
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0)
					ai1[i1++] = k;
				else
					i1++;
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0)
					ai1[i1++] = k;
				else
					i1++;
			}
			for (int l2 = i; l2 < 0; l2++) {
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0)
					ai1[i1++] = k;
				else
					i1++;
			}
			i1 += l;
			k1 += j1;
		}
	}

	public int myPixels[];
	public short myWidth;
	public short myHeight;
	private byte drawOffsetX;
	private byte drawOffsetY;
	public short maxWidth;
	public short maxHeight;
}
