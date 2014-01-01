final class Item extends Renderable {

	public final Model getRotatedModel() {
		ItemDefinition itemDef = ItemDefinition.forID(ID);
		return itemDef.method201(item_count);
	}

	public Item() {
	}

	public int ID;
	public int x;
	public int y;
	public int item_count;
}
