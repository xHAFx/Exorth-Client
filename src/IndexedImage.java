public final class IndexedImage extends RSRaster {

	public IndexedImage(CacheArchive streamLoader, String s, int i) {
		RSBuffer stream = new RSBuffer(streamLoader.getDataForName(s + ".dat"));
		RSBuffer stream_1 = new RSBuffer(streamLoader.getDataForName("index.dat"));
		stream_1.pointer = stream.readUShort();
		maxWidth = (short) stream_1.readUShort();
		maxHeight = (short) stream_1.readUShort();
		int j = stream_1.readUByte();
		palette = new int[j];
		for (int k = 0; k < j - 1; k++)
			palette[k + 1] = stream_1.read24Int();
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
		imgPixels = new byte[j1];
		if (i1 == 0) {
			for (int k1 = 0; k1 < j1; k1++)
				imgPixels[k1] = stream.readByte();
			return;
		}
		if (i1 == 1) {
			for (int l1 = 0; l1 < myWidth; l1++) {
				for (int i2 = 0; i2 < myHeight; i2++)
					imgPixels[l1 + i2 * myWidth] = stream.readByte();
			}
		}
	}

	public void resizeToHalf() {
		maxWidth /= 2;
		maxHeight /= 2;
		byte abyte0[] = new byte[maxWidth * maxHeight];
		int i = 0;
		for (int j = 0; j < myHeight; j++) {
			for (int k = 0; k < myWidth; k++)
				abyte0[(k + drawOffsetX >> 1) + (j + drawOffsetY >> 1) * maxWidth] = imgPixels[i++];
		}
		imgPixels = abyte0;
		myWidth = maxWidth;
		myHeight = maxHeight;
		drawOffsetX = 0;
		drawOffsetY = 0;
	}

	public void resizeToOriginal() {
		if (myWidth == maxWidth && myHeight == maxHeight)
			return;
		byte abyte0[] = new byte[maxWidth * maxHeight];
		int i = 0;
		for (int j = 0; j < myHeight; j++) {
			for (int k = 0; k < myWidth; k++)
				abyte0[k + drawOffsetX + (j + drawOffsetY) * maxWidth] = imgPixels[i++];
		}
		imgPixels = abyte0;
		myWidth = maxWidth;
		myHeight = maxHeight;
		drawOffsetX = 0;
		drawOffsetY = 0;
	}

	public void flipHorizontal() {
		byte abyte0[] = new byte[myWidth * myHeight];
		int j = 0;
		for (int k = 0; k < myHeight; k++) {
			for (int l = myWidth - 1; l >= 0; l--)
				abyte0[j++] = imgPixels[l + k * myWidth];
		}
		imgPixels = abyte0;
		drawOffsetX = (byte) (maxWidth - myWidth - drawOffsetX);
	}

	public void flipVertical() {
		byte abyte0[] = new byte[myWidth * myHeight];
		int i = 0;
		for (int j = myHeight - 1; j >= 0; j--) {
			for (int k = 0; k < myWidth; k++)
				abyte0[i++] = imgPixels[k + j * myWidth];
		}
		imgPixels = abyte0;
		drawOffsetY = (byte) (maxHeight - myHeight - drawOffsetY);
	}

	public void shiftColours(int i, int j, int k) {
		for (int i1 = 0; i1 < palette.length; i1++) {
			int j1 = palette[i1] >> 16 & 0xff;
			j1 += i;
			if (j1 < 0)
				j1 = 0;
			else if (j1 > 255)
				j1 = 255;
			int k1 = palette[i1] >> 8 & 0xff;
			k1 += j;
			if (k1 < 0)
				k1 = 0;
			else if (k1 > 255)
				k1 = 255;
			int l1 = palette[i1] & 0xff;
			l1 += k;
			if (l1 < 0)
				l1 = 0;
			else if (l1 > 255)
				l1 = 255;
			palette[i1] = (j1 << 16) + (k1 << 8) + l1;
		}
	}

	public void drawIndexedImage(int i, int k) {
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
			arraycopy(j1, RSRaster.pixels, imgPixels, l1, l, k1, i1, palette, i2);
		}
	}

	private void arraycopy(int i, int ai[], byte abyte0[], int j, int k, int l, int i1, int ai1[], int j1) {
		int k1 = -(l >> 2);
		l = -(l & 3);
		for (int l1 = -i; l1 < 0; l1++) {
			for (int i2 = k1; i2 < 0; i2++) {
				byte byte1 = abyte0[i1++];
				if (byte1 != 0)
					ai[k++] = ai1[byte1 & 0xff];
				else
					k++;
				byte1 = abyte0[i1++];
				if (byte1 != 0)
					ai[k++] = ai1[byte1 & 0xff];
				else
					k++;
				byte1 = abyte0[i1++];
				if (byte1 != 0)
					ai[k++] = ai1[byte1 & 0xff];
				else
					k++;
				byte1 = abyte0[i1++];
				if (byte1 != 0)
					ai[k++] = ai1[byte1 & 0xff];
				else
					k++;
			}
			for (int j2 = l; j2 < 0; j2++) {
				byte byte2 = abyte0[i1++];
				if (byte2 != 0)
					ai[k++] = ai1[byte2 & 0xff];
				else
					k++;
			}
			k += j;
			i1 += j1;
		}
	}

	public byte imgPixels[];
	public final int[] palette;
	public short myWidth;
	public short myHeight;
	public byte drawOffsetX;
	public byte drawOffsetY;
	public short maxWidth;
	public short maxHeight;
}
