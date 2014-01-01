public final class Rasterizer extends RSRaster {

	public static void clearCache() {
		anIntArray1468 = null;
		anIntArray1468 = null;
		sineTable = null;
		cosineTable = null;
		lineOffsets = null;
		textureImages = null;
		aBooleanArray1475 = null;
		anIntArray1476 = null;
		anIntArrayArray1478 = null;
		anIntArrayArray1479 = null;
		textureLastUsed = null;
		hsl2rgb = null;
		anIntArrayArray1483 = null;
	}

	public static void method364() {
		lineOffsets = new int[height];
		for (int j = 0; j < height; j++)
			lineOffsets[j] = width * j;
		textureInt1 = width / 2;
		textureInt2 = height / 2;
	}

	public static void method365(int j, int k) {
		try {
			lineOffsets = new int[k];
			for (int l = 0; l < k; l++)
				lineOffsets[l] = j * l;
			textureInt1 = j / 2;
			textureInt2 = k / 2;
		} catch (Exception e) {
		}
	}

	public static void method366() {
		anIntArrayArray1478 = null;
		for (int j = 0; j < 50; j++)
			anIntArrayArray1479[j] = null;
	}

	public static void method367() {
		if (anIntArrayArray1478 == null) {
			anInt1477 = 20;// was parameter
			if (lowMem)
				anIntArrayArray1478 = new int[anInt1477][16384];
			else
				anIntArrayArray1478 = new int[anInt1477][0x10000];
			for (int k = 0; k < 50; k++)
				anIntArrayArray1479[k] = null;
		}
	}

	public static void unpack(CacheArchive streamLoader) {
		anInt1473 = 0;
		for (int j = 0; j < 50; j++)
			try {
				textureImages[j] = new IndexedImage(streamLoader, String.valueOf(j), 0);
				if (lowMem && textureImages[j].maxWidth == 128)
					textureImages[j].resizeToHalf();
				else
					textureImages[j].resizeToOriginal();
				anInt1473++;
			} catch (Exception _ex) {
			}
	}

	public static int method369(int i) {
		if (anIntArray1476[i] != 0)
			return anIntArray1476[i];
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = anIntArrayArray1483[i].length;
		for (int k1 = 0; k1 < j1; k1++) {
			k += anIntArrayArray1483[i][k1] >> 16 & 0xff;
			l += anIntArrayArray1483[i][k1] >> 8 & 0xff;
			i1 += anIntArrayArray1483[i][k1] & 0xff;
		}
		int l1 = (k / j1 << 16) + (l / j1 << 8) + i1 / j1;
		l1 = method373(l1, 1.3999999999999999D);
		if (l1 == 0)
			l1 = 1;
		anIntArray1476[i] = l1;
		return l1;
	}

	public static void resetTexture(int i) {
		if (anIntArrayArray1479[i] == null)
			return;
		anIntArrayArray1478[anInt1477++] = anIntArrayArray1479[i];
		anIntArrayArray1479[i] = null;
	}

	private static int[] method371(int i) {
		if (i == 1)
			i = 24;
		textureLastUsed[i] = anInt1481++;
		if (anIntArrayArray1479[i] != null)
			return anIntArrayArray1479[i];
		int ai[];
		if (anInt1477 > 0) {
			ai = anIntArrayArray1478[--anInt1477];
			anIntArrayArray1478[anInt1477] = null;
		} else {
			int j = 0;
			int k = -1;
			for (int l = 0; l < anInt1473; l++)
				if (anIntArrayArray1479[l] != null && (textureLastUsed[l] < j || k == -1)) {
					j = textureLastUsed[l];
					k = l;
				}
			ai = anIntArrayArray1479[k];
			anIntArrayArray1479[k] = null;
		}
		anIntArrayArray1479[i] = ai;
		IndexedImage background = textureImages[i];
		int ai1[] = anIntArrayArray1483[i];
		if (lowMem) {
			aBooleanArray1475[i] = false;
			for (int i1 = 0; i1 < 4096; i1++) {
				int i2 = ai[i1] = ai1[background.imgPixels[i1]] & 0xf8f8ff;
				if (i2 == 0)
					aBooleanArray1475[i] = true;
				ai[4096 + i1] = i2 - (i2 >>> 3) & 0xf8f8ff;
				ai[8192 + i1] = i2 - (i2 >>> 2) & 0xf8f8ff;
				ai[12288 + i1] = i2 - (i2 >>> 2) - (i2 >>> 3) & 0xf8f8ff;
			}
		} else {
			if (background.myWidth == 64) {
				for (int j1 = 0; j1 < 128; j1++) {
					for (int j2 = 0; j2 < 128; j2++)
						ai[j2 + (j1 << 7)] = ai1[background.imgPixels[(j2 >> 1) + ((j1 >> 1) << 6)]];
				}
			} else {
				for (int k1 = 0; k1 < 16384; k1++)
					ai[k1] = ai1[background.imgPixels[k1]];
			}
			aBooleanArray1475[i] = false;
			for (int l1 = 0; l1 < 16384; l1++) {
				ai[l1] &= 0xf8f8ff;
				int k2 = ai[l1];
				if (k2 == 0)
					aBooleanArray1475[i] = true;
				ai[16384 + l1] = k2 - (k2 >>> 3) & 0xf8f8ff;
				ai[32768 + l1] = k2 - (k2 >>> 2) & 0xf8f8ff;
				ai[49152 + l1] = k2 - (k2 >>> 2) - (k2 >>> 3) & 0xf8f8ff;
			}
		}
		return ai;
	}

	public static void method372(double d) {
		int j = 0;
		for (int k = 0; k < 512; k++) {
			double d1 = (double) (k / 8) / 64D + 0.0078125D;
			double d2 = (double) (k & 7) / 8D + 0.0625D;
			for (int k1 = 0; k1 < 128; k1++) {
				double d3 = (double) k1 / 128D;
				double d4 = d3;
				double d5 = d3;
				double d6 = d3;
				if (d2 != 0.0D) {
					double d7;
					if (d3 < 0.5D)
						d7 = d3 * (1.0D + d2);
					else
						d7 = (d3 + d2) - d3 * d2;
					double d8 = 2D * d3 - d7;
					double d9 = d1 + 0.33333333333333331D;
					if (d9 > 1.0D)
						d9--;
					double d10 = d1;
					double d11 = d1 - 0.33333333333333331D;
					if (d11 < 0.0D)
						d11++;
					if (6D * d9 < 1.0D)
						d4 = d8 + (d7 - d8) * 6D * d9;
					else if (2D * d9 < 1.0D)
						d4 = d7;
					else if (3D * d9 < 2D)
						d4 = d8 + (d7 - d8) * (0.66666666666666663D - d9) * 6D;
					else
						d4 = d8;
					if (6D * d10 < 1.0D)
						d5 = d8 + (d7 - d8) * 6D * d10;
					else if (2D * d10 < 1.0D)
						d5 = d7;
					else if (3D * d10 < 2D)
						d5 = d8 + (d7 - d8) * (0.66666666666666663D - d10) * 6D;
					else
						d5 = d8;
					if (6D * d11 < 1.0D)
						d6 = d8 + (d7 - d8) * 6D * d11;
					else if (2D * d11 < 1.0D)
						d6 = d7;
					else if (3D * d11 < 2D)
						d6 = d8 + (d7 - d8) * (0.66666666666666663D - d11) * 6D;
					else
						d6 = d8;
				}
				int l1 = (int) (d4 * 256D);
				int i2 = (int) (d5 * 256D);
				int j2 = (int) (d6 * 256D);
				int k2 = (l1 << 16) + (i2 << 8) + j2;
				k2 = method373(k2, d);
				if (k2 == 0)
					k2 = 1;
				hsl2rgb[j++] = k2;
			}
		}
		for (int l = 0; l < 50; l++)
			if (textureImages[l] != null) {
				int ai[] = textureImages[l].palette;
				anIntArrayArray1483[l] = new int[ai.length];
				for (int j1 = 0; j1 < ai.length; j1++) {
					anIntArrayArray1483[l][j1] = method373(ai[j1], d);
					if ((anIntArrayArray1483[l][j1] & 0xf8f8ff) == 0 && j1 != 0)
						anIntArrayArray1483[l][j1] = 1;
				}
			}
		for (int i1 = 0; i1 < 50; i1++)
			resetTexture(i1);
	}

	private static int method373(int i, double d) {
		double d1 = (double) (i >> 16) / 256D;
		double d2 = (double) (i >> 8 & 0xff) / 256D;
		double d3 = (double) (i & 0xff) / 256D;
		d1 = Math.pow(d1, d);
		d2 = Math.pow(d2, d);
		d3 = Math.pow(d3, d);
		int j = (int) (d1 * 256D);
		int k = (int) (d2 * 256D);
		int l = (int) (d3 * 256D);
		return (j << 16) + (k << 8) + l;
	}

	public static void method374(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int z_a, int z_b, int z_c, boolean chatHeadFix) {
		try {
			int x_a_off = 0;
			int z_a_off = 0;
			if (y_b != y_a) {
				x_a_off = (x_b - x_a << 16) / (y_b - y_a);
				z_a_off = (z_b - z_a << 15) / (y_b - y_a);
			}
			int x_b_off = 0;
			int z_b_off = 0;
			if (y_c != y_b) {
				x_b_off = (x_c - x_b << 16) / (y_c - y_b);
				z_b_off = (z_c - z_b << 15) / (y_c - y_b);
			}
			int x_c_off = 0;
			int z_c_off = 0;
			if (y_c != y_a) {
				x_c_off = (x_a - x_c << 16) / (y_a - y_c);
				z_c_off = (z_a - z_c << 15) / (y_a - y_c);
			}
			if (chatHeadFix) {
				if (y_a <= topY)
					y_a = topY;
				if (y_b <= topY)
					y_b = topY;
				if (y_c <= topY)
					y_c = topY;
			}
			if (y_a <= y_b && y_a <= y_c) {
				if (y_a >= bottomY)
					return;
				if (y_b > bottomY)
					y_b = bottomY;
				if (y_c > bottomY)
					y_c = bottomY;
				if (y_b < y_c) {
					x_c = x_a <<= 16;
					z_c = z_a <<= 15;
					if (y_a < 0) {
						x_c -= x_c_off * y_a;
						x_a -= x_a_off * y_a;
						z_c -= z_c_off * y_a;
						z_a -= z_a_off * y_a;
						y_a = 0;
					}
					x_b <<= 16;
					z_b <<= 15;
					if (y_b < 0) {
						x_b -= x_b_off * y_b;
						z_b -= z_b_off * y_b;
						y_b = 0;
					}
					if (y_a != y_b && x_c_off < x_a_off || y_a == y_b && x_c_off > x_b_off) {
						y_c -= y_b;
						y_b -= y_a;
						for (y_a = lineOffsets[y_a]; --y_b >= 0; y_a += width) {
							drawShadedLine(pixels, y_a, x_c >> 16, x_a >> 16, z_c >> 7, z_a >> 7, chatHeadFix);
							x_c += x_c_off;
							x_a += x_a_off;
							z_c += z_c_off;
							z_a += z_a_off;
						}
						while (--y_c >= 0) {
							drawShadedLine(pixels, y_a, x_c >> 16, x_b >> 16, z_c >> 7, z_b >> 7, chatHeadFix);
							x_c += x_c_off;
							x_b += x_b_off;
							z_c += z_c_off;
							z_b += z_b_off;
							y_a += width;
						}
						return;
					}
					y_c -= y_b;
					y_b -= y_a;
					for (y_a = lineOffsets[y_a]; --y_b >= 0; y_a += width) {
						drawShadedLine(pixels, y_a, x_a >> 16, x_c >> 16, z_a >> 7, z_c >> 7, chatHeadFix);
						x_c += x_c_off;
						x_a += x_a_off;
						z_c += z_c_off;
						z_a += z_a_off;
					}
					while (--y_c >= 0) {
						drawShadedLine(pixels, y_a, x_b >> 16, x_c >> 16, z_b >> 7, z_c >> 7, chatHeadFix);
						x_c += x_c_off;
						x_b += x_b_off;
						z_c += z_c_off;
						z_b += z_b_off;
						y_a += width;
					}
					return;
				}
				x_b = x_a <<= 16;
				z_b = z_a <<= 15;
				if (y_a < 0) {
					x_b -= x_c_off * y_a;
					x_a -= x_a_off * y_a;
					z_b -= z_c_off * y_a;
					z_a -= z_a_off * y_a;
					y_a = 0;
				}
				x_c <<= 16;
				z_c <<= 15;
				if (y_c < 0) {
					x_c -= x_b_off * y_c;
					z_c -= z_b_off * y_c;
					y_c = 0;
				}
				if (y_a != y_c && x_c_off < x_a_off || y_a == y_c && x_b_off > x_a_off) {
					y_b -= y_c;
					y_c -= y_a;
					for (y_a = lineOffsets[y_a]; --y_c >= 0; y_a += width) {
						drawShadedLine(pixels, y_a, x_b >> 16, x_a >> 16, z_b >> 7, z_a >> 7, chatHeadFix);
						x_b += x_c_off;
						x_a += x_a_off;
						z_b += z_c_off;
						z_a += z_a_off;
					}
					while (--y_b >= 0) {
						drawShadedLine(pixels, y_a, x_c >> 16, x_a >> 16, z_c >> 7, z_a >> 7, chatHeadFix);
						x_c += x_b_off;
						x_a += x_a_off;
						z_c += z_b_off;
						z_a += z_a_off;
						y_a += width;
					}
					return;
				}
				y_b -= y_c;
				y_c -= y_a;
				for (y_a = lineOffsets[y_a]; --y_c >= 0; y_a += width) {
					drawShadedLine(pixels, y_a, x_a >> 16, x_b >> 16, z_a >> 7, z_b >> 7, chatHeadFix);
					x_b += x_c_off;
					x_a += x_a_off;
					z_b += z_c_off;
					z_a += z_a_off;
				}
				while (--y_b >= 0) {
					drawShadedLine(pixels, y_a, x_a >> 16, x_c >> 16, z_a >> 7, z_c >> 7, chatHeadFix);
					x_c += x_b_off;
					x_a += x_a_off;
					z_c += z_b_off;
					z_a += z_a_off;
					y_a += width;
				}
				return;
			}
			if (y_b <= y_c) {
				if (y_b >= bottomY)
					return;
				if (y_c > bottomY)
					y_c = bottomY;
				if (y_a > bottomY)
					y_a = bottomY;
				if (y_c < y_a) {
					x_a = x_b <<= 16;
					z_a = z_b <<= 15;
					if (y_b < 0) {
						x_a -= x_a_off * y_b;
						x_b -= x_b_off * y_b;
						z_a -= z_a_off * y_b;
						z_b -= z_b_off * y_b;
						y_b = 0;
					}
					x_c <<= 16;
					z_c <<= 15;
					if (y_c < 0) {
						x_c -= x_c_off * y_c;
						z_c -= z_c_off * y_c;
						y_c = 0;
					}
					if (y_b != y_c && x_a_off < x_b_off || y_b == y_c && x_a_off > x_c_off) {
						y_a -= y_c;
						y_c -= y_b;
						for (y_b = lineOffsets[y_b]; --y_c >= 0; y_b += width) {
							drawShadedLine(pixels, y_b, x_a >> 16, x_b >> 16, z_a >> 7, z_b >> 7, chatHeadFix);
							x_a += x_a_off;
							x_b += x_b_off;
							z_a += z_a_off;
							z_b += z_b_off;
						}
						while (--y_a >= 0) {
							drawShadedLine(pixels, y_b, x_a >> 16, x_c >> 16, z_a >> 7, z_c >> 7, chatHeadFix);
							x_a += x_a_off;
							x_c += x_c_off;
							z_a += z_a_off;
							z_c += z_c_off;
							y_b += width;
						}
						return;
					}
					y_a -= y_c;
					y_c -= y_b;
					for (y_b = lineOffsets[y_b]; --y_c >= 0; y_b += width) {
						drawShadedLine(pixels, y_b, x_b >> 16, x_a >> 16, z_b >> 7, z_a >> 7, chatHeadFix);
						x_a += x_a_off;
						x_b += x_b_off;
						z_a += z_a_off;
						z_b += z_b_off;
					}
					while (--y_a >= 0) {
						drawShadedLine(pixels, y_b, x_c >> 16, x_a >> 16, z_c >> 7, z_a >> 7, chatHeadFix);
						x_a += x_a_off;
						x_c += x_c_off;
						z_a += z_a_off;
						z_c += z_c_off;
						y_b += width;
					}
					return;
				}
				x_c = x_b <<= 16;
				z_c = z_b <<= 15;
				if (y_b < 0) {
					x_c -= x_a_off * y_b;
					x_b -= x_b_off * y_b;
					z_c -= z_a_off * y_b;
					z_b -= z_b_off * y_b;
					y_b = 0;
				}
				x_a <<= 16;
				z_a <<= 15;
				if (y_a < 0) {
					x_a -= x_c_off * y_a;
					z_a -= z_c_off * y_a;
					y_a = 0;
				}
				if (x_a_off < x_b_off) {
					y_c -= y_a;
					y_a -= y_b;
					for (y_b = lineOffsets[y_b]; --y_a >= 0; y_b += width) {
						drawShadedLine(pixels, y_b, x_c >> 16, x_b >> 16, z_c >> 7, z_b >> 7, chatHeadFix);
						x_c += x_a_off;
						x_b += x_b_off;
						z_c += z_a_off;
						z_b += z_b_off;
					}
					while (--y_c >= 0) {
						drawShadedLine(pixels, y_b, x_a >> 16, x_b >> 16, z_a >> 7, z_b >> 7, chatHeadFix);
						x_a += x_c_off;
						x_b += x_b_off;
						z_a += z_c_off;
						z_b += z_b_off;
						y_b += width;
					}
					return;
				}
				y_c -= y_a;
				y_a -= y_b;
				for (y_b = lineOffsets[y_b]; --y_a >= 0; y_b += width) {
					drawShadedLine(pixels, y_b, x_b >> 16, x_c >> 16, z_b >> 7, z_c >> 7, chatHeadFix);
					x_c += x_a_off;
					x_b += x_b_off;
					z_c += z_a_off;
					z_b += z_b_off;
				}
				while (--y_c >= 0) {
					drawShadedLine(pixels, y_b, x_b >> 16, x_a >> 16, z_b >> 7, z_a >> 7, chatHeadFix);
					x_a += x_c_off;
					x_b += x_b_off;
					z_a += z_c_off;
					z_b += z_b_off;
					y_b += width;
				}
				return;
			}
			if (y_c >= bottomY)
				return;
			if (y_a > bottomY)
				y_a = bottomY;
			if (y_b > bottomY)
				y_b = bottomY;
			if (y_a < y_b) {
				x_b = x_c <<= 16;
				z_b = z_c <<= 15;
				if (y_c < 0) {
					x_b -= x_b_off * y_c;
					x_c -= x_c_off * y_c;
					z_b -= z_b_off * y_c;
					z_c -= z_c_off * y_c;
					y_c = 0;
				}
				x_a <<= 16;
				z_a <<= 15;
				if (y_a < 0) {
					x_a -= x_a_off * y_a;
					z_a -= z_a_off * y_a;
					y_a = 0;
				}
				if (x_b_off < x_c_off) {
					y_b -= y_a;
					y_a -= y_c;
					for (y_c = lineOffsets[y_c]; --y_a >= 0; y_c += width) {
						drawShadedLine(pixels, y_c, x_b >> 16, x_c >> 16, z_b >> 7, z_c >> 7, chatHeadFix);
						x_b += x_b_off;
						x_c += x_c_off;
						z_b += z_b_off;
						z_c += z_c_off;
					}
					while (--y_b >= 0) {
						drawShadedLine(pixels, y_c, x_b >> 16, x_a >> 16, z_b >> 7, z_a >> 7, chatHeadFix);
						x_b += x_b_off;
						x_a += x_a_off;
						z_b += z_b_off;
						z_a += z_a_off;
						y_c += width;
					}
					return;
				}
				y_b -= y_a;
				y_a -= y_c;
				for (y_c = lineOffsets[y_c]; --y_a >= 0; y_c += width) {
					drawShadedLine(pixels, y_c, x_c >> 16, x_b >> 16, z_c >> 7, z_b >> 7, chatHeadFix);
					x_b += x_b_off;
					x_c += x_c_off;
					z_b += z_b_off;
					z_c += z_c_off;
				}
				while (--y_b >= 0) {
					drawShadedLine(pixels, y_c, x_a >> 16, x_b >> 16, z_a >> 7, z_b >> 7, chatHeadFix);
					x_b += x_b_off;
					x_a += x_a_off;
					z_b += z_b_off;
					z_a += z_a_off;
					y_c += width;
				}
				return;
			}
			x_a = x_c <<= 16;
			z_a = z_c <<= 15;
			if (y_c < 0) {
				x_a -= x_b_off * y_c;
				x_c -= x_c_off * y_c;
				z_a -= z_b_off * y_c;
				z_c -= z_c_off * y_c;
				y_c = 0;
			}
			x_b <<= 16;
			z_b <<= 15;
			if (y_b < 0) {
				x_b -= x_a_off * y_b;
				z_b -= z_a_off * y_b;
				y_b = 0;
			}
			if (x_b_off < x_c_off) {
				y_a -= y_b;
				y_b -= y_c;
				for (y_c = lineOffsets[y_c]; --y_b >= 0; y_c += width) {
					drawShadedLine(pixels, y_c, x_a >> 16, x_c >> 16, z_a >> 7, z_c >> 7, chatHeadFix);
					x_a += x_b_off;
					x_c += x_c_off;
					z_a += z_b_off;
					z_c += z_c_off;
				}
				while (--y_a >= 0) {
					drawShadedLine(pixels, y_c, x_b >> 16, x_c >> 16, z_b >> 7, z_c >> 7, chatHeadFix);
					x_b += x_a_off;
					x_c += x_c_off;
					z_b += z_a_off;
					z_c += z_c_off;
					y_c += width;
				}
				return;
			}
			y_a -= y_b;
			y_b -= y_c;
			for (y_c = lineOffsets[y_c]; --y_b >= 0; y_c += width) {
				drawShadedLine(pixels, y_c, x_c >> 16, x_a >> 16, z_c >> 7, z_a >> 7, chatHeadFix);
				x_a += x_b_off;
				x_c += x_c_off;
				z_a += z_b_off;
				z_c += z_c_off;
			}
			while (--y_a >= 0) {
				drawShadedLine(pixels, y_c, x_c >> 16, x_b >> 16, z_c >> 7, z_b >> 7, chatHeadFix);
				x_b += x_a_off;
				x_c += x_c_off;
				z_b += z_a_off;
				z_c += z_c_off;
				y_c += width;
			}
		} catch (Exception e) {
		}
	}

	public static void drawShadedLine(int[] ai, int i, int l, int i1, int j1, int k1, boolean chatHeadFix) {
		try {
			int j;
			int k;
			int l1 = 0;
			if (chatHeadFix) {
				if (i1 > RSRaster.centerX)
					i1 = RSRaster.centerX;
				if (l < 9) {
					l = 9;
				}
			}
			if (restrict_edges) {
				if (i1 > RSRaster.centerX)
					i1 = RSRaster.centerX;
				if (l < 0) {
					j1 -= l * l1;
					l = 0;
				}
			}
			if (l < i1) {
				i += l;
				j1 += l1 * l;
				if (notTextured) {
					k = i1 - l >> 2;
					if (k > 0)
						l1 = (k1 - j1) * anIntArray1468[k] >> 15;
					else
						l1 = 0;
					if (alpha == 0) {
						if (k > 0) {
							do {
								j = hsl2rgb[j1 >> 8];
								j1 += l1;
								ai[i++] = j;
								ai[i++] = j;
								ai[i++] = j;
								ai[i++] = j;
							} while (--k > 0);
						}
						k = i1 - l & 0x3;
						if (k > 0) {
							j = hsl2rgb[j1 >> 8];
							do
								ai[i++] = j;
							while (--k > 0);
						}
					} else {
						int j2 = alpha;
						int l2 = 256 - alpha;
						if (k > 0) {
							do {
								j = hsl2rgb[j1 >> 8];
								j1 += l1;
								j = (((j & 0xff00ff) * l2 >> 8 & 0xff00ff) + ((j & 0xff00) * l2 >> 8 & 0xff00));
								int h = ai[i];
								ai[i++] = (j + ((h & 0xff00ff) * j2 >> 8 & 0xff00ff) + ((h & 0xff00) * j2 >> 8 & 0xff00));
								h = ai[i];
								ai[i++] = (j + ((h & 0xff00ff) * j2 >> 8 & 0xff00ff) + ((h & 0xff00) * j2 >> 8 & 0xff00));
								h = ai[i];
								ai[i++] = (j + ((h & 0xff00ff) * j2 >> 8 & 0xff00ff) + ((h & 0xff00) * j2 >> 8 & 0xff00));
								h = ai[i];
								ai[i++] = (j + ((h & 0xff00ff) * j2 >> 8 & 0xff00ff) + ((h & 0xff00) * j2 >> 8 & 0xff00));
							} while (--k > 0);
						}
						k = i1 - l & 0x3;
						if (k > 0) {
							j = hsl2rgb[j1 >> 8];
							j = (((j & 0xff00ff) * l2 >> 8 & 0xff00ff) + ((j & 0xff00) * l2 >> 8 & 0xff00));
							do {
								int i_61_ = ai[i];
								ai[i++] = (j + ((i_61_ & 0xff00ff) * j2 >> 8 & 0xff00ff) + ((i_61_ & 0xff00) * j2 >> 8 & 0xff00));
							} while (--k > 0);
						}
					}
				} else {
					int i2 = (k1 - j1) / (i1 - l);
					k = i1 - l;
					if (alpha == 0) {
						do {
							ai[i++] = hsl2rgb[j1 >> 8];
							j1 += i2;
						} while (--k > 0);
					} else {
						int i_62_ = alpha;
						int i_63_ = 256 - alpha;
						do {
							j = hsl2rgb[j1 >> 8];
							j1 += i2;
							j = (((j & 0xff00ff) * i_63_ >> 8 & 0xff00ff) + ((j & 0xff00) * i_63_ >> 8 & 0xff00));
							int i_64_ = ai[i];
							ai[i++] = (j + ((i_64_ & 0xff00ff) * i_62_ >> 8 & 0xff00ff) + ((i_64_ & 0xff00) * i_62_ >> 8 & 0xff00));
						} while (--k > 0);
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public static void method376(int i, int j, int k, int l, int i1, int j1, int k1) {
		int l1 = 0;
		if (j != i)
			l1 = (i1 - l << 16) / (j - i);
		int i2 = 0;
		if (k != j)
			i2 = (j1 - i1 << 16) / (k - j);
		int j2 = 0;
		if (k != i)
			j2 = (l - j1 << 16) / (i - k);
		if (i <= j && i <= k) {
			if (i >= bottomY)
				return;
			if (j > bottomY)
				j = bottomY;
			if (k > bottomY)
				k = bottomY;
			if (j < k) {
				j1 = l <<= 16;
				if (i < 0) {
					j1 -= j2 * i;
					l -= l1 * i;
					i = 0;
				}
				i1 <<= 16;
				if (j < 0) {
					i1 -= i2 * j;
					j = 0;
				}
				if (i != j && j2 < l1 || i == j && j2 > i2) {
					k -= j;
					j -= i;
					for (i = lineOffsets[i]; --j >= 0; i += width) {
						method377(pixels, i, k1, j1 >> 16, l >> 16);
						j1 += j2;
						l += l1;
					}
					while (--k >= 0) {
						method377(pixels, i, k1, j1 >> 16, i1 >> 16);
						j1 += j2;
						i1 += i2;
						i += width;
					}
					return;
				}
				k -= j;
				j -= i;
				for (i = lineOffsets[i]; --j >= 0; i += width) {
					method377(pixels, i, k1, l >> 16, j1 >> 16);
					j1 += j2;
					l += l1;
				}
				while (--k >= 0) {
					method377(pixels, i, k1, i1 >> 16, j1 >> 16);
					j1 += j2;
					i1 += i2;
					i += width;
				}
				return;
			}
			i1 = l <<= 16;
			if (i < 0) {
				i1 -= j2 * i;
				l -= l1 * i;
				i = 0;
			}
			j1 <<= 16;
			if (k < 0) {
				j1 -= i2 * k;
				k = 0;
			}
			if (i != k && j2 < l1 || i == k && i2 > l1) {
				j -= k;
				k -= i;
				for (i = lineOffsets[i]; --k >= 0; i += width) {
					method377(pixels, i, k1, i1 >> 16, l >> 16);
					i1 += j2;
					l += l1;
				}
				while (--j >= 0) {
					method377(pixels, i, k1, j1 >> 16, l >> 16);
					j1 += i2;
					l += l1;
					i += width;
				}
				return;
			}
			j -= k;
			k -= i;
			for (i = lineOffsets[i]; --k >= 0; i += width) {
				method377(pixels, i, k1, l >> 16, i1 >> 16);
				i1 += j2;
				l += l1;
			}
			while (--j >= 0) {
				method377(pixels, i, k1, l >> 16, j1 >> 16);
				j1 += i2;
				l += l1;
				i += width;
			}
			return;
		}
		if (j <= k) {
			if (j >= bottomY)
				return;
			if (k > bottomY)
				k = bottomY;
			if (i > bottomY)
				i = bottomY;
			if (k < i) {
				l = i1 <<= 16;
				if (j < 0) {
					l -= l1 * j;
					i1 -= i2 * j;
					j = 0;
				}
				j1 <<= 16;
				if (k < 0) {
					j1 -= j2 * k;
					k = 0;
				}
				if (j != k && l1 < i2 || j == k && l1 > j2) {
					i -= k;
					k -= j;
					for (j = lineOffsets[j]; --k >= 0; j += width) {
						method377(pixels, j, k1, l >> 16, i1 >> 16);
						l += l1;
						i1 += i2;
					}
					while (--i >= 0) {
						method377(pixels, j, k1, l >> 16, j1 >> 16);
						l += l1;
						j1 += j2;
						j += width;
					}
					return;
				}
				i -= k;
				k -= j;
				for (j = lineOffsets[j]; --k >= 0; j += width) {
					method377(pixels, j, k1, i1 >> 16, l >> 16);
					l += l1;
					i1 += i2;
				}
				while (--i >= 0) {
					method377(pixels, j, k1, j1 >> 16, l >> 16);
					l += l1;
					j1 += j2;
					j += width;
				}
				return;
			}
			j1 = i1 <<= 16;
			if (j < 0) {
				j1 -= l1 * j;
				i1 -= i2 * j;
				j = 0;
			}
			l <<= 16;
			if (i < 0) {
				l -= j2 * i;
				i = 0;
			}
			if (l1 < i2) {
				k -= i;
				i -= j;
				for (j = lineOffsets[j]; --i >= 0; j += width) {
					method377(pixels, j, k1, j1 >> 16, i1 >> 16);
					j1 += l1;
					i1 += i2;
				}
				while (--k >= 0) {
					method377(pixels, j, k1, l >> 16, i1 >> 16);
					l += j2;
					i1 += i2;
					j += width;
				}
				return;
			}
			k -= i;
			i -= j;
			for (j = lineOffsets[j]; --i >= 0; j += width) {
				method377(pixels, j, k1, i1 >> 16, j1 >> 16);
				j1 += l1;
				i1 += i2;
			}
			while (--k >= 0) {
				method377(pixels, j, k1, i1 >> 16, l >> 16);
				l += j2;
				i1 += i2;
				j += width;
			}
			return;
		}
		if (k >= bottomY)
			return;
		if (i > bottomY)
			i = bottomY;
		if (j > bottomY)
			j = bottomY;
		if (i < j) {
			i1 = j1 <<= 16;
			if (k < 0) {
				i1 -= i2 * k;
				j1 -= j2 * k;
				k = 0;
			}
			l <<= 16;
			if (i < 0) {
				l -= l1 * i;
				i = 0;
			}
			if (i2 < j2) {
				j -= i;
				i -= k;
				for (k = lineOffsets[k]; --i >= 0; k += width) {
					method377(pixels, k, k1, i1 >> 16, j1 >> 16);
					i1 += i2;
					j1 += j2;
				}
				while (--j >= 0) {
					method377(pixels, k, k1, i1 >> 16, l >> 16);
					i1 += i2;
					l += l1;
					k += width;
				}
				return;
			}
			j -= i;
			i -= k;
			for (k = lineOffsets[k]; --i >= 0; k += width) {
				method377(pixels, k, k1, j1 >> 16, i1 >> 16);
				i1 += i2;
				j1 += j2;
			}
			while (--j >= 0) {
				method377(pixels, k, k1, l >> 16, i1 >> 16);
				i1 += i2;
				l += l1;
				k += width;
			}
			return;
		}
		l = j1 <<= 16;
		if (k < 0) {
			l -= i2 * k;
			j1 -= j2 * k;
			k = 0;
		}
		i1 <<= 16;
		if (j < 0) {
			i1 -= l1 * j;
			j = 0;
		}
		if (i2 < j2) {
			i -= j;
			j -= k;
			for (k = lineOffsets[k]; --j >= 0; k += width) {
				method377(pixels, k, k1, l >> 16, j1 >> 16);
				l += i2;
				j1 += j2;
			}
			while (--i >= 0) {
				method377(pixels, k, k1, i1 >> 16, j1 >> 16);
				i1 += l1;
				j1 += j2;
				k += width;
			}
			return;
		}
		i -= j;
		j -= k;
		for (k = lineOffsets[k]; --j >= 0; k += width) {
			method377(pixels, k, k1, j1 >> 16, l >> 16);
			l += i2;
			j1 += j2;
		}
		while (--i >= 0) {
			method377(pixels, k, k1, j1 >> 16, i1 >> 16);
			i1 += l1;
			j1 += j2;
			k += width;
		}
	}

	private static void method377(int ai[], int i, int j, int l, int i1) {
		int k;// was parameter
		if (restrict_edges) {
			if (i1 > centerX)
				i1 = centerX;
			if (l < 0)
				l = 0;
		}
		if (l >= i1)
			return;
		i += l;
		k = i1 - l >> 2;
		if (alpha == 0) {
			while (--k >= 0) {
				ai[i++] = j;
				ai[i++] = j;
				ai[i++] = j;
				ai[i++] = j;
			}
			for (k = i1 - l & 3; --k >= 0;)
				ai[i++] = j;
			return;
		}
		int j1 = alpha;
		int k1 = 256 - alpha;
		j = ((j & 0xff00ff) * k1 >> 8 & 0xff00ff) + ((j & 0xff00) * k1 >> 8 & 0xff00);
		while (--k >= 0) {
			ai[i] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff) + ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
			i++;
			ai[i] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff) + ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
			i++;
			ai[i] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff) + ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
			i++;
			ai[i] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff) + ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
			i++;
		}
		for (k = i1 - l & 3; --k >= 0;) {
			ai[i] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff) + ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
			i++;
		}
	}

	public static void method378(int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2, int k2, int l2, int i3,
			int j3, int k3, int l3, int i4, int j4, int k4) {
		int ai[] = method371(k4);
		aBoolean1463 = !aBooleanArray1475[k4];
		k2 = j2 - k2;
		j3 = i3 - j3;
		i4 = l3 - i4;
		l2 -= j2;
		k3 -= i3;
		j4 -= l3;
		int l4 = l2 * i3 - k3 * j2 << 14;
		int i5 = k3 * l3 - j4 * i3 << 8;
		int j5 = j4 * j2 - l2 * l3 << 5;
		int k5 = k2 * i3 - j3 * j2 << 14;
		int l5 = j3 * l3 - i4 * i3 << 8;
		int i6 = i4 * j2 - k2 * l3 << 5;
		int j6 = j3 * l2 - k2 * k3 << 14;
		int k6 = i4 * k3 - j3 * j4 << 8;
		int l6 = k2 * j4 - i4 * l2 << 5;
		int i7 = 0;
		int j7 = 0;
		if (j != i) {
			i7 = (i1 - l << 16) / (j - i);
			j7 = (l1 - k1 << 16) / (j - i);
		}
		int k7 = 0;
		int l7 = 0;
		if (k != j) {
			k7 = (j1 - i1 << 16) / (k - j);
			l7 = (i2 - l1 << 16) / (k - j);
		}
		int i8 = 0;
		int j8 = 0;
		if (k != i) {
			i8 = (l - j1 << 16) / (i - k);
			j8 = (k1 - i2 << 16) / (i - k);
		}
		if (i <= j && i <= k) {
			if (i >= bottomY)
				return;
			if (j > bottomY)
				j = bottomY;
			if (k > bottomY)
				k = bottomY;
			if (j < k) {
				j1 = l <<= 16;
				i2 = k1 <<= 16;
				if (i < 0) {
					j1 -= i8 * i;
					l -= i7 * i;
					i2 -= j8 * i;
					k1 -= j7 * i;
					i = 0;
				}
				i1 <<= 16;
				l1 <<= 16;
				if (j < 0) {
					i1 -= k7 * j;
					l1 -= l7 * j;
					j = 0;
				}
				int k8 = i - textureInt2;
				l4 += j5 * k8;
				k5 += i6 * k8;
				j6 += l6 * k8;
				if (i != j && i8 < i7 || i == j && i8 > k7) {
					k -= j;
					j -= i;
					i = lineOffsets[i];
					while (--j >= 0) {
						method379(pixels, ai, i, j1 >> 16, l >> 16, i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
						j1 += i8;
						l += i7;
						i2 += j8;
						k1 += j7;
						i += width;
						l4 += j5;
						k5 += i6;
						j6 += l6;
					}
					while (--k >= 0) {
						method379(pixels, ai, i, j1 >> 16, i1 >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
						j1 += i8;
						i1 += k7;
						i2 += j8;
						l1 += l7;
						i += width;
						l4 += j5;
						k5 += i6;
						j6 += l6;
					}
					return;
				}
				k -= j;
				j -= i;
				i = lineOffsets[i];
				while (--j >= 0) {
					method379(pixels, ai, i, l >> 16, j1 >> 16, k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
					j1 += i8;
					l += i7;
					i2 += j8;
					k1 += j7;
					i += width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--k >= 0) {
					method379(pixels, ai, i, i1 >> 16, j1 >> 16, l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
					j1 += i8;
					i1 += k7;
					i2 += j8;
					l1 += l7;
					i += width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			i1 = l <<= 16;
			l1 = k1 <<= 16;
			if (i < 0) {
				i1 -= i8 * i;
				l -= i7 * i;
				l1 -= j8 * i;
				k1 -= j7 * i;
				i = 0;
			}
			j1 <<= 16;
			i2 <<= 16;
			if (k < 0) {
				j1 -= k7 * k;
				i2 -= l7 * k;
				k = 0;
			}
			int l8 = i - textureInt2;
			l4 += j5 * l8;
			k5 += i6 * l8;
			j6 += l6 * l8;
			if (i != k && i8 < i7 || i == k && k7 > i7) {
				j -= k;
				k -= i;
				i = lineOffsets[i];
				while (--k >= 0) {
					method379(pixels, ai, i, i1 >> 16, l >> 16, l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
					i1 += i8;
					l += i7;
					l1 += j8;
					k1 += j7;
					i += width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--j >= 0) {
					method379(pixels, ai, i, j1 >> 16, l >> 16, i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
					j1 += k7;
					l += i7;
					i2 += l7;
					k1 += j7;
					i += width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			j -= k;
			k -= i;
			i = lineOffsets[i];
			while (--k >= 0) {
				method379(pixels, ai, i, l >> 16, i1 >> 16, k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
				i1 += i8;
				l += i7;
				l1 += j8;
				k1 += j7;
				i += width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			while (--j >= 0) {
				method379(pixels, ai, i, l >> 16, j1 >> 16, k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
				j1 += k7;
				l += i7;
				i2 += l7;
				k1 += j7;
				i += width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			return;
		}
		if (j <= k) {
			if (j >= bottomY)
				return;
			if (k > bottomY)
				k = bottomY;
			if (i > bottomY)
				i = bottomY;
			if (k < i) {
				l = i1 <<= 16;
				k1 = l1 <<= 16;
				if (j < 0) {
					l -= i7 * j;
					i1 -= k7 * j;
					k1 -= j7 * j;
					l1 -= l7 * j;
					j = 0;
				}
				j1 <<= 16;
				i2 <<= 16;
				if (k < 0) {
					j1 -= i8 * k;
					i2 -= j8 * k;
					k = 0;
				}
				int i9 = j - textureInt2;
				l4 += j5 * i9;
				k5 += i6 * i9;
				j6 += l6 * i9;
				if (j != k && i7 < k7 || j == k && i7 > i8) {
					i -= k;
					k -= j;
					j = lineOffsets[j];
					while (--k >= 0) {
						method379(pixels, ai, j, l >> 16, i1 >> 16, k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
						l += i7;
						i1 += k7;
						k1 += j7;
						l1 += l7;
						j += width;
						l4 += j5;
						k5 += i6;
						j6 += l6;
					}
					while (--i >= 0) {
						method379(pixels, ai, j, l >> 16, j1 >> 16, k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
						l += i7;
						j1 += i8;
						k1 += j7;
						i2 += j8;
						j += width;
						l4 += j5;
						k5 += i6;
						j6 += l6;
					}
					return;
				}
				i -= k;
				k -= j;
				j = lineOffsets[j];
				while (--k >= 0) {
					method379(pixels, ai, j, i1 >> 16, l >> 16, l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
					l += i7;
					i1 += k7;
					k1 += j7;
					l1 += l7;
					j += width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--i >= 0) {
					method379(pixels, ai, j, j1 >> 16, l >> 16, i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
					l += i7;
					j1 += i8;
					k1 += j7;
					i2 += j8;
					j += width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			j1 = i1 <<= 16;
			i2 = l1 <<= 16;
			if (j < 0) {
				j1 -= i7 * j;
				i1 -= k7 * j;
				i2 -= j7 * j;
				l1 -= l7 * j;
				j = 0;
			}
			l <<= 16;
			k1 <<= 16;
			if (i < 0) {
				l -= i8 * i;
				k1 -= j8 * i;
				i = 0;
			}
			int j9 = j - textureInt2;
			l4 += j5 * j9;
			k5 += i6 * j9;
			j6 += l6 * j9;
			if (i7 < k7) {
				k -= i;
				i -= j;
				j = lineOffsets[j];
				while (--i >= 0) {
					method379(pixels, ai, j, j1 >> 16, i1 >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
					j1 += i7;
					i1 += k7;
					i2 += j7;
					l1 += l7;
					j += width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--k >= 0) {
					method379(pixels, ai, j, l >> 16, i1 >> 16, k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
					l += i8;
					i1 += k7;
					k1 += j8;
					l1 += l7;
					j += width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			k -= i;
			i -= j;
			j = lineOffsets[j];
			while (--i >= 0) {
				method379(pixels, ai, j, i1 >> 16, j1 >> 16, l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
				j1 += i7;
				i1 += k7;
				i2 += j7;
				l1 += l7;
				j += width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			while (--k >= 0) {
				method379(pixels, ai, j, i1 >> 16, l >> 16, l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
				l += i8;
				i1 += k7;
				k1 += j8;
				l1 += l7;
				j += width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			return;
		}
		if (k >= bottomY)
			return;
		if (i > bottomY)
			i = bottomY;
		if (j > bottomY)
			j = bottomY;
		if (i < j) {
			i1 = j1 <<= 16;
			l1 = i2 <<= 16;
			if (k < 0) {
				i1 -= k7 * k;
				j1 -= i8 * k;
				l1 -= l7 * k;
				i2 -= j8 * k;
				k = 0;
			}
			l <<= 16;
			k1 <<= 16;
			if (i < 0) {
				l -= i7 * i;
				k1 -= j7 * i;
				i = 0;
			}
			int k9 = k - textureInt2;
			l4 += j5 * k9;
			k5 += i6 * k9;
			j6 += l6 * k9;
			if (k7 < i8) {
				j -= i;
				i -= k;
				k = lineOffsets[k];
				while (--i >= 0) {
					method379(pixels, ai, k, i1 >> 16, j1 >> 16, l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
					i1 += k7;
					j1 += i8;
					l1 += l7;
					i2 += j8;
					k += width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--j >= 0) {
					method379(pixels, ai, k, i1 >> 16, l >> 16, l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
					i1 += k7;
					l += i7;
					l1 += l7;
					k1 += j7;
					k += width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			j -= i;
			i -= k;
			k = lineOffsets[k];
			while (--i >= 0) {
				method379(pixels, ai, k, j1 >> 16, i1 >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
				i1 += k7;
				j1 += i8;
				l1 += l7;
				i2 += j8;
				k += width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			while (--j >= 0) {
				method379(pixels, ai, k, l >> 16, i1 >> 16, k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
				i1 += k7;
				l += i7;
				l1 += l7;
				k1 += j7;
				k += width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			return;
		}
		l = j1 <<= 16;
		k1 = i2 <<= 16;
		if (k < 0) {
			l -= k7 * k;
			j1 -= i8 * k;
			k1 -= l7 * k;
			i2 -= j8 * k;
			k = 0;
		}
		i1 <<= 16;
		l1 <<= 16;
		if (j < 0) {
			i1 -= i7 * j;
			l1 -= j7 * j;
			j = 0;
		}
		int l9 = k - textureInt2;
		l4 += j5 * l9;
		k5 += i6 * l9;
		j6 += l6 * l9;
		if (k7 < i8) {
			i -= j;
			j -= k;
			k = lineOffsets[k];
			while (--j >= 0) {
				method379(pixels, ai, k, l >> 16, j1 >> 16, k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
				l += k7;
				j1 += i8;
				k1 += l7;
				i2 += j8;
				k += width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			while (--i >= 0) {
				method379(pixels, ai, k, i1 >> 16, j1 >> 16, l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
				i1 += i7;
				j1 += i8;
				l1 += j7;
				i2 += j8;
				k += width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			return;
		}
		i -= j;
		j -= k;
		k = lineOffsets[k];
		while (--j >= 0) {
			method379(pixels, ai, k, j1 >> 16, l >> 16, i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
			l += k7;
			j1 += i8;
			k1 += l7;
			i2 += j8;
			k += width;
			l4 += j5;
			k5 += i6;
			j6 += l6;
		}
		while (--i >= 0) {
			method379(pixels, ai, k, j1 >> 16, i1 >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
			i1 += i7;
			j1 += i8;
			l1 += j7;
			i2 += j8;
			k += width;
			l4 += j5;
			k5 += i6;
			j6 += l6;
		}
	}

	private static void method379(int ai[], int ai1[], int k, int l, int i1, int j1, int k1, int l1, int i2, int j2, int k2, int l2, int i3) {
		int i = 0;
		int j = 0;
		if (l >= i1)
			return;
		int j3;
		int k3;
		if (restrict_edges) {
			j3 = (k1 - j1) / (i1 - l);
			if (i1 > centerX)
				i1 = centerX;
			if (l < 0) {
				j1 -= l * j3;
				l = 0;
			}
			if (l >= i1)
				return;
			k3 = i1 - l >> 3;
			j3 <<= 12;
			j1 <<= 9;
		} else {
			if (i1 - l > 7) {
				k3 = i1 - l >> 3;
				j3 = (k1 - j1) * anIntArray1468[k3] >> 6;
			} else {
				k3 = 0;
				j3 = 0;
			}
			j1 <<= 9;
		}
		k += l;
		if (lowMem) {
			int i4 = 0;
			int k4 = 0;
			int k6 = l - textureInt1;
			l1 += (k2 >> 3) * k6;
			i2 += (l2 >> 3) * k6;
			j2 += (i3 >> 3) * k6;
			int i5 = j2 >> 12;
			if (i5 != 0) {
				i = l1 / i5;
				j = i2 / i5;
				if (i < 0)
					i = 0;
				else if (i > 4032)
					i = 4032;
			}
			l1 += k2;
			i2 += l2;
			j2 += i3;
			i5 = j2 >> 12;
			if (i5 != 0) {
				i4 = l1 / i5;
				k4 = i2 / i5;
				if (i4 < 7)
					i4 = 7;
				else if (i4 > 4032)
					i4 = 4032;
			}
			int i7 = i4 - i >> 3;
			int k7 = k4 - j >> 3;
			i += (j1 & 0x600000) >> 3;
			int i8 = j1 >> 23;
			if (aBoolean1463) {
				while (k3-- > 0) {
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i = i4;
					j = k4;
					l1 += k2;
					i2 += l2;
					j2 += i3;
					int j5 = j2 >> 12;
					if (j5 != 0) {
						i4 = l1 / j5;
						k4 = i2 / j5;
						if (i4 < 7)
							i4 = 7;
						else if (i4 > 4032)
							i4 = 4032;
					}
					i7 = i4 - i >> 3;
					k7 = k4 - j >> 3;
					j1 += j3;
					i += (j1 & 0x600000) >> 3;
					i8 = j1 >> 23;
				}
				for (k3 = i1 - l & 7; k3-- > 0;) {
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
				}
				return;
			}
			while (k3-- > 0) {
				int k8;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i = i4;
				j = k4;
				l1 += k2;
				i2 += l2;
				j2 += i3;
				int k5 = j2 >> 12;
				if (k5 != 0) {
					i4 = l1 / k5;
					k4 = i2 / k5;
					if (i4 < 7)
						i4 = 7;
					else if (i4 > 4032)
						i4 = 4032;
				}
				i7 = i4 - i >> 3;
				k7 = k4 - j >> 3;
				j1 += j3;
				i += (j1 & 0x600000) >> 3;
				i8 = j1 >> 23;
			}
			for (k3 = i1 - l & 7; k3-- > 0;) {
				int l8;
				if ((l8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = l8;
				k++;
				i += i7;
				j += k7;
			}
			return;
		}
		int j4 = 0;
		int l4 = 0;
		int l6 = l - textureInt1;
		l1 += (k2 >> 3) * l6;
		i2 += (l2 >> 3) * l6;
		j2 += (i3 >> 3) * l6;
		int l5 = j2 >> 14;
		if (l5 != 0) {
			i = l1 / l5;
			j = i2 / l5;
			if (i < 0)
				i = 0;
			else if (i > 16256)
				i = 16256;
		}
		l1 += k2;
		i2 += l2;
		j2 += i3;
		l5 = j2 >> 14;
		if (l5 != 0) {
			j4 = l1 / l5;
			l4 = i2 / l5;
			if (j4 < 7)
				j4 = 7;
			else if (j4 > 16256)
				j4 = 16256;
		}
		int j7 = j4 - i >> 3;
		int l7 = l4 - j >> 3;
		i += j1 & 0x600000;
		int j8 = j1 >> 23;
		if (aBoolean1463) {
			while (k3-- > 0) {
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i = j4;
				j = l4;
				l1 += k2;
				i2 += l2;
				j2 += i3;
				int i6 = j2 >> 14;
				if (i6 != 0) {
					j4 = l1 / i6;
					l4 = i2 / i6;
					if (j4 < 7)
						j4 = 7;
					else if (j4 > 16256)
						j4 = 16256;
				}
				j7 = j4 - i >> 3;
				l7 = l4 - j >> 3;
				j1 += j3;
				i += j1 & 0x600000;
				j8 = j1 >> 23;
			}
			for (k3 = i1 - l & 7; k3-- > 0;) {
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
			}
			return;
		}
		while (k3-- > 0) {
			int i9;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i = j4;
			j = l4;
			l1 += k2;
			i2 += l2;
			j2 += i3;
			int j6 = j2 >> 14;
			if (j6 != 0) {
				j4 = l1 / j6;
				l4 = i2 / j6;
				if (j4 < 7)
					j4 = 7;
				else if (j4 > 16256)
					j4 = 16256;
			}
			j7 = j4 - i >> 3;
			l7 = l4 - j >> 3;
			j1 += j3;
			i += j1 & 0x600000;
			j8 = j1 >> 23;
		}
		for (int l3 = i1 - l & 7; l3-- > 0;) {
			int j9;
			if ((j9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = j9;
			k++;
			i += j7;
			j += l7;
		}
	}

	public static boolean lowMem = false;
	public static boolean restrict_edges;
	private static boolean aBoolean1463;
	public static boolean notTextured = true;
	public static int alpha;
	public static int textureInt1;
	public static int textureInt2;
	private static int[] anIntArray1468;
	public static final int[] anIntArray1469;
	public static int sineTable[];
	public static int cosineTable[];
	public static int lineOffsets[];
	private static int anInt1473;
	public static IndexedImage textureImages[] = new IndexedImage[50];
	private static boolean[] aBooleanArray1475 = new boolean[50];
	private static int[] anIntArray1476 = new int[50];
	private static int anInt1477;
	private static int[][] anIntArrayArray1478;
	private static int[][] anIntArrayArray1479 = new int[50][];
	public static int textureLastUsed[] = new int[50];
	public static int anInt1481;
	public static int hsl2rgb[] = new int[0x10000];
	private static int[][] anIntArrayArray1483 = new int[50][];
	static {
		anIntArray1468 = new int[512];
		anIntArray1469 = new int[2048];
		sineTable = new int[2048];
		cosineTable = new int[2048];
		for (int i = 1; i < 512; i++)
			anIntArray1468[i] = 32768 / i;
		for (int j = 1; j < 2048; j++)
			anIntArray1469[j] = 0x10000 / j;
		for (int k = 0; k < 2048; k++) {
			sineTable[k] = (int) (65536D * Math.sin((double) k * (Math.PI / 1024.0D)));
			cosineTable[k] = (int) (65536D * Math.cos((double) k * (Math.PI / 1024.0D)));
		}
	}
}
