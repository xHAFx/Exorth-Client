import java.util.logging.Logger;

public final class BZ2InputStream {

	public static int decompressBuffer(byte outputBuf[], int decompressedSize, byte inputBuff[], int compressedSize, int offset) {
		synchronized (block) {
			block.input = inputBuff;
			block.nextIn = offset;
			block.output = outputBuf;
			block.availOut = 0;
			block.compressedSize = compressedSize;
			block.decompressedSize = decompressedSize;
			block.blockSizeLive = 0;
			block.blockSizeBuffer = 0;
			block.totalInLo32 = 0;
			block.totalInHi32 = 0;
			block.totalOutLo32 = 0;
			block.totalOutHi32 = 0;
			block.blockNumber = 0;
			decompress(block);
			decompressedSize -= block.decompressedSize;
			return decompressedSize;
		}
	}

	private static void getNextFileHeader(BZ2Block block) {
		byte state_out_ch = block.state_out_ch;
		int state_out_len = block.state_out_len;
		int nBlock_used = block.nBlock_used;
		int k = block.kVal;
		int out[] = BZ2Block.anIntArray587;
		int nextOut = block.nextOut;
		byte outputBuffer[] = block.output;
		int availOut = block.availOut;
		int decompressedSize = block.decompressedSize;
		int decompSize = decompressedSize;
		int nBlockPtr = block.blockPtr + 1;
		label0: do {
			if (state_out_len > 0) {
				do {
					if (decompressedSize == 0)
						break label0;
					if (state_out_len == 1)
						break;
					outputBuffer[availOut] = state_out_ch;
					state_out_len--;
					availOut++;
					decompressedSize--;
				} while (true);
				if (decompressedSize == 0) {
					state_out_len = 1;
					break;
				}
				outputBuffer[availOut] = state_out_ch;
				availOut++;
				decompressedSize--;
			}
			boolean flag = true;
			while (flag) {
				flag = false;
				if (nBlock_used == nBlockPtr) {
					state_out_len = 0;
					break label0;
				}
				state_out_ch = (byte) k;
				nextOut = out[nextOut];
				byte byte0 = (byte) (nextOut & 0xff);
				nextOut >>= 8;
				nBlock_used++;
				if (byte0 != k) {
					k = byte0;
					if (decompressedSize == 0) {
						state_out_len = 1;
					} else {
						outputBuffer[availOut] = state_out_ch;
						availOut++;
						decompressedSize--;
						flag = true;
						continue;
					}
					break label0;
				}
				if (nBlock_used != nBlockPtr)
					continue;
				if (decompressedSize == 0) {
					state_out_len = 1;
					break label0;
				}
				outputBuffer[availOut] = state_out_ch;
				availOut++;
				decompressedSize--;
				flag = true;
			}
			state_out_len = 2;
			nextOut = out[nextOut];
			byte byte1 = (byte) (nextOut & 0xff);
			nextOut >>= 8;
			if (++nBlock_used != nBlockPtr)
				if (byte1 != k) {
					k = byte1;
				} else {
					state_out_len = 3;
					nextOut = out[nextOut];
					byte byte2 = (byte) (nextOut & 0xff);
					nextOut >>= 8;
					if (++nBlock_used != nBlockPtr)
						if (byte2 != k) {
							k = byte2;
						} else {
							nextOut = out[nextOut];
							byte byte3 = (byte) (nextOut & 0xff);
							nextOut >>= 8;
							nBlock_used++;
							state_out_len = (byte3 & 0xff) + 4;
							nextOut = out[nextOut];
							k = (byte) (nextOut & 0xff);
							nextOut >>= 8;
							nBlock_used++;
						}
				}
		} while (true);
		int i2 = block.totalOutLo32;
		block.totalOutLo32 += decompSize - decompressedSize;
		if (block.totalOutLo32 < i2)
			block.totalOutHi32++;
		block.state_out_ch = state_out_ch;
		block.state_out_len = state_out_len;
		block.nBlock_used = nBlock_used;
		block.kVal = k;
		BZ2Block.anIntArray587 = out;
		block.nextOut = nextOut;
		block.output = outputBuffer;
		block.availOut = availOut;
		block.decompressedSize = decompressedSize;
	}

	private static void decompress(BZ2Block block) {
		int tMinLength = 0;
		int tLimit[] = null;
		int tBase[] = null;
		int tPerm[] = null;
		block.size = 1;
		if (BZ2Block.anIntArray587 == null)
			BZ2Block.anIntArray587 = new int[block.size * 0x186a0];
		boolean reading = true;
		while (reading) {
			byte head = readUChar(block);
			if (head == 23)
				return;
			head = readUChar(block);
			head = readUChar(block);
			head = readUChar(block);
			head = readUChar(block);
			head = readUChar(block);
			block.blockNumber++;
			head = readUChar(block);
			head = readUChar(block);
			head = readUChar(block);
			head = readUChar(block);
			head = readBit(block);
			block.randomized = head != 0;
			if (block.randomized)
				Logger.getAnonymousLogger().severe("PANIC! RANDOMISED BLOCK!");
			block.anInt580 = 0;
			head = readUChar(block);
			block.anInt580 = block.anInt580 << 8 | head & 0xff;
			head = readUChar(block);
			block.anInt580 = block.anInt580 << 8 | head & 0xff;
			head = readUChar(block);
			block.anInt580 = block.anInt580 << 8 | head & 0xff;
			for (int j = 0; j < 16; j++) {
				byte byte1 = readBit(block);
				block.inUse16[j] = byte1 == 1;
			}
			for (int k = 0; k < 256; k++)
				block.inUse[k] = false;
			for (int l = 0; l < 16; l++)
				if (block.inUse16[l]) {
					for (int i3 = 0; i3 < 16; i3++) {
						byte byte2 = readBit(block);
						if (byte2 == 1)
							block.inUse[l * 16 + i3] = true;
					}
				}
			createMaps(block);
			int alphaSize = block.nextInUse + 2;
			int groups = getBits(3, block);
			int selectors = getBits(15, block);
			for (int i1 = 0; i1 < selectors; i1++) {
				int j3 = 0;
				do {
					byte byte3 = readBit(block);
					if (byte3 == 0)
						break;
					j3++;
				} while (true);
				block.selectorMtf[i1] = (byte) j3;
			}
			byte pos[] = new byte[6];
			for (byte byte16 = 0; byte16 < groups; byte16++)
				pos[byte16] = byte16;
			for (int j1 = 0; j1 < selectors; j1++) {
				byte selectorMtf = block.selectorMtf[j1];
				byte currentSelectorMtf = pos[selectorMtf];
				for (; selectorMtf > 0; selectorMtf--)
					pos[selectorMtf] = pos[selectorMtf - 1];
				pos[0] = currentSelectorMtf;
				block.aByteArray594[j1] = currentSelectorMtf;
			}
			for (int k3 = 0; k3 < groups; k3++) {
				int current = getBits(5, block);
				for (int k1 = 0; k1 < alphaSize; k1++) {
					do {
						byte flag = readBit(block);
						if (flag == 0)
							break;
						flag = readBit(block);
						if (flag == 0)
							current++;
						else
							current--;
					} while (true);
					block.len[k3][k1] = (byte) current;
				}
			}
			for (int l3 = 0; l3 < groups; l3++) {
				byte minLength = 32;
				int maxLength = 0;
				for (int l1 = 0; l1 < alphaSize; l1++) {
					if (block.len[l3][l1] > maxLength)
						maxLength = block.len[l3][l1];
					if (block.len[l3][l1] < minLength)
						minLength = block.len[l3][l1];
				}
				createDecodeTables(block.limit[l3], block.base[l3], block.perm[l3], block.len[l3], minLength, maxLength, alphaSize);
				block.minLengths[l3] = minLength;
			}
			int endOfBlock = block.nextInUse + 1;
			@SuppressWarnings("unused")
			int l5 = 0x186a0 * block.size;
			int groupNo = -1;
			int groupPos = 0;
			for (int i2 = 0; i2 <= 255; i2++)
				block.unZipTable[i2] = 0;
			int j9 = 4095;
			for (int l8 = 15; l8 >= 0; l8--) {
				for (int i9 = 15; i9 >= 0; i9--) {
					block.aByteArray592[j9] = (byte) (l8 * 16 + i9);
					j9--;
				}
				block.mtfSize16[l8] = j9 + 1;
			}
			int i6 = 0;
			if (groupPos == 0) {
				groupNo++;
				groupPos = 50;
				byte byte12 = block.aByteArray594[groupNo];
				tMinLength = block.minLengths[byte12];
				tLimit = block.limit[byte12];
				tPerm = block.perm[byte12];
				tBase = block.base[byte12];
			}
			groupPos--;
			int index = tMinLength;
			int zVec;
			byte bit;
			for (zVec = getBits(index, block); zVec > tLimit[index]; zVec = zVec << 1 | bit) {
				index++;
				bit = readBit(block);
			}
			for (int k5 = tPerm[zVec - tBase[index]]; k5 != endOfBlock;)
				if (k5 == 0 || k5 == 1) {
					int j6 = -1;
					int k6 = 1;
					do {
						if (k5 == 0)
							j6 += k6;
						else if (k5 == 1)
							j6 += 2 * k6;
						k6 *= 2;
						if (groupPos == 0) {
							groupNo++;
							groupPos = 50;
							byte byte13 = block.aByteArray594[groupNo];
							tMinLength = block.minLengths[byte13];
							tLimit = block.limit[byte13];
							tPerm = block.perm[byte13];
							tBase = block.base[byte13];
						}
						groupPos--;
						int j7 = tMinLength;
						int i8;
						byte byte10;
						for (i8 = getBits(j7, block); i8 > tLimit[j7]; i8 = i8 << 1 | byte10) {
							j7++;
							byte10 = readBit(block);
						}
						k5 = tPerm[i8 - tBase[j7]];
					} while (k5 == 0 || k5 == 1);
					j6++;
					byte ch = block.seqToUnseq[block.aByteArray592[block.mtfSize16[0]] & 0xff];
					block.unZipTable[ch & 0xff] += j6;
					for (; j6 > 0; j6--) {
						BZ2Block.anIntArray587[i6] = ch & 0xff;
						i6++;
					}
				} else {
					int j11 = k5 - 1;
					byte byte6;
					if (j11 < 16) {
						int j10 = block.mtfSize16[0];
						byte6 = block.aByteArray592[j10 + j11];
						for (; j11 > 3; j11 -= 4) {
							int k11 = j10 + j11;
							block.aByteArray592[k11] = block.aByteArray592[k11 - 1];
							block.aByteArray592[k11 - 1] = block.aByteArray592[k11 - 2];
							block.aByteArray592[k11 - 2] = block.aByteArray592[k11 - 3];
							block.aByteArray592[k11 - 3] = block.aByteArray592[k11 - 4];
						}
						for (; j11 > 0; j11--)
							block.aByteArray592[j10 + j11] = block.aByteArray592[(j10 + j11) - 1];
						block.aByteArray592[j10] = byte6;
					} else {
						int l10 = j11 / 16;
						int i11 = j11 % 16;
						int k10 = block.mtfSize16[l10] + i11;
						byte6 = block.aByteArray592[k10];
						for (; k10 > block.mtfSize16[l10]; k10--)
							block.aByteArray592[k10] = block.aByteArray592[k10 - 1];
						block.mtfSize16[l10]++;
						for (; l10 > 0; l10--) {
							block.mtfSize16[l10]--;
							block.aByteArray592[block.mtfSize16[l10]] = block.aByteArray592[(block.mtfSize16[l10 - 1] + 16) - 1];
						}
						block.mtfSize16[0]--;
						block.aByteArray592[block.mtfSize16[0]] = byte6;
						if (block.mtfSize16[0] == 0) {
							int i10 = 4095;
							for (int k9 = 15; k9 >= 0; k9--) {
								for (int l9 = 15; l9 >= 0; l9--) {
									block.aByteArray592[i10] = block.aByteArray592[block.mtfSize16[k9] + l9];
									i10--;
								}
								block.mtfSize16[k9] = i10 + 1;
							}
						}
					}
					block.unZipTable[block.seqToUnseq[byte6 & 0xff] & 0xff]++;
					BZ2Block.anIntArray587[i6] = block.seqToUnseq[byte6 & 0xff] & 0xff;
					i6++;
					if (groupPos == 0) {
						groupNo++;
						groupPos = 50;
						byte byte14 = block.aByteArray594[groupNo];
						tMinLength = block.minLengths[byte14];
						tLimit = block.limit[byte14];
						tPerm = block.perm[byte14];
						tBase = block.base[byte14];
					}
					groupPos--;
					int k7 = tMinLength;
					int j8;
					byte byte11;
					for (j8 = getBits(k7, block); j8 > tLimit[k7]; j8 = j8 << 1 | byte11) {
						k7++;
						byte11 = readBit(block);
					}
					k5 = tPerm[j8 - tBase[k7]];
				}
			block.state_out_len = 0;
			block.state_out_ch = 0;
			block.cfTable[0] = 0;
			for (int j2 = 1; j2 <= 256; j2++)
				block.cfTable[j2] = block.unZipTable[j2 - 1];
			for (int k2 = 1; k2 <= 256; k2++)
				block.cfTable[k2] += block.cfTable[k2 - 1];
			for (int l2 = 0; l2 < i6; l2++) {
				byte byte7 = (byte) (BZ2Block.anIntArray587[l2] & 0xff);
				BZ2Block.anIntArray587[block.cfTable[byte7 & 0xff]] |= l2 << 8;
				block.cfTable[byte7 & 0xff]++;
			}
			block.nextOut = BZ2Block.anIntArray587[block.anInt580] >> 8;
			block.nBlock_used = 0;
			block.nextOut = BZ2Block.anIntArray587[block.nextOut];
			block.kVal = (byte) (block.nextOut & 0xff);
			block.nextOut >>= 8;
			block.nBlock_used++;
			block.blockPtr = i6;
			getNextFileHeader(block);
			reading = block.nBlock_used == block.blockPtr + 1 && block.state_out_len == 0;
		}
	}

	private static byte readUChar(BZ2Block block) {
		return (byte) getBits(8, block);
	}

	private static byte readBit(BZ2Block block) {
		return (byte) getBits(1, block);
	}

	private static int getBits(int i, BZ2Block block) {
		int j;
		do {
			if (block.blockSizeLive >= i) {
				int k = block.blockSizeBuffer >> block.blockSizeLive - i & (1 << i) - 1;
				block.blockSizeLive -= i;
				j = k;
				break;
			}
			block.blockSizeBuffer = block.blockSizeBuffer << 8 | block.input[block.nextIn] & 0xff;
			block.blockSizeLive += 8;
			block.nextIn++;
			block.compressedSize--;
			block.totalInLo32++;
			if (block.totalInLo32 == 0)
				block.totalInHi32++;
		} while (true);
		return j;
	}

	private static void createMaps(BZ2Block block) {
		block.nextInUse = 0;
		for (int i = 0; i < 256; i++)
			if (block.inUse[i]) {
				block.seqToUnseq[block.nextInUse] = (byte) i;
				block.nextInUse++;
			}
	}

	private static void createDecodeTables(int limit[], int base[], int perm[], byte len[], int minLength, int maxLen, int alphaSize) {
		int l = 0;
		for (int i1 = minLength; i1 <= maxLen; i1++) {
			for (int l2 = 0; l2 < alphaSize; l2++)
				if (len[l2] == i1) {
					perm[l] = l2;
					l++;
				}
		}
		for (int j1 = 0; j1 < 23; j1++)
			base[j1] = 0;
		for (int k1 = 0; k1 < alphaSize; k1++)
			base[len[k1] + 1]++;
		for (int l1 = 1; l1 < 23; l1++)
			base[l1] += base[l1 - 1];
		for (int i2 = 0; i2 < 23; i2++)
			limit[i2] = 0;
		int i3 = 0;
		for (int j2 = minLength; j2 <= maxLen; j2++) {
			i3 += base[j2 + 1] - base[j2];
			limit[j2] = i3 - 1;
			i3 <<= 1;
		}
		for (int k2 = minLength + 1; k2 <= maxLen; k2++)
			base[k2] = (limit[k2 - 1] + 1 << 1) - base[k2];
	}

	private static final BZ2Block block = new BZ2Block();
}
