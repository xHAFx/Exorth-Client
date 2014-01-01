public final class NPC extends Mobile {

	private Model method450() {
		if (super.anim >= 0 && super.anInt1529 == 0) {
			int k = Sequence.anims[super.anim].anIntArray353[super.anInt1527];
			int i1 = -1;
			if (super.anInt1517 >= 0 && super.anInt1517 != super.anInt1511)
				i1 = Sequence.anims[super.anInt1517].anIntArray353[super.anInt1518];
			return desc.method164(i1, k, Sequence.anims[super.anim].anIntArray357);
		}
		int l = -1;
		if (super.anInt1517 >= 0)
			l = Sequence.anims[super.anInt1517].anIntArray353[super.anInt1518];
		return desc.method164(-1, l, null);
	}

	public Model getRotatedModel() {
		if (desc == null)
			return null;
		Model model = method450();
		if (model == null)
			return null;
		super.height = model.modelHeight;
		if (super.gfxId != -1 && super.currentAnim != -1) {
			Graphic spotAnim = Graphic.cache[super.gfxId];
			Model model_1 = spotAnim.getModel();
			if (model_1 != null) {
				int j = spotAnim.aAnimation_407.anIntArray353[super.currentAnim];
				Model model_2 = new Model(true, Animation.method532(j), false, model_1);
				model_2.method475(0, -super.anInt1524, 0);
				model_2.method469();
				model_2.method470(j);
				model_2.triangleSkin = null;
				model_2.vertexSkin = null;
				if (spotAnim.anInt410 != 128 || spotAnim.anInt411 != 128)
					model_2.resize(spotAnim.anInt410, spotAnim.anInt410, spotAnim.anInt411);
				model_2.light(64 + spotAnim.anInt413, 850 + spotAnim.anInt414, -30, -50, -30, true);
				Model aModel[] = { model, model_2 };
				model = new Model(aModel);
			}
		}
		if (desc.size == 1)
			model.oneSquareModel = true;
		return model;
	}

	public boolean isVisible() {
		return desc != null;
	}

	NPC() {
	}

	public NpcDefintion desc;
}
