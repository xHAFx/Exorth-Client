public class CacheArchive {

	public CacheArchive(byte abyte0[]) {
		RSBuffer stream = new RSBuffer(abyte0);
		int i = stream.read24Int();
		int j = stream.read24Int();
		if (j != i) {
			byte abyte1[] = new byte[i];
			BZ2InputStream.decompressBuffer(abyte1, i, abyte0, j, 6);
			aByteArray726 = abyte1;
			stream = new RSBuffer(aByteArray726);
			aBoolean732 = true;
		} else {
			aByteArray726 = abyte0;
			aBoolean732 = false;
		}
		dataSize = stream.readUShort();
		anIntArray728 = new int[dataSize];
		anIntArray729 = new int[dataSize];
		anIntArray730 = new int[dataSize];
		anIntArray731 = new int[dataSize];
		int k = stream.pointer + dataSize * 10;
		for (int l = 0; l < dataSize; l++) {
			anIntArray728[l] = stream.readInt();
			anIntArray729[l] = stream.read24Int();
			anIntArray730[l] = stream.read24Int();
			anIntArray731[l] = k;
			k += anIntArray730[l];
		}
	}

	public byte[] getDataForName(String s) {
		byte abyte0[] = null; // was a parameter
		int i = 0;
		s = s.toUpperCase();
		for (int j = 0; j < s.length(); j++)
			i = (i * 61 + s.charAt(j)) - 32;
		for (int k = 0; k < dataSize; k++)
			if (anIntArray728[k] == i) {
				if (abyte0 == null)
					abyte0 = new byte[anIntArray729[k]];
				if (!aBoolean732) {
					BZ2InputStream.decompressBuffer(abyte0, anIntArray729[k], aByteArray726, anIntArray730[k], anIntArray731[k]);
				} else {
					System.arraycopy(aByteArray726, anIntArray731[k], abyte0, 0, anIntArray729[k]);
				}
				return abyte0;
			}
		return null;
	}

	public byte[] aByteArray726;
	public int dataSize;
	public int[] anIntArray728;
	public int[] anIntArray729;
	public int[] anIntArray730;
	public int[] anIntArray731;
	public boolean aBoolean732;
}
