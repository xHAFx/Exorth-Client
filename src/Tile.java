public final class Tile extends Node {

	public Tile(int i, int j, int k) {
		interactableObjects = new InteractiveObject[5];
		anIntArray1319 = new int[5];
		anInt1310 = (short) (tileZ = i);
		anInt1308 = (short) j;
		anInt1309 = (short) k;
	}

	int tileZ;
	final short anInt1308;
	final short anInt1309;
	final short anInt1310;
	public PlainTile plainTile;
	public ShapedTile shapedTile;
	public WallObject wallObject;
	public WallDecoration wallDecoration;
	public GroundDecoration groundDecoration;
	public GroundItemTile groundItemTile;
	int entityCount;
	public final InteractiveObject[] interactableObjects;
	final int[] anIntArray1319;
	int anInt1320;
	int logicHeight;
	boolean aBoolean1322;
	boolean aBoolean1323;
	boolean aBoolean1324;
	short anInt1325;
	short anInt1326;
	short anInt1327;
	short anInt1328;
	public Tile tile;
}
