/**
 * 
 * @author Joshua Barry
 * 
 */
public class Sound {

	private Sound() {
		samples = new Synthesizer[10];
	}

	public static void unpack(RSBuffer stream) {
		output = new byte[0x6baa8];
		buffer = new RSBuffer(output);
		Synthesizer.initialize();
		do {
			int j = stream.readUShort();
			if (j == 65535)
				return;
			soundTracks[j] = new Sound();
			soundTracks[j].decode(stream);
			anIntArray326[j] = soundTracks[j].method243();
		} while (true);
	}

	public static RSBuffer getData(int volume, int id) {
		if (soundTracks[id] != null) {
			Sound sounds = soundTracks[id];
			return sounds.pack(volume);
		} else {
			return null;
		}
	}

	private void decode(RSBuffer stream) {
		for (int i = 0; i < 10; i++) {
			int j = stream.readUByte();
			if (j != 0) {
				stream.pointer--;
				samples[i] = new Synthesizer();
				samples[i].method169(stream);
			}
		}
		anInt330 = stream.readUShort();
		anInt331 = stream.readUShort();
	}

	private int method243() {
		int j = 0x98967f;
		for (int k = 0; k < 10; k++)
			if (samples[k] != null && samples[k].remaining / 20 < j)
				j = samples[k].remaining / 20;
		if (anInt330 < anInt331 && anInt330 / 20 < j)
			j = anInt330 / 20;
		if (j == 0x98967f || j == 0)
			return 0;
		for (int l = 0; l < 10; l++)
			if (samples[l] != null)
				samples[l].remaining -= j * 20;
		if (anInt330 < anInt331) {
			anInt330 -= j * 20;
			anInt331 -= j * 20;
		}
		return j;
	}

	private RSBuffer pack(int volume) {
		int subChunkSize = encode(volume);
		buffer.pointer = 0;
		buffer.writeInt(0x52494646);
		buffer.writeLEInt(36 + subChunkSize);
		buffer.writeInt(0x57415645);
		buffer.writeInt(0x666d7420);
		buffer.writeLEInt(16);
		buffer.writeLEShort(1);
		buffer.writeLEShort(1);
		buffer.writeLEInt(22050);
		buffer.writeLEInt(22050);
		buffer.writeLEShort(1);
		buffer.writeLEShort(8);
		buffer.writeInt(0x64617461);
		buffer.writeLEInt(subChunkSize);
		buffer.pointer += subChunkSize;
		return buffer;
	}

	private int encode(int i) {
		int j = 0;
		for (int k = 0; k < 10; k++)
			if (samples[k] != null && samples[k].offset + samples[k].remaining > j)
				j = samples[k].offset + samples[k].remaining;
		if (j == 0)
			return 0;
		int l = (22050 * j) / 1000;
		int i1 = (22050 * anInt330) / 1000;
		int j1 = (22050 * anInt331) / 1000;
		if (i1 < 0 || i1 > l || j1 < 0 || j1 > l || i1 >= j1)
			i = 0;
		int k1 = l + (j1 - i1) * (i - 1);
		for (int l1 = 44; l1 < k1 + 44; l1++)
			output[l1] = -128;
		for (int i2 = 0; i2 < 10; i2++)
			if (samples[i2] != null) {
				int j2 = (samples[i2].offset * 22050) / 1000;
				int i3 = (samples[i2].remaining * 22050) / 1000;
				int ai[] = samples[i2].method167(j2, samples[i2].offset);
				for (int l3 = 0; l3 < j2; l3++)
					output[l3 + i3 + 44] += (byte) (ai[l3] >> 8);
			}
		if (i > 1) {
			i1 += 44;
			j1 += 44;
			l += 44;
			int k2 = (k1 += 44) - l;
			for (int j3 = l - 1; j3 >= j1; j3--)
				output[j3 + k2] = output[j3];
			for (int k3 = 1; k3 < i; k3++) {
				int l2 = (j1 - i1) * k3;
				System.arraycopy(output, i1, output, i1 + l2, j1 - i1);
			}
			k1 -= 44;
		}
		return k1;
	}

	private static final Sound[] soundTracks = new Sound[5000];
	public static final int[] anIntArray326 = new int[5000];
	private static byte[] output;
	private static RSBuffer buffer;
	private final Synthesizer[] samples;
	private int anInt330;
	private int anInt331;
}
