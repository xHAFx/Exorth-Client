import java.util.Hashtable;

/**
 * 
 * @author Joshua Barry
 * 
 */
public final class Animation {

	private static Hashtable<Integer, Animation> frameList = new Hashtable<Integer, Animation>();

	public static void load(byte abyte0[], int fileId) {
		RSBuffer stream = new RSBuffer(abyte0);
		stream.pointer = abyte0.length - 12;
		int i = stream.readInt();
		int j = stream.readInt();
		int k = stream.readInt();
		int i1 = 0;
		RSBuffer stream_1 = new RSBuffer(abyte0);
		stream_1.pointer = i1;
		i1 += i + 4;
		RSBuffer stream_2 = new RSBuffer(abyte0);
		stream_2.pointer = i1;
		i1 += j;
		RSBuffer stream_3 = new RSBuffer(abyte0);
		stream_3.pointer = i1;
		i1 += k;
		RSBuffer stream_4 = new RSBuffer(abyte0);
		stream_4.pointer = i1;
		SkinList class18 = new SkinList(stream_4);
		int k1 = stream_1.readInt();
		short ai[] = new short[500];
		short ai1[] = new short[500];
		short ai2[] = new short[500];
		short ai3[] = new short[500];
		for (int l1 = 0; l1 < k1; l1++) {
			int i2 = stream_1.readInt();
			Animation class36 = new Animation();
			frameList.put(new Integer((fileId << 16) + i2), class36);
			class36.aClass18_637 = class18;
			int j2 = stream_1.readUByte();
			int k2 = -1;
			int l2 = 0;
			for (int i3 = 0; i3 < j2; i3++) {
				int j3 = stream_2.readUByte();
				if (j3 > 0) {
					if (class18.anIntArray342[i3] != 0) {
						for (int l3 = i3 - 1; l3 > k2; l3--) {
							if (class18.anIntArray342[l3] != 0)
								continue;
							ai[l2] = (short) l3;
							ai1[l2] = 0;
							ai2[l2] = 0;
							ai3[l2] = 0;
							l2++;
							break;
						}
					}
					ai[l2] = (short) i3;
					char c = '\0';
					if (class18.anIntArray342[i3] == 3)
						c = '\200';
					if ((j3 & 1) != 0)
						ai1[l2] = (short) stream_3.method421();
					else
						ai1[l2] = (short) c;
					if ((j3 & 2) != 0)
						ai2[l2] = (short) stream_3.method421();
					else
						ai2[l2] = (short) c;
					if ((j3 & 4) != 0)
						ai3[l2] = (short) stream_3.method421();
					else
						ai3[l2] = (short) c;
					k2 = i3;
					l2++;
				}
			}
			class36.anInt638 = (short) l2;
			class36.anIntArray639 = new short[l2];
			class36.anIntArray640 = new short[l2];
			class36.anIntArray641 = new short[l2];
			class36.anIntArray642 = new short[l2];
			for (int k3 = 0; k3 < l2; k3++) {
				class36.anIntArray639[k3] = ai[k3];
				class36.anIntArray640[k3] = ai1[k3];
				class36.anIntArray641[k3] = ai2[k3];
				class36.anIntArray642[k3] = ai3[k3];
			}
		}
	}

	public static void nullLoader() {
		frameList = null;
	}

	public static Animation method531(int j) {
		int fileId = j >> 16;
		Animation class36 = (Animation) frameList.get(j);
		if (class36 == null) {
			RSClient.instance.resourceProvider.method558(1, fileId);
			return null;
		}
		return class36;
	}

	public static boolean method532(int i) {
		return i == -1;
	}

	private Animation() {
	}

	public short anInt636;
	public SkinList aClass18_637;
	public short anInt638;
	public short anIntArray639[];
	public short anIntArray640[];
	public short anIntArray641[];
	public short anIntArray642[];
}