import java.math.BigInteger;

public final class RSBuffer extends QueueNode {

	private static final BigInteger RSA_MODULUS = new BigInteger("143690958001225849100503496893758066948984921380482659564113596152800934352119496873386875214251264258425208995167316497331786595942754290983849878549630226741961610780416197036711585670124061149988186026407785250364328460839202438651793652051153157765358767514800252431284681765433239888090564804146588087023");

	private static final BigInteger RSA_EXPONENT = new BigInteger("65537");


	public static RSBuffer create() {
		synchronized (buffers) {
			RSBuffer buffer = null;
			if (anInt1412 > 0) {
				anInt1412--;
				buffer = (RSBuffer) buffers.popHead();
			}
			if (buffer != null) {
				buffer.pointer = 0;
				return buffer;
			}
		}
		RSBuffer buffer = new RSBuffer();
		buffer.pointer = 0;
		buffer.buffer = new byte[5000];
		return buffer;
	}

	public RSBuffer() {
		/* Empty */
	}

	public RSBuffer(byte[] data) {
		buffer = data;
		pointer = 0;
	}

	public int readU24Int(int i) {
		pointer += 3;
		return (0xff & buffer[pointer - 3] << 16) + (0xff & buffer[pointer - 2] << 8) + (0xff & buffer[pointer - 1]);
	}

	public void writeOpcode(int i) {
		buffer[pointer++] = (byte) (i + encryption.getNextKey());
	}

	public void writeByte(int i) {
		buffer[pointer++] = (byte) i;
	}

	public int readUSmart2() {
		int baseVal = 0;
		int lastVal = 0;
		while ((lastVal = readSmarts()) == 32767) {
			baseVal += 32767;
		}
		return baseVal + lastVal;
	}

	public String readNewString() {
		int i = pointer;
		while (buffer[pointer++] != 0) {
			/* Empty */
		}
		return new String(buffer, i, pointer - i - 1);
	}

	public void writeShort(int i) {
		buffer[pointer++] = (byte) (i >> 8);
		buffer[pointer++] = (byte) i;
	}

	public void writeLEShort(int i) {
		buffer[pointer++] = (byte) i;
		buffer[pointer++] = (byte) (i >> 8);
	}

	public void write24Int(int i) {
		buffer[pointer++] = (byte) (i >> 16);
		buffer[pointer++] = (byte) (i >> 8);
		buffer[pointer++] = (byte) i;
	}

	public void writeInt(int i) {
		buffer[pointer++] = (byte) (i >> 24);
		buffer[pointer++] = (byte) (i >> 16);
		buffer[pointer++] = (byte) (i >> 8);
		buffer[pointer++] = (byte) i;
	}

	public void writeSpaceSaver(int val) {
		buffer[pointer++] = (byte) ((val >> 8) + 1);
		buffer[pointer++] = (byte) val;
	}

	public void writeLEInt(int j) {
		buffer[pointer++] = (byte) j;
		buffer[pointer++] = (byte) (j >> 8);
		buffer[pointer++] = (byte) (j >> 16);
		buffer[pointer++] = (byte) (j >> 24);
	}

	public void writeLong(long l) {
		buffer[pointer++] = (byte) (int) (l >> 56);
		buffer[pointer++] = (byte) (int) (l >> 48);
		buffer[pointer++] = (byte) (int) (l >> 40);
		buffer[pointer++] = (byte) (int) (l >> 32);
		buffer[pointer++] = (byte) (int) (l >> 24);
		buffer[pointer++] = (byte) (int) (l >> 16);
		buffer[pointer++] = (byte) (int) (l >> 8);
		buffer[pointer++] = (byte) (int) l;
	}

	public void writeString(String s) {
		System.arraycopy(s.getBytes(), 0, buffer, pointer, s.length());
		pointer += s.length();
		buffer[pointer++] = 10;
	}

	public void writeBytes(byte[] src, int length, int offset) {
		for (int ptr = offset; ptr < offset + length; ptr++) {
			buffer[pointer++] = src[ptr];
		}
	}

	public void writeBytes(int i) {
		buffer[pointer - i - 1] = (byte) i;
	}

	public int readUByte() {
		return buffer[pointer++] & 0xff;
	}

	public byte readByte() {
		return buffer[pointer++];
	}

	public int readUShort() {
		pointer += 2;
		return ((buffer[pointer - 2] & 0xff) << 8) + (buffer[pointer - 1] & 0xff);
	}

	public int readShort() {
		pointer += 2;
		int i = ((buffer[pointer - 2] & 0xff) << 8) + (buffer[pointer - 1] & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int read24Int() {
		pointer += 3;
		return ((buffer[pointer - 3] & 0xff) << 16) + ((buffer[pointer - 2] & 0xff) << 8) + (buffer[pointer - 1] & 0xff);
	}

	public final int readMediumInt() {
		pointer += 3;
		return (0xff & buffer[pointer - 1]) + ((buffer[pointer - 3] << 16 & 0xff0000) + (0xff00 & buffer[pointer - 2] << 8));
	}

	public int readInt() {
		pointer += 4;
		return ((buffer[pointer - 4] & 0xff) << 24) + ((buffer[pointer - 3] & 0xff) << 16) + ((buffer[pointer - 2] & 0xff) << 8)
				+ (buffer[pointer - 1] & 0xff);
	}

	public long readLong() {
		long l = (long) readInt() & 0xffffffffL;
		long l1 = (long) readInt() & 0xffffffffL;
		return (l << 32) + l1;
	}

	public String readString() {
		int i = pointer;
		while (buffer[pointer++] != 10) {
			/* Empty */
		}
		return new String(buffer, i, pointer - i - 1);
	}

	public boolean readBoolean() {
		int b = buffer[pointer++] & 0xff;
		return (b == 1);
	}

	public byte[] readBytes() {
		int offset = pointer;
		while (buffer[pointer++] != 10) {
			/* Empty */
		}
		byte[] dest = new byte[pointer - offset - 1];
		System.arraycopy(buffer, offset, dest, offset - offset, pointer - 1 - offset);
		return dest;
	}

	public void readBytes(int length, int offset, byte[] dest) {
		for (int ptr = offset; ptr < offset + length; ptr++) {
			dest[ptr] = buffer[pointer++];
		}
	}

	public void initBitAccess() {
		bitPosition = pointer * 8;
	}

	public int readBits(int i) {
		int k = bitPosition >> 3;
		int l = 8 - (bitPosition & 7);
		int i1 = 0;
		bitPosition += i;
		for (; i > l; l = 8) {
			i1 += (buffer[k++] & anIntArray1409[l]) << i - l;
			i -= l;
		}
		if (i == l)
			i1 += buffer[k] & anIntArray1409[l];
		else
			i1 += buffer[k] >> l - i & anIntArray1409[i];
		return i1;
	}

	public void finishBitAccess() {
		pointer = (bitPosition + 7) / 8;
	}

	public int method421() {
		int i = buffer[pointer] & 0xff;
		if (i < 128)
			return readUByte() - 64;
		else
			return readUShort() - 49152;
	}

	public int readSmarts() {
		int i = buffer[pointer] & 0xff;
		if (i < 128)
			return readUByte();
		else
			return readUShort() - 32768;
	}

	public void applyRSA() {
		int i = pointer;
		pointer = 0;
		byte abyte0[] = new byte[i];
		readBytes(i, 0, abyte0);
		BigInteger biginteger2 = new BigInteger(abyte0);
		BigInteger biginteger3 = biginteger2.modPow(RSA_EXPONENT, RSA_MODULUS);
		byte abyte1[] = biginteger3.toByteArray();
		pointer = 0;
		writeByte(abyte1.length);
		writeBytes(abyte1, abyte1.length, 0);
	}

	public void readInverseByte(int i) {
		buffer[pointer++] = (byte) (-i);
	}

	public void method425(int j) {
		buffer[pointer++] = (byte) (128 - j);
	}

	public int method426() {
		return buffer[pointer++] - 128 & 0xff;
	}

	public int method427() {
		return -buffer[pointer++] & 0xff;
	}

	public int method428() {
		return 128 - buffer[pointer++] & 0xff;
	}

	public byte method429() {
		return (byte) (-buffer[pointer++]);
	}

	public byte method430() {
		return (byte) (128 - buffer[pointer++]);
	}

	public void method431(int i) {
		buffer[pointer++] = (byte) i;
		buffer[pointer++] = (byte) (i >> 8);
	}

	public void method432(int j) {
		buffer[pointer++] = (byte) (j >> 8);
		buffer[pointer++] = (byte) (j + 128);
	}

	public void method433(int j) {
		buffer[pointer++] = (byte) (j + 128);
		buffer[pointer++] = (byte) (j >> 8);
	}

	public int method434() {
		pointer += 2;
		return ((buffer[pointer - 1] & 0xff) << 8) + (buffer[pointer - 2] & 0xff);
	}

	public int method435() {
		pointer += 2;
		return ((buffer[pointer - 2] & 0xff) << 8) + (buffer[pointer - 1] - 128 & 0xff);
	}

	public int method436() {
		pointer += 2;
		return ((buffer[pointer - 1] & 0xff) << 8) + (buffer[pointer - 2] - 128 & 0xff);
	}

	public int method437() {
		pointer += 2;
		int j = ((buffer[pointer - 1] & 0xff) << 8) + (buffer[pointer - 2] & 0xff);
		if (j > 32767)
			j -= 0x10000;
		return j;
	}

	public int method438() {
		pointer += 2;
		int j = ((buffer[pointer - 1] & 0xff) << 8) + (buffer[pointer - 2] - 128 & 0xff);
		if (j > 32767)
			j -= 0x10000;
		return j;
	}

	public int method439() {
		pointer += 4;
		return ((buffer[pointer - 2] & 0xff) << 24) + ((buffer[pointer - 1] & 0xff) << 16) + ((buffer[pointer - 4] & 0xff) << 8)
				+ (buffer[pointer - 3] & 0xff);
	}

	public int method440() {
		pointer += 4;
		return ((buffer[pointer - 3] & 0xff) << 24) + ((buffer[pointer - 4] & 0xff) << 16) + ((buffer[pointer - 1] & 0xff) << 8)
				+ (buffer[pointer - 2] & 0xff);
	}

	public void method441(int i, byte abyte0[], int j) {
		for (int k = (i + j) - 1; k >= i; k--)
			buffer[pointer++] = (byte) (abyte0[k] + 128);
	}

	public void method442(int i, int j, byte abyte0[]) {
		for (int k = (j + i) - 1; k >= j; k--)
			abyte0[k] = buffer[pointer++];
	}

	public byte buffer[];
	public int pointer;
	public int bitPosition;
	public static final int[] anIntArray1409 = { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535,
			0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff,
			0x1fffffff, 0x3fffffff, 0x7fffffff, -1 };
	public ISAACGenerator encryption;
	public static int anInt1412;
	public static final Deque buffers = new Deque();
}
