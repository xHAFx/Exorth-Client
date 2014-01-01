/**
 * 
 * @author Joshua Barry
 * 
 */
public final class Graphic {

	public static void unpackConfig(CacheArchive archive) {
		RSBuffer stream = new RSBuffer(archive.getDataForName("spotanim.dat"));
		int length = stream.readUShort();
		if (cache == null)
			cache = new Graphic[length];
		for (int j = 0; j < length; j++) {
			if (cache[j] == null)
				cache[j] = new Graphic();
			cache[j].anInt404 = j;
			cache[j].readValues(stream);
		}
	}

	private void readValues(RSBuffer stream) {
		do {
			int i = stream.readUByte();
			if (i == 0)
				return;
			if (i == 1)
				anInt405 = stream.readUShort();
			else if (i == 2) {
				anInt406 = stream.readUShort();
				if (Sequence.anims != null)
					aAnimation_407 = Sequence.anims[anInt406];
			} else if (i == 4)
				anInt410 = stream.readUShort();
			else if (i == 5)
				anInt411 = stream.readUShort();
			else if (i == 6)
				anInt412 = stream.readUShort();
			else if (i == 7)
				anInt413 = stream.readUByte();
			else if (i == 8)
				anInt414 = stream.readUByte();
			else if (i == 40) {
				int j = stream.readUByte();
				for (int k = 0; k < j; k++) {
					anIntArray408[k] = stream.readUShort();
					anIntArray409[k] = stream.readUShort();
				}
			} else
				System.out.println("Error unrecognised spotanim config code: " + i);
		} while (true);
	}

	public Model getModel() {
		Model model = (Model) aMRUNodes_415.insertFromCache(anInt404);
		if (model != null)
			return model;
		model = Model.method462(anInt405);
		if (model == null)
			return null;
		for (int i = 0; i < 10; i++)
			if (anIntArray408[0] != 0)
				model.method476(anIntArray408[i], anIntArray409[i]);
		aMRUNodes_415.removeFromCache(model, anInt404);
		return model;
	}

	private Graphic() {
		anInt406 = -1;
		anIntArray408 = new int[10];
		anIntArray409 = new int[10];
		anInt410 = 128;
		anInt411 = 128;
	}

	public static Graphic cache[];
	private int anInt404;
	private int anInt405;
	private int anInt406;
	public Sequence aAnimation_407;
	private final int[] anIntArray408;
	private final int[] anIntArray409;
	public int anInt410;
	public int anInt411;
	public int anInt412;
	public int anInt413;
	public int anInt414;
	public static MemoryCache aMRUNodes_415 = new MemoryCache(30);
}