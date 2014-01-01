/**
 * 
 * @author Joshua Barry <syipkpker>
 * 
 */
public final class BZ2Block {

	BZ2Block() {
		unZipTable = new int[256];
		cfTable = new int[257];
		inUse = new boolean[256];
		inUse16 = new boolean[16];
		seqToUnseq = new byte[256];
		aByteArray592 = new byte[4096];
		mtfSize16 = new int[16];
		aByteArray594 = new byte[18002];
		selectorMtf = new byte[18002];
		len = new byte[6][258];
		limit = new int[6][258];
		base = new int[6][258];
		perm = new int[6][258];
		minLengths = new int[6];
	}

	byte input[];
	int nextIn;
	int compressedSize;
	int totalInLo32;
	int totalInHi32;
	byte output[];
	int availOut;
	int decompressedSize;
	int totalOutLo32;
	int totalOutHi32;
	byte state_out_ch;
	int state_out_len;
	boolean randomized;
	int blockSizeBuffer;
	int blockSizeLive;
	int size;
	int blockNumber;
	int anInt580;
	int nextOut;
	int kVal;
	final int[] unZipTable;
	int nBlock_used;
	final int[] cfTable;
	public static int anIntArray587[];
	int nextInUse;
	final boolean[] inUse;
	final boolean[] inUse16;
	final byte[] seqToUnseq;
	final byte[] aByteArray592;
	final int[] mtfSize16;
	final byte[] aByteArray594;
	final byte[] selectorMtf;
	final byte[][] len;
	final int[][] limit;
	final int[][] base;
	final int[][] perm;
	final int[] minLengths;
	int blockPtr;
}
