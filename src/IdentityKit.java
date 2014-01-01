public final class IdentityKit {

	public static void unpackConfig(CacheArchive streamLoader) {
		RSBuffer stream = new RSBuffer(streamLoader.getDataForName("idk.dat"));
		length = stream.readUShort();
		if (cache == null)
			cache = new IdentityKit[length];
		for (int j = 0; j < length; j++) {
			if (cache[j] == null)
				cache[j] = new IdentityKit();
			cache[j].readValues(stream);
		}
	}

	private void readValues(RSBuffer stream) {
		do {
			int i = stream.readUByte();
			if (i == 0)
				return;
			if (i == 1)
				bodyPartId = stream.readUByte();
			else if (i == 2) {
				int j = stream.readUByte();
				bodyModelIds = new int[j];
				for (int k = 0; k < j; k++)
					bodyModelIds[k] = stream.readUShort();
			} else if (i == 3)
				notSelectable = true;
			else if (i >= 40 && i < 50)
				originalColors[i - 40] = stream.readUShort();
			else if (i >= 50 && i < 60)
				modifiedColors[i - 50] = stream.readUShort();
			else if (i >= 60 && i < 70)
				headModelIds[i - 60] = stream.readUShort();
			else
				System.out.println("Error unrecognised config code: " + i);
		} while (true);
	}

	public boolean method537() {
		if (bodyModelIds == null)
			return true;
		boolean flag = true;
		for (int j = 0; j < bodyModelIds.length; j++)
			if (!Model.method463(bodyModelIds[j]))
				flag = false;
		return flag;
	}

	public Model method538() {
		if (bodyModelIds == null)
			return null;
		Model aclass30_sub2_sub4_sub6s[] = new Model[bodyModelIds.length];
		for (int i = 0; i < bodyModelIds.length; i++)
			aclass30_sub2_sub4_sub6s[i] = Model.method462(bodyModelIds[i]);
		Model model;
		if (aclass30_sub2_sub4_sub6s.length == 1)
			model = aclass30_sub2_sub4_sub6s[0];
		else
			model = new Model(aclass30_sub2_sub4_sub6s.length, aclass30_sub2_sub4_sub6s);
		for (int j = 0; j < 6; j++) {
			if (originalColors[j] == 0)
				break;
			model.method476(originalColors[j], modifiedColors[j]);
		}
		return model;
	}

	public boolean method539() {
		boolean flag1 = true;
		for (int i = 0; i < 5; i++)
			if (headModelIds[i] != -1 && !Model.method463(headModelIds[i]))
				flag1 = false;
		return flag1;
	}

	public Model method540() {
		Model aclass30_sub2_sub4_sub6s[] = new Model[5];
		int j = 0;
		for (int k = 0; k < 5; k++)
			if (headModelIds[k] != -1)
				aclass30_sub2_sub4_sub6s[j++] = Model.method462(headModelIds[k]);
		Model model = new Model(j, aclass30_sub2_sub4_sub6s);
		for (int l = 0; l < 6; l++) {
			if (originalColors[l] == 0)
				break;
			model.method476(originalColors[l], modifiedColors[l]);
		}
		return model;
	}

	private IdentityKit() {
		bodyPartId = -1;
		originalColors = new int[6];
		modifiedColors = new int[6];
		notSelectable = false;
	}

	public static int length;
	public static IdentityKit cache[];
	public int bodyPartId;
	private int[] bodyModelIds;
	private int[] originalColors;
	private int[] modifiedColors;
	private int[] headModelIds = { -1, -1, -1, -1, -1 };
	public boolean notSelectable;
}
