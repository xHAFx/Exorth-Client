/**
 * 
 * @author Joshua Barry
 * 
 */
public final class Envelope {

	public void decode(RSBuffer buffer) {
		anInt540 = buffer.readUByte();
		anInt538 = buffer.readInt();
		anInt539 = buffer.readInt();
		decodeSegments(buffer);
	}

	public void decodeSegments(RSBuffer stream) {
		segmentCount = stream.readUByte();
		segmentDuration = new int[segmentCount];
		segmentPeak = new int[segmentCount];
		for (int i = 0; i < segmentCount; i++) {
			segmentDuration[i] = stream.readUShort();
			segmentPeak[i] = stream.readUShort();
		}
	}

	void resetValues() {
		checkpoint = 0;
		segmentPtr = 0;
		step = 0;
		amplitude = 0;
		tick = 0;
	}

	int evaluate(int rate) {
		if (tick >= checkpoint) {
			amplitude = segmentPeak[segmentPtr++] << 15;
			if (segmentPtr >= segmentCount)
				segmentPtr = segmentCount - 1;
			checkpoint = (int) (((double) segmentDuration[segmentPtr] / 65536D) * (double) rate);
			if (checkpoint > tick)
				step = ((segmentPeak[segmentPtr] << 15) - amplitude) / (checkpoint - tick);
		}
		amplitude += step;
		tick++;
		return amplitude - step >> 15;
	}

	private int segmentCount;
	private int[] segmentDuration;
	private int[] segmentPeak;
	int anInt538;
	int anInt539;
	int anInt540;
	private int checkpoint;
	private int segmentPtr;
	private int step;
	private int amplitude;
	private int tick;
	public static int anInt546;
}
