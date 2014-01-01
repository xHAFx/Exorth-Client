public final class SkinList {

	public SkinList(RSBuffer stream) {
		int skin = stream.readUByte();
		anIntArray342 = new int[skin];
		anIntArrayArray343 = new int[skin][];
		for (int j = 0; j < skin; j++)
			anIntArray342[j] = stream.readUByte();
		for (int j = 0; j < skin; j++)
			anIntArrayArray343[j] = new int[stream.readUByte()];
		for (int j = 0; j < skin; j++)
			for (int l = 0; l < anIntArrayArray343[j].length; l++)
				anIntArrayArray343[j][l] = stream.readUByte();
	}

	public final int[] anIntArray342;
	public final int[][] anIntArrayArray343;
}
