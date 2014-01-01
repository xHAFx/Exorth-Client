public final class VarBit {

	public static void unpackConfig(CacheArchive streamLoader) {
		RSBuffer stream = new RSBuffer(streamLoader.getDataForName("varbit.dat"));
		int cacheSize = stream.readUShort();
		if (cache == null)
			cache = new VarBit[cacheSize];
		for (int j = 0; j < cacheSize; j++) {
			if (cache[j] == null)
				cache[j] = new VarBit();
			cache[j].readValues(stream);
			if (cache[j].aBoolean651)
				Varp.cache[cache[j].configId].aBoolean713 = true;
		}
		if (stream.pointer != stream.buffer.length)
			System.out.println("varbit load mismatch");
	}

	private void readValues(RSBuffer stream) {
		do {
			int j = stream.readUByte();
			if (j == 0)
				return;
			if (j == 1) {
				configId = stream.readUShort();
				leastSignificantBit = stream.readUByte();
				mostSignificantBit = stream.readUByte();
			} else if (j == 10)
				stream.readString();
			else if (j == 2)
				aBoolean651 = true;
			else if (j == 3)
				stream.readInt();
			else if (j == 4)
				stream.readInt();
			else
				System.out.println("Error unrecognised config code: " + j);
		} while (true);
	}

	private VarBit() {
		aBoolean651 = false;
	}

	public static VarBit cache[];
	public int configId;
	public int leastSignificantBit;
	public int mostSignificantBit;
	private boolean aBoolean651;
}
