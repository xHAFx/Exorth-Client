/**
 * 
 * @author Joshua Barry
 * 
 */
public final class Sequence {

	public static void unpackConfig(CacheArchive archive) {
		RSBuffer stream = new RSBuffer(archive.getDataForName("seq.dat"));
		int length = stream.readUShort();
		if (anims == null)
			anims = new Sequence[length];
		for (int j = 0; j < length; j++) {
			if (anims[j] == null)
				anims[j] = new Sequence();
			anims[j].readValues(stream);
			if (j >= 4937 && j <= 4982) {
				anims[j].walkable = 0;
			}
			// if (anims[j].anInt360 == 3017) {
			// System.err.println(j);
			// }
		}
	}

	public int getFrameLength(int i) {
		int j = delays[i];
		if (j == 0) {
			Animation class36 = Animation.method531(anIntArray353[i]);
			if (class36 != null)
				j = delays[i] = (short) class36.anInt636;
		}
		if (j == 0)
			j = 1;
		return j;
	}

	private void readValues(RSBuffer stream) {
		do {
			int i = stream.readUByte();
			if (i == 0)
				break;
			if (i == 1) {
				anInt352 = (short) stream.readUShort();
				anIntArray353 = new int[anInt352];
				anIntArray354 = new int[anInt352];
				delays = new short[anInt352];
				for (int j = 0; j < anInt352; j++) {
					delays[j] = (short) stream.readUShort();
					anIntArray354[j] = -1;
				}
				for (int j = 0; j < anInt352; j++)
					anIntArray353[j] = stream.readUShort();
				for (int i1 = 0; i1 < anInt352; i1++) {
					anIntArray353[i1] = (stream.readUShort() << 16) + anIntArray353[i1];
				}
			} else if (i == 2)
				anInt356 = (short) stream.readUShort();
			else if (i == 3) {
				int k = stream.readUByte();
				anIntArray357 = new int[k + 1];
				for (int l = 0; l < k; l++)
					anIntArray357[l] = stream.readUByte();
				anIntArray357[k] = 0x98967f;
			} else if (i == 4)
				aBoolean358 = true;
			else if (i == 5)
				anInt359 = (byte) stream.readUByte();
			else if (i == 6)
				anInt360 = (short) stream.readUShort();
			else if (i == 7)
				anInt361 = (short) stream.readUShort();
			else if (i == 8)
				anInt362 = (byte) stream.readUByte();
			else if (i == 9)
				anInt363 = (byte) stream.readUByte();
			else if (i == 10)
				walkable = (byte) stream.readUByte();
			else if (i == 11)
				anInt365 = (byte) stream.readUByte();
			else if (i == 12)
				stream.readInt();
			else
				System.out.println("Error unrecognised seq config code: " + i);
		} while (true);
		if (anInt352 == 0) {
			anInt352 = 1;
			anIntArray353 = new int[1];
			anIntArray353[0] = -1;
			anIntArray354 = new int[1];
			anIntArray354[0] = -1;
			delays = new short[1];
			delays[0] = -1;
		}
		if (anInt363 == -1)
			if (anIntArray357 != null)
				anInt363 = 2;
			else
				anInt363 = 0;
		if (walkable == -1) {
			if (anIntArray357 != null) {
				walkable = 2;
				return;
			}
			walkable = 0;
		}
	}

	private Sequence() {
		anInt356 = -1;
		aBoolean358 = false;
		anInt359 = 5;
		anInt360 = -1;
		anInt361 = -1;
		anInt362 = 99;
		anInt363 = -1;
		walkable = -1;
		anInt365 = 2;
	}

	public static Sequence anims[];
	public short anInt352;
	public int anIntArray353[];
	public int anIntArray354[];
	private short[] delays;
	public short anInt356;
	public int anIntArray357[];
	public boolean aBoolean358;
	public byte anInt359;
	public short anInt360;
	public short anInt361;
	public byte anInt362;
	public byte anInt363;
	public byte walkable;
	public byte anInt365;
	public static int anInt367;
}