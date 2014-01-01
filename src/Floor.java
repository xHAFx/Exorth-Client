public final class Floor {

	public static void unpackConfig(CacheArchive streamLoader) {
		RSBuffer stream = new RSBuffer(streamLoader.getDataForName("flo.dat"));
		int cacheSize = stream.readUShort();
		if (cache == null)
			cache = new Floor[cacheSize];
		for (int j = 0; j < cacheSize; j++) {
			if (cache[j] == null)
				cache[j] = new Floor();
			cache[j].readValues(stream, j);
		}
	}

	private void readValues(RSBuffer stream, int id) {
		do {
			int i = stream.readUByte();
			if (i == 0)
				return;
			else if (i == 1) {
				anInt390 = stream.read24Int();
				method262(anInt390);
			} else if (i == 2) {
				anInt391 = stream.readUByte();
			} else if (i == 3) {
			} else if (i == 4) {
				overlay = stream.read24Int();
				method262_2(overlay);
			} else if (i == 5)
				aBoolean393 = false;
			else if (i == 6)
				stream.readString();
			else if (i == 7) {
				int j = anInt394_2;
				int k = anInt395_2;
				int l = anInt396_2;
				int i1 = anInt397_2;
				minimapColour = stream.read24Int();
				method262_2(minimapColour);
				anInt394_2 = j;
				anInt395_2 = k;
				anInt396_2 = l;
				anInt397_2 = i1;
				anInt398_2 = i1;
			} else {
				System.out.println("Error unrecognised config code: " + i);
			}
		} while (true);
	}

	private void method262(int i) {
		double d = (double) (i >> 16 & 0xff) / 256D;
		double d1 = (double) (i >> 8 & 0xff) / 256D;
		double d2 = (double) (i & 0xff) / 256D;
		double d3 = d;
		if (d1 < d3)
			d3 = d1;
		if (d2 < d3)
			d3 = d2;
		double d4 = d;
		if (d1 > d4)
			d4 = d1;
		if (d2 > d4)
			d4 = d2;
		double d5 = 0.0D;
		double d6 = 0.0D;
		double d7 = (d3 + d4) / 2D;
		if (d3 != d4) {
			if (d7 < 0.5D)
				d6 = (d4 - d3) / (d4 + d3);
			if (d7 >= 0.5D)
				d6 = (d4 - d3) / (2D - d4 - d3);
			if (d == d4)
				d5 = (d1 - d2) / (d4 - d3);
			else if (d1 == d4)
				d5 = 2D + (d2 - d) / (d4 - d3);
			else if (d2 == d4)
				d5 = 4D + (d - d1) / (d4 - d3);
		}
		d5 /= 6D;
		anInt394 = (int) (d5 * 256D);
		anInt395 = (int) (d6 * 256D);
		anInt396 = (int) (d7 * 256D);
		if (anInt395 < 0)
			anInt395 = 0;
		else if (anInt395 > 255)
			anInt395 = 255;
		if (anInt396 < 0)
			anInt396 = 0;
		else if (anInt396 > 255)
			anInt396 = 255;
		if (d7 > 0.5D)
			anInt398 = (int) ((1.0D - d7) * d6 * 512D);
		else
			anInt398 = (int) (d7 * d6 * 512D);
		if (anInt398 < 1)
			anInt398 = 1;
		anInt397 = (int) (d5 * (double) anInt398);
		anInt399 = method263(anInt394, anInt395, anInt396);
	}

	private void method262_2(int i) {
		double d = (double) (i >> 16 & 0xff) / 256D;
		double d1 = (double) (i >> 8 & 0xff) / 256D;
		double d2 = (double) (i & 0xff) / 256D;
		double d3 = d;
		if (d1 < d3)
			d3 = d1;
		if (d2 < d3)
			d3 = d2;
		double d4 = d;
		if (d1 > d4)
			d4 = d1;
		if (d2 > d4)
			d4 = d2;
		double d5 = 0.0D;
		double d6 = 0.0D;
		double d7 = (d3 + d4) / 2D;
		if (d3 != d4) {
			if (d7 < 0.5D)
				d6 = (d4 - d3) / (d4 + d3);
			if (d7 >= 0.5D)
				d6 = (d4 - d3) / (2D - d4 - d3);
			if (d == d4)
				d5 = (d1 - d2) / (d4 - d3);
			else if (d1 == d4)
				d5 = 2D + (d2 - d) / (d4 - d3);
			else if (d2 == d4)
				d5 = 4D + (d - d1) / (d4 - d3);
		}
		d5 /= 6D;
		anInt394_2 = (int) (d5 * 256D);
		anInt395_2 = (int) (d6 * 256D);
		anInt396_2 = (int) (d7 * 256D);
		if (anInt395_2 < 0)
			anInt395_2 = 0;
		else if (anInt395_2 > 255)
			anInt395_2 = 255;
		if (anInt396_2 < 0)
			anInt396_2 = 0;
		else if (anInt396_2 > 255)
			anInt396_2 = 255;
		if (d7 > 0.5D)
			anInt398_2 = (int) ((1.0D - d7) * d6 * 512D);
		else
			anInt398_2 = (int) (d7 * d6 * 512D);
		if (anInt398_2 < 1)
			anInt398_2 = 1;
		anInt397_2 = (int) (d5 * (double) anInt398_2);
		anInt399_2 = method263(anInt394_2, anInt395_2, anInt396_2);
	}

	private int method263(int i, int j, int k) {
		if (k > 179)
			j /= 2;
		if (k > 192)
			j /= 2;
		if (k > 217)
			j /= 2;
		if (k > 243)
			j /= 2;
		return (i / 4 << 10) + (j / 32 << 7) + k / 2;
	}

	private Floor() {
		anInt391 = -1;
		aBoolean393 = true;
	}

	public static Floor cache[];
	public int anInt390;
	public int overlay;
	public int minimapColour;
	public int anInt391;
	public boolean aBoolean393;
	public int anInt394;
	public int anInt395;
	public int anInt396;
	public int anInt397;
	public int anInt398;
	public int anInt399;
	public int anInt394_2;
	public int anInt395_2;
	public int anInt396_2;
	public int anInt397_2;
	public int anInt398_2;
	public int anInt399_2;
}
