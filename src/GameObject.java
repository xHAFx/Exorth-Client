final class GameObject extends Renderable {

	private int anInt1599;
	private final int[] anIntArray1600;
	private final int anInt1601;
	private final int anInt1602;
	private final int anInt1603;
	private final int anInt1604;
	private final int anInt1605;
	private final int anInt1606;
	private Sequence aAnimation_1607;
	private int anInt1608;
	public static RSClient clientInstance;
	private final int anInt1610;
	private final int anInt1611;
	private final int anInt1612;

	private ObjectDefinition method457() {
		int i = -1;
		if (anInt1601 != -1) {
			try {
				VarBit varBit = VarBit.cache[anInt1601];
				int k = varBit.configId;
				int l = varBit.leastSignificantBit;
				int i1 = varBit.mostSignificantBit;
				int j1 = RSClient.anIntArray1232[i1 - l];
				i = clientInstance.variousSettings[k] >> l & j1;
			} catch (Exception ex) {
			}
		} else if (anInt1602 != -1)
			i = clientInstance.variousSettings[anInt1602];
		if (i < 0 || i >= anIntArray1600.length || anIntArray1600[i] == -1)
			return null;
		else
			return ObjectDefinition.forID(anIntArray1600[i]);
	}

	public Model getRotatedModel() {
		int j = -1;
		if (aAnimation_1607 != null && !RSClient.lowMemory) {
			int k = RSClient.loopCycle - anInt1608;
			if (k > 100 && aAnimation_1607.anInt356 > 0)
				k = 100;
			while (k > aAnimation_1607.getFrameLength(anInt1599)) {
				k -= aAnimation_1607.getFrameLength(anInt1599);
				anInt1599++;
				if (anInt1599 < aAnimation_1607.anInt352)
					continue;
				anInt1599 -= aAnimation_1607.anInt356;
				if (anInt1599 >= 0 && anInt1599 < aAnimation_1607.anInt352)
					continue;
				aAnimation_1607 = null;
				break;
			}
			anInt1608 = RSClient.loopCycle - k;
			if (aAnimation_1607 != null)
				j = aAnimation_1607.anIntArray353[anInt1599];
		}
		ObjectDefinition objDefinition;
		if (anIntArray1600 != null)
			objDefinition = method457();
		else
			objDefinition = ObjectDefinition.forID(anInt1610);
		if (objDefinition == null) {
			return null;
		} else {
			return objDefinition.generateModel(anInt1611, anInt1612, anInt1603, anInt1604, anInt1605, anInt1606, j);
		}
	}

	public GameObject(int i, int j, int k, int l, int i1, int j1, int k1, int l1, boolean flag) {
		anInt1610 = i;
		anInt1611 = k;
		anInt1612 = j;
		anInt1603 = j1;
		anInt1604 = l;
		anInt1605 = i1;
		anInt1606 = k1;
		if (l1 != -1) {
			aAnimation_1607 = Sequence.anims[l1];
			Animation.method531(aAnimation_1607.anIntArray353[anInt1599]);
			anInt1599 = 0;
			anInt1608 = RSClient.loopCycle;
			if (flag && aAnimation_1607.anInt356 != -1) {
				anInt1599 = (int) (Math.random() * (double) aAnimation_1607.anInt352);
				anInt1608 -= (int) (Math.random() * (double) aAnimation_1607.getFrameLength(anInt1599));
			}
		}
		ObjectDefinition objDefinition = ObjectDefinition.forID(anInt1610);
		anInt1601 = objDefinition.anInt774;
		anInt1602 = objDefinition.anInt749;
		anIntArray1600 = objDefinition.childrenIDs;
	}
}