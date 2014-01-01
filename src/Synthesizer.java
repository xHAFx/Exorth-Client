import java.util.Random;

final class Synthesizer {

	public static void initialize() {
		NOISE = new int[32768];
		Random random = new Random(0L);
		for (int i = 0; i < 32768; i++)
			NOISE[i] = (random.nextInt() & 2) - 1;
		SINE = new int[32768];
		for (int j = 0; j < 32768; j++)
			SINE[j] = (int) (Math.sin((double) j / 5215.1903000000002D) * 16384D);
		samples = new int[0x35d54];
	}

	public int[] method167(int i, int j) {
		for (int k = 0; k < i; k++)
			samples[k] = 0;
		if (j < 10)
			return samples;
		double d = (double) i / ((double) j + 0.0D);
		aClass29_98.resetValues();
		aClass29_99.resetValues();
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		if (envolope != null) {
			envolope.resetValues();
			aClass29_101.resetValues();
			l = (int) (((double) (envolope.anInt539 - envolope.anInt538) * 32.768000000000001D) / d);
			i1 = (int) (((double) envolope.anInt538 * 32.768000000000001D) / d);
		}
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		if (aClass29_102 != null) {
			aClass29_102.resetValues();
			aClass29_103.resetValues();
			k1 = (int) (((double) (aClass29_102.anInt539 - aClass29_102.anInt538) * 32.768000000000001D) / d);
			l1 = (int) (((double) aClass29_102.anInt538 * 32.768000000000001D) / d);
		}
		for (int j2 = 0; j2 < 5; j2++)
			if (anIntArray106[j2] != 0) {
				phase[j2] = 0;
				anIntArray119[j2] = (int) ((double) anIntArray108[j2] * d);
				anIntArray120[j2] = (anIntArray106[j2] << 14) / 100;
				anIntArray121[j2] = (int) (((double) (aClass29_98.anInt539 - aClass29_98.anInt538) * 32.768000000000001D * Math.pow(
						1.0057929410678534D, cents[j2])) / d);
				anIntArray122[j2] = (int) (((double) aClass29_98.anInt538 * 32.768000000000001D) / d);
			}
		for (int k2 = 0; k2 < i; k2++) {
			int l2 = aClass29_98.evaluate(i);
			int j4 = aClass29_99.evaluate(i);
			if (envolope != null) {
				int j5 = envolope.evaluate(i);
				int j6 = aClass29_101.evaluate(i);
				l2 += method168(j6, j1, envolope.anInt540) >> 1;
				j1 += (j5 * l >> 16) + i1;
			}
			if (aClass29_102 != null) {
				int k5 = aClass29_102.evaluate(i);
				int k6 = aClass29_103.evaluate(i);
				j4 = j4 * ((method168(k6, i2, aClass29_102.anInt540) >> 1) + 32768) >> 15;
				i2 += (k5 * k1 >> 16) + l1;
			}
			for (int l5 = 0; l5 < 5; l5++)
				if (anIntArray106[l5] != 0) {
					int l6 = k2 + anIntArray119[l5];
					if (l6 < i) {
						samples[l6] += method168(j4 * anIntArray120[l5] >> 15, phase[l5], aClass29_98.anInt540);
						phase[l5] += (l2 * anIntArray121[l5] >> 16) + anIntArray122[l5];
					}
				}
		}
		if (aClass29_104 != null) {
			aClass29_104.resetValues();
			aClass29_105.resetValues();
			int i3 = 0;
			boolean flag1 = true;
			for (int i7 = 0; i7 < i; i7++) {
				int k7 = aClass29_104.evaluate(i);
				int i8 = aClass29_105.evaluate(i);
				int k4;
				if (flag1)
					k4 = aClass29_104.anInt538 + ((aClass29_104.anInt539 - aClass29_104.anInt538) * k7 >> 8);
				else
					k4 = aClass29_104.anInt538 + ((aClass29_104.anInt539 - aClass29_104.anInt538) * i8 >> 8);
				if ((i3 += 256) >= k4) {
					i3 = 0;
					flag1 = !flag1;
				}
				if (flag1)
					samples[i7] = 0;
			}
		}
		if (anInt109 > 0 && gain > 0) {
			int j3 = (int) ((double) anInt109 * d);
			for (int l4 = j3; l4 < i; l4++)
				samples[l4] += (samples[l4 - j3] * gain) / 100;
		}
		if (filter.anIntArray665[0] > 0 || filter.anIntArray665[1] > 0) {
			aClass29_112.resetValues();
			int k3 = aClass29_112.evaluate(i + 1);
			int i5 = filter.method544(0, (float) k3 / 65536F);
			int i6 = filter.method544(1, (float) k3 / 65536F);
			if (i >= i5 + i6) {
				int j7 = 0;
				int l7 = i6;
				if (l7 > i - i5)
					l7 = i - i5;
				for (; j7 < l7; j7++) {
					int j8 = (int) ((long) samples[j7 + i5] * (long) Filter.anInt672 >> 16);
					for (int k8 = 0; k8 < i5; k8++)
						j8 += (int) ((long) samples[(j7 + i5) - 1 - k8] * (long) Filter.anIntArrayArray670[0][k8] >> 16);
					for (int j9 = 0; j9 < j7; j9++)
						j8 -= (int) ((long) samples[j7 - 1 - j9] * (long) Filter.anIntArrayArray670[1][j9] >> 16);
					samples[j7] = j8;
					k3 = aClass29_112.evaluate(i + 1);
				}
				char c = '\200';
				l7 = c;
				do {
					if (l7 > i - i5)
						l7 = i - i5;
					for (; j7 < l7; j7++) {
						int l8 = (int) ((long) samples[j7 + i5] * (long) Filter.anInt672 >> 16);
						for (int k9 = 0; k9 < i5; k9++)
							l8 += (int) ((long) samples[(j7 + i5) - 1 - k9] * (long) Filter.anIntArrayArray670[0][k9] >> 16);
						for (int i10 = 0; i10 < i6; i10++)
							l8 -= (int) ((long) samples[j7 - 1 - i10] * (long) Filter.anIntArrayArray670[1][i10] >> 16);
						samples[j7] = l8;
						k3 = aClass29_112.evaluate(i + 1);
					}
					if (j7 >= i - i5)
						break;
					i5 = filter.method544(0, (float) k3 / 65536F);
					i6 = filter.method544(1, (float) k3 / 65536F);
					l7 += c;
				} while (true);
				for (; j7 < i; j7++) {
					int i9 = 0;
					for (int l9 = (j7 + i5) - i; l9 < i5; l9++)
						i9 += (int) ((long) samples[(j7 + i5) - 1 - l9] * (long) Filter.anIntArrayArray670[0][l9] >> 16);
					for (int j10 = 0; j10 < i6; j10++)
						i9 -= (int) ((long) samples[j7 - 1 - j10] * (long) Filter.anIntArrayArray670[1][j10] >> 16);
					samples[j7] = i9;
					aClass29_112.evaluate(i + 1);
				}
			}
		}
		for (int i4 = 0; i4 < i; i4++) {
			if (samples[i4] < -32768)
				samples[i4] = -32768;
			if (samples[i4] > 32767)
				samples[i4] = 32767;
		}
		return samples;
	}

	private int method168(int i, int k, int l) {
		if (l == 1)
			if ((k & 0x7fff) < 16384)
				return i;
			else
				return -i;
		if (l == 2)
			return SINE[k & 0x7fff] * i >> 14;
		if (l == 3)
			return ((k & 0x7fff) * i >> 14) - i;
		if (l == 4)
			return NOISE[k / 2607 & 0x7fff] * i;
		else
			return 0;
	}

	public void method169(RSBuffer stream) {
		aClass29_98 = new Envelope();
		aClass29_98.decode(stream);
		aClass29_99 = new Envelope();
		aClass29_99.decode(stream);
		int i = stream.readUByte();
		if (i != 0) {
			stream.pointer--;
			envolope = new Envelope();
			envolope.decode(stream);
			aClass29_101 = new Envelope();
			aClass29_101.decode(stream);
		}
		i = stream.readUByte();
		if (i != 0) {
			stream.pointer--;
			aClass29_102 = new Envelope();
			aClass29_102.decode(stream);
			aClass29_103 = new Envelope();
			aClass29_103.decode(stream);
		}
		i = stream.readUByte();
		if (i != 0) {
			stream.pointer--;
			aClass29_104 = new Envelope();
			aClass29_104.decode(stream);
			aClass29_105 = new Envelope();
			aClass29_105.decode(stream);
		}
		for (int j = 0; j < 10; j++) {
			int k = stream.readSmarts();
			if (k == 0)
				break;
			anIntArray106[j] = k;
			cents[j] = stream.method421();
			anIntArray108[j] = stream.readSmarts();
		}
		anInt109 = stream.readSmarts();
		gain = stream.readSmarts();
		offset = stream.readUShort();
		remaining = stream.readUShort();
		filter = new Filter();
		aClass29_112 = new Envelope();
		filter.method545(stream, aClass29_112);
	}

	public Synthesizer() {
		anIntArray106 = new int[5];
		cents = new int[5];
		anIntArray108 = new int[5];
		gain = 100;
		offset = 500;
	}

	private Envelope aClass29_98;
	private Envelope aClass29_99;
	private Envelope envolope;
	private Envelope aClass29_101;
	private Envelope aClass29_102;
	private Envelope aClass29_103;
	private Envelope aClass29_104;
	private Envelope aClass29_105;
	private final int[] anIntArray106;
	private final int[] cents;
	private final int[] anIntArray108;
	private int anInt109;
	private int gain;
	private Filter filter;
	private Envelope aClass29_112;
	int offset;
	int remaining;
	private static int[] samples;
	private static int[] NOISE;
	private static int[] SINE;
	private static final int[] phase = new int[5];
	private static final int[] anIntArray119 = new int[5];
	private static final int[] anIntArray120 = new int[5];
	private static final int[] anIntArray121 = new int[5];
	private static final int[] anIntArray122 = new int[5];
}
