public class RSRaster extends QueueNode {

	public static void initDrawingArea(int i, int j, int ai[]) {
		pixels = ai;
		width = (short) j;
		height = (short) i;
		setDrawingArea(i, 0, j, 0);
	}

	public static void defaultDrawingAreaSize() {
		topX = 0;
		topY = 0;
		bottomX = width;
		bottomY = height;
		centerX = bottomX;
		centerY = (short) (bottomX / 2);
	}

	public static void setDrawingArea(int botY, int tpX, int botX, int tpY) {
		if (tpX < 0)
			tpX = 0;
		if (tpY < 0)
			tpY = 0;
		if (botX > width)
			botX = width;
		if (botY > height)
			botY = height;
		topX = (short) tpX;
		topY = (short) tpY;
		bottomX = (short) botX;
		bottomY = (short) botY;
		centerX = bottomX;
		centerY = (short) (bottomX / 2);
		anInt1387 = (short) (bottomY / 2);
	}

	public static void drawHorizontalLine(int drawX, int drawY, int lineLength, int color) {
		if (drawY >= topY && drawY < bottomY) {
			if (drawX < topX) {
				lineLength -= topX - drawX;
				drawX = topX;
			}
			if (drawX + lineLength > bottomX) {
				lineLength = bottomX - drawX;
			}
			int i_63_ = drawX + drawY * width;
			for (int i_64_ = 0; i_64_ < lineLength; i_64_++) {
				pixels[i_63_ + i_64_] = color;
			}
		}
	}
	
	public static void drawHorizontalLine(int x, int y, int length, int color, int alphaValue) {
		if (y < topY || y >= bottomY)
			return;
		if (x < topX) {
			length -= topX - x;
			x = topX;
		}
		if (x + length > bottomX)
			length = bottomX - x;
		int alpha = 256 - alphaValue;
		int red = (color >> 16 & 0xff) * alphaValue;
		int green = (color >> 8 & 0xff) * alphaValue;
		int blue = (color & 0xff) * alphaValue;
		int pixel = x + y * width;
		for (int index = 0; index < length; index++) {
			int r = (pixels[pixel] >> 16 & 0xff) * alpha;
			int g = (pixels[pixel] >> 8 & 0xff) * alpha;
			int b = (pixels[pixel] & 0xff) * alpha;
			int pixelColor = ((red + r >> 8) << 16) + ((green + g >> 8) << 8) + (blue + b >> 8);
			pixels[pixel++] = pixelColor;
		}
	}
	
	public static void drawVerticalLine(int drawX, int drawY, int lineLength, int color) {
		if (drawX >= topX && drawX < bottomX) {
			if (drawY < topY) {
				lineLength -= topY - drawY;
				drawY = topY;
			}
			if (drawY + lineLength > bottomY) {
				lineLength = bottomY - drawY;
			}
			int i = drawX + drawY * width;
			for (int i_13_ = 0; i_13_ < lineLength; i_13_++) {
				pixels[i + i_13_ * width] = color;
			}
		}
	}
	
	public static void drawVerticalLine(int x, int y, int length, int color, int alpha) {
		if (x < topX || x >= bottomX) {
			return;
		}
		if (y < topY) {
			length -= topY - y;
			y = topY;
		}
		if (y + length > bottomY) {
			length = bottomY - y;
		}
		int alphaValue = 256 - alpha;
		int red = (color >> 16 & 0xff) * alpha;
		int green = (color >> 8 & 0xff) * alpha;
		int blue = (color & 0xff) * alpha;
		int pixel = x + y * width;
		for (int j3 = 0; j3 < length; j3++) {
			int r = (pixels[pixel] >> 16 & 0xff) * alphaValue;
			int g = (pixels[pixel] >> 8 & 0xff) * alphaValue;
			int b = (pixels[pixel] & 0xff) * alphaValue;
			int pixelColor = ((red + r >> 8) << 16) + ((green + g >> 8) << 8) + (blue + b >> 8);
			pixels[pixel] = pixelColor;
			pixel += width;
		}
	}
	
    public static void drawRoundedRectangle(int x, int y, int width,
            int height, int color, int alpha, boolean filled, boolean shadowed, int roundDepth) {
        if (shadowed) {
            drawRoundedRectangle(x + 1, y + 1, width, height, 0, alpha, filled, false, roundDepth);
        }
        if (alpha == -1) {
            if (filled) {
                drawHorizontalLine(y + 1, color, width - 4, x + 2);
                drawHorizontalLine(y + height - 2, color, width - 4, x + 2);
                drawFilledPixels(height - 4, y + 2, x + 1, color, width - 2);
            }
            drawHorizontalLine(y, color, width - 4, x + 2);
            drawHorizontalLine(y + height - 1, color, width - 4, x + 2);
            drawVerticalLine(x, y + 2, height - 4, color);
            drawVerticalLine(x + width - 1, y + 2, height - 4, color);
            drawFilledPixels(x + 1, y + 1, 1, 1, color);
            drawFilledPixels(x + width - 2, y + 1, 1, 1, color);
            drawFilledPixels(x + width - 2, y + height - 2, 1, 1, color);
            drawFilledPixels(x + 1, y + height - 2, 1, 1, color);
        } else if (alpha != -1) {
            if (filled) {
                drawHorizontalLine(x + 2, y + 1, width - 4, color, alpha);
                drawHorizontalLine(x + 2, y + height - 2, width - 4, color, alpha);
                drawFilledPixels(x + 1, y + 2, width - 2, height - 4, color, alpha);
            }
            drawHorizontalLine(x + 2, y, width - 4, color, alpha);
            drawHorizontalLine(x + 2, y + height - 1, width - 4, color, alpha);
            drawVerticalLine(x, y + 2, height - 4, color, alpha);
            drawVerticalLine(x + width - 1, y + 2, height - 4, color, alpha);
            fillRectangleAlpha(x + 1, y + 1, 1, 1, color, alpha);
            fillRectangleAlpha(x + width - 2, y + 1, 1, 1, color, alpha);
            fillRectangleAlpha(x + 1, y + height - 2, 1, 1, color, alpha);
            fillRectangleAlpha(x + width - 2, y + height - 2, 1, 1, color, alpha);
        }
    }
	
	public static void drawRectangle(final int x, final int y, final int w,
			final int h, final int color) {
		drawHorizontalLine(x, y, w, color);
		drawHorizontalLine(x, y + h - 1, w, color);
		drawVerticalLine(x, y, h, color);
		drawVerticalLine(x + w - 1, y, h, color);
	}
	
	public static void drawFilledPixels(int xPos, int yPos,
		    int pixelWidth, int pixelHeight, int color, int alpha) { // method586
		    if (xPos < topX) {
		        pixelWidth -= topX - xPos;
		        xPos = topX;
		    }
		    if (yPos < topY) {
		        pixelHeight -= topY - yPos;
		        yPos = topY;
		    }
		    if (xPos + pixelWidth > bottomX)
		        pixelWidth = bottomX - xPos;
		    if (yPos + pixelHeight > bottomY)
		        pixelHeight = bottomY - yPos;
		    color = ((color & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((color & 0xff00) * alpha >> 8 & 0xff00);
		    int k1 = 256 - alpha;
		    int l1 = width - pixelWidth;
		    int i2 = xPos + yPos * width;
		    for (int j2 = 0; j2 < pixelHeight; j2++) {
		        for (int k2 = -pixelWidth; k2 < 0; k2++) {
		            int l2 = pixels[i2];
		            l2 = ((l2 & 0xff00ff) * k1 >> 8 & 0xff00ff) + ((l2 & 0xff00) * k1 >> 8 & 0xff00);
		            pixels[i2++] = color + l2;
		        }
		        i2 += l1;
		    }
		}
	
	public static void drawFilledPixels(int x, int y, int pixelWidth,
			int pixelHeight, int color) {// method578
		if (x < topX) {
			pixelWidth -= topX - x;
			x = topX;
		}
		if (y < topY) {
			pixelHeight -= topY - y;
			y = topY;
		}
		if (x + pixelWidth > bottomX)
			pixelWidth = bottomX - x;
		if (y + pixelHeight > bottomY)
			pixelHeight = bottomY - y;
		int j1 = width - pixelWidth;
		int k1 = x + y * width;
		for (int l1 = -pixelHeight; l1 < 0; l1++) {
			for (int i2 = -pixelWidth; i2 < 0; i2++)
				pixels[k1++] = color;
			k1 += j1;
		}
	}
	
	public static void fillRectangleAlpha(int x, int y, int w, int h,
			final int color, final int opac) {
		if (x < topX) {
			w -= topX - x;
			x = topX;
		}
		if (y < topY) {
			h -= topY - y;
			y = topY;
		}
		if (x + w > bottomX) {
			w = bottomX - x;
		}
		if (y + h > bottomY) {
			h = bottomY - y;
		}
		final int opacity = 256 - opac;
		final int ra = (color >> 16 & 0xff) * opac;
		final int ga = (color >> 8 & 0xff) * opac;
		final int ba = (color & 0xff) * opac;
		final int numLeft = width - w;
		int inc = x + y * width;
		for (int l3 = 0; l3 < h; l3++) {
			for (int i4 = -w; i4 < 0; i4++) {
				final int r = (pixels[inc] >> 16 & 0xff) * opacity;
				final int g = (pixels[inc] >> 8 & 0xff) * opacity;
				final int b = (pixels[inc] & 0xff) * opacity;
				final int rgba = (ra + r >> 8 << 16) + (ga + g >> 8 << 8)
						+ (ba + b >> 8);
				pixels[inc++] = rgba;
			}

			inc += numLeft;
		}

	}

	public static void setAllPixelsToZero() {
		int i = width * height;
		for (int j = 0; j < i; j++)
			pixels[j] = 0;
	}

	public static void method335(int i, int j, int k, int l, int i1, int k1) {
		if (k1 < topX) {
			k -= topX - k1;
			k1 = topX;
		}
		if (j < topY) {
			l -= topY - j;
			j = topY;
		}
		if (k1 + k > bottomX)
			k = bottomX - k1;
		if (j + l > bottomY)
			l = bottomY - j;
		int l1 = 256 - i1;
		int i2 = (i >> 16 & 0xff) * i1;
		int j2 = (i >> 8 & 0xff) * i1;
		int k2 = (i & 0xff) * i1;
		int k3 = width - k;
		int l3 = k1 + j * width;
		for (int i4 = 0; i4 < l; i4++) {
			for (int j4 = -k; j4 < 0; j4++) {
				int l2 = (pixels[l3] >> 16 & 0xff) * l1;
				int i3 = (pixels[l3] >> 8 & 0xff) * l1;
				int j3 = (pixels[l3] & 0xff) * l1;
				int k4 = ((i2 + l2 >> 8) << 16) + ((j2 + i3 >> 8) << 8) + (k2 + j3 >> 8);
				pixels[l3++] = k4;
			}
			l3 += k3;
		}
	}

	public static void transparentBox(int i, int j, int k, int l, int i1, int j1, int opac) {
		int j3 = 256 - opac;
		if (k < topX) {
			i1 -= topX - k;
			k = topX;
		}
		if (j < topY) {
			i -= topY - j;
			j = topY;
		}
		if (k + i1 > bottomX)
			i1 = bottomX - k;
		if (j + i > bottomY)
			i = bottomY - j;
		int k1 = width - i1;
		int l1 = k + j * width;
		if (j1 != 0)
			anInt1387 = -374;
		for (int i2 = -i; i2 < 0; i2++) {
			for (int j2 = -i1; j2 < 0; j2++) {
				int i3 = pixels[l1];
				pixels[l1++] = ((l & 0xff00ff) * opac + (i3 & 0xff00ff) * j3 & 0xff00ff00)
						+ ((l & 0xff00) * opac + (i3 & 0xff00) * j3 & 0xff0000) >> 8;
			}
			l1 += k1;
		}
	}
	
	public static void drawPixels(int height, int yPos, int xPos, int color, int fill) {
		if (xPos < topX) {
			fill -= topX - xPos;
			xPos = topX;
		}
		if (yPos < topY) {
			height -= topY - yPos;
			yPos = topY;
		}
		if (xPos + fill > bottomX)
			fill = bottomX - xPos;
		if (yPos + height > bottomY)
			height = bottomY - yPos;
		int k1 = width - fill;
		int l1 = xPos + yPos * width;
		for (int i2 = -height; i2 < 0; i2++) {
			for (int j2 = -fill; j2 < 0; j2++)
				pixels[l1++] = color;
			l1 += k1;
		}
	}

	public static void fillPixels(int i, int j, int k, int l, int i1) {
		method339(i1, l, j, i);
		method339((i1 + k) - 1, l, j, i);
		method341(i1, l, k, i);
		method341(i1, l, k, (i + j) - 1);
	}

	public static void method338(int i, int j, int k, int l, int i1, int j1) {
		method340(l, i1, i, k, j1);
		method340(l, i1, (i + j) - 1, k, j1);
		if (j >= 3) {
			method342(l, j1, k, i + 1, j - 2);
			method342(l, (j1 + i1) - 1, k, i + 1, j - 2);
		}
	}

	public static void method339(int i, int j, int k, int l) {
		if (i < topY || i >= bottomY)
			return;
		if (l < topX) {
			k -= topX - l;
			l = topX;
		}
		if (l + k > bottomX)
			k = bottomX - l;
		int i1 = l + i * width;
		for (int j1 = 0; j1 < k; j1++)
			pixels[i1 + j1] = j;
	}

	private static void method340(int i, int j, int k, int l, int i1) {
		if (k < topY || k >= bottomY)
			return;
		if (i1 < topX) {
			j -= topX - i1;
			i1 = topX;
		}
		if (i1 + j > bottomX)
			j = bottomX - i1;
		int j1 = 256 - l;
		int k1 = (i >> 16 & 0xff) * l;
		int l1 = (i >> 8 & 0xff) * l;
		int i2 = (i & 0xff) * l;
		int i3 = i1 + k * width;
		for (int j3 = 0; j3 < j; j3++) {
			int j2 = (pixels[i3] >> 16 & 0xff) * j1;
			int k2 = (pixels[i3] >> 8 & 0xff) * j1;
			int l2 = (pixels[i3] & 0xff) * j1;
			int k3 = ((k1 + j2 >> 8) << 16) + ((l1 + k2 >> 8) << 8) + (i2 + l2 >> 8);
			pixels[i3++] = k3;
		}
	}

	public static void method341(int i, int j, int k, int l) {
		if (l < topX || l >= bottomX)
			return;
		if (i < topY) {
			k -= topY - i;
			i = topY;
		}
		if (i + k > bottomY)
			k = bottomY - i;
		int j1 = l + i * width;
		for (int k1 = 0; k1 < k; k1++)
			pixels[j1 + k1 * width] = j;
	}

	private static void method342(int i, int j, int k, int l, int i1) {
		if (j < topX || j >= bottomX)
			return;
		if (l < topY) {
			i1 -= topY - l;
			l = topY;
		}
		if (l + i1 > bottomY)
			i1 = bottomY - l;
		int j1 = 256 - k;
		int k1 = (i >> 16 & 0xff) * k;
		int l1 = (i >> 8 & 0xff) * k;
		int i2 = (i & 0xff) * k;
		int i3 = j + l * width;
		for (int j3 = 0; j3 < i1; j3++) {
			int j2 = (pixels[i3] >> 16 & 0xff) * j1;
			int k2 = (pixels[i3] >> 8 & 0xff) * j1;
			int l2 = (pixels[i3] & 0xff) * j1;
			int k3 = ((k1 + j2 >> 8) << 16) + ((l1 + k2 >> 8) << 8) + (i2 + l2 >> 8);
			pixels[i3] = k3;
			i3 += width;
		}
	}

	public static void drawBubble(int x, int y, int radius, int colour, int initialAlpha) {
        fillCircleAlpha(x, y, radius, colour, initialAlpha);
        fillCircleAlpha(x, y, radius + 2, colour, 8);
        fillCircleAlpha(x, y, radius + 4, colour, 6);
        fillCircleAlpha(x, y, radius + 6, colour, 4);
        fillCircleAlpha(x, y, radius + 8, colour, 2);
	}

	public static void fillCircleAlpha(int posX, int posY, int radius, int colour, int alpha)
	{
        int dest_intensity = 256 - alpha;
        int src_red = (colour >> 16 & 0xff) * alpha;
        int src_green = (colour >> 8 & 0xff) * alpha;
        int src_blue = (colour & 0xff) * alpha;
        int i3 = posY - radius;
        if(i3 < 0)
                i3 = 0;
        int j3 = posY + radius;
        if(j3 >= height)
                j3 = height - 1;
        for(int y = i3; y <= j3; y++)
        {
                int l3 = y - posY;
                int i4 = (int)Math.sqrt(radius * radius - l3 * l3);
                int x = posX - i4;
                if(x < 0)
                        x = 0;
                int k4 = posX + i4;
                if(k4 >= width)
                        k4 = width - 1;
                int pixel_offset = x + y * width;
                for(int i5 = x; i5 <= k4; i5++)
                {
                        int dest_red = (pixels[pixel_offset] >> 16 & 0xff) * dest_intensity;
                        int dest_green = (pixels[pixel_offset] >> 8 & 0xff) * dest_intensity;
                        int dest_blue = (pixels[pixel_offset] & 0xff) * dest_intensity;
                        int result_rgb = ((src_red + dest_red >> 8) << 16) + ((src_green + dest_green >> 8) << 8) + (src_blue + dest_blue >> 8);
                        pixels[pixel_offset++] = result_rgb;
                }
        }
	}
	
	public static void drawAlphaGradient2(int x, int y, int gradientWidth, int gradientHeight, int startColor, int endColor, int alpha) {
		int k1 = 0;
		int l1 = 0x10000 / gradientHeight;
		if (x < topX) {
			gradientWidth -= topX - x;
			x = topX;
		}
		if (y < topY) {
			k1 -= (topY - y) * l1;
			gradientHeight -= topY - y;
			y = topY;
		}
		if (x + gradientWidth > bottomX)
			gradientWidth = bottomX - x;
		if (y + gradientHeight > bottomY)
			gradientHeight = bottomY - y;
		int i2 = width - gradientWidth;
		int total_pixels = x + y * width;
		for (int k2 = -gradientHeight; k2 < 0; k2++) {
			int alpha2 = (gradientHeight + k2) * (gradientHeight / alpha);
			int result_alpha = 256 - alpha2;
			int gradient1 = 0x10000 - k1 >> 8;
			int gradient2 = k1 >> 8;
			int gradient_color = ((startColor & 0xff00ff) * gradient1 + (endColor & 0xff00ff) * gradient2 & 0xff00ff00)
					+ ((startColor & 0xff00) * gradient1 + (endColor & 0xff00) * gradient2 & 0xff0000) >>> 8;
			int color = ((gradient_color & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((gradient_color & 0xff00) * alpha >> 8 & 0xff00);
			for (int k3 = -gradientWidth; k3 < 0; k3++) {
				int colored_pixel = pixels[total_pixels];
				colored_pixel = ((colored_pixel & 0xff00ff) * result_alpha >> 8 & 0xff00ff)
						+ ((colored_pixel & 0xff00) * result_alpha >> 8 & 0xff00);
				pixels[total_pixels++] = color + colored_pixel;
			}
			total_pixels += i2;
			k1 -= l1;
		}
	}

	public static void drawGradient(int x, int y, int gradientWidth, int gradientHeight, int startColor, int endColor) {
		int k1 = 0;
		int l1 = 0x10000 / gradientHeight;
		if (x < topX) {
			gradientWidth -= topX - x;
			x = topX;
		}
		if (y < topY) {
			k1 += (topY - y) * l1;
			gradientHeight -= topY - y;
			y = topY;
		}
		if (x + gradientWidth > bottomX)
			gradientWidth = bottomX - x;
		if (y + gradientHeight > bottomY)
			gradientHeight = bottomY - y;
		int i2 = width - gradientWidth;
		int j2 = x + y * width;
		for (int k2 = -gradientHeight; k2 < 0; k2++) {
			int l2 = 0x10000 - k1 >> 8;
			int i3 = k1 >> 8;
			int j3 = ((startColor & 0xff00ff) * l2 + (endColor & 0xff00ff) * i3 & 0xff00ff00)
					+ ((startColor & 0xff00) * l2 + (endColor & 0xff00) * i3 & 0xff0000) >>> 8;
			for (int k3 = -gradientWidth; k3 < 0; k3++)
				pixels[j2++] = j3;
			j2 += i2;
			k1 += l1;
		}
	}

	public static void drawHorizontalGradient(int x, int y, int gradientWidth, int gradientHeight, int startColor, int endColor) {
		int k1 = 0;
		int l1 = 0x10000 / gradientHeight;
		if (x < topX) {
			gradientWidth -= topX - y;
			y = topX;
		}
		if (y < topY) {
			k1 += (topY - y) * l1;
			gradientHeight -= topY - x;
			x = topY;
		}
		if (x + gradientWidth > bottomX)
			gradientWidth = bottomX - y;
		if (y + gradientHeight > bottomY)
			gradientHeight = bottomY - x;
		int i2 = width - gradientWidth;
		int j2 = x + y * width;
		for (int k2 = -gradientHeight; k2 < 0; k2++) {
			int l2 = 0x10000 - k1 >> 8;
			int i3 = k1 >> 8;
			int j3 = ((startColor & 0xff00ff) * l2 + (endColor & 0xff00ff) * i3 & 0xff00ff00)
					+ ((startColor & 0xff00) * l2 + (endColor & 0xff00) * i3 & 0xff0000) >>> 8;
			for (int k3 = -gradientWidth; k3 < 0; k3++)
				pixels[j2++] = j3;
			j2 += i2;
			k1 += l1;
		}
	}

	public static void drawAlphaGradient(int x, int y, int gradientWidth, int gradientHeight, int startColor, int endColor, int alpha) {
		int k1 = 0;
		int l1 = 0x10000 / gradientHeight;
		if (x < topX) {
			gradientWidth -= topX - x;
			x = topX;
		}
		if (y < topY) {
			k1 += (topY - y) * l1;
			gradientHeight -= topY - y;
			y = topY;
		}
		if (x + gradientWidth > bottomX)
			gradientWidth = bottomX - x;
		if (y + gradientHeight > bottomY)
			gradientHeight = bottomY - y;
		int i2 = width - gradientWidth;
		int result_alpha = 256 - alpha;
		int total_pixels = x + y * width;
		for (int k2 = -gradientHeight; k2 < 0; k2++) {
			int gradient1 = 0x10000 - k1 >> 8;
			int gradient2 = k1 >> 8;
			int gradient_color = ((startColor & 0xff00ff) * gradient1 + (endColor & 0xff00ff) * gradient2 & 0xff00ff00)
					+ ((startColor & 0xff00) * gradient1 + (endColor & 0xff00) * gradient2 & 0xff0000) >>> 8;
			int color = ((gradient_color & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((gradient_color & 0xff00) * alpha >> 8 & 0xff00);
			for (int k3 = -gradientWidth; k3 < 0; k3++) {
				int colored_pixel = pixels[total_pixels];
				colored_pixel = ((colored_pixel & 0xff00ff) * result_alpha >> 8 & 0xff00ff)
						+ ((colored_pixel & 0xff00) * result_alpha >> 8 & 0xff00);
				pixels[total_pixels++] = color + colored_pixel;
			}
			total_pixels += i2;
			k1 += l1;
		}
	}

	RSRaster() {
	}

	public static int pixels[];
	public static short width;
	public static short height;
	public static short topY;
	public static short bottomY;
	public static short topX;
	public static short bottomX;
	public static short centerX;
	public static short centerY;
	public static short anInt1387;
}
