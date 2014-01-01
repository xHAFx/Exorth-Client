public final class Player extends Mobile {

	public Model getRotatedModel() {
		if (!visible)
			return null;
		Model model = method452(false);
		if (model == null)
			return null;
		super.height = model.modelHeight;
		model.oneSquareModel = true;
		if (aBoolean1699)
			return model;
		if (super.gfxId != -1 && super.currentAnim != -1) {
			Graphic spotAnim = Graphic.cache[super.gfxId];
			Model model_2 = spotAnim.getModel();
			if (model_2 != null) {
				Model model_3 = new Model(true, Animation.method532(super.currentAnim), false, model_2);
				model_3.method475(0, -super.anInt1524, 0);
				model_3.method469();
				model_3.method470(spotAnim.aAnimation_407.anIntArray353[super.currentAnim]);
				model_3.triangleSkin = null;
				model_3.vertexSkin = null;
				if (spotAnim.anInt410 != 128 || spotAnim.anInt411 != 128)
					model_3.resize(spotAnim.anInt410, spotAnim.anInt410, spotAnim.anInt411);
				model_3.light(60 + spotAnim.anInt413, 850 + spotAnim.anInt414, -5, -15, -5, true);
				Model aclass30_sub2_sub4_sub6_1s[] = { model, model_3 };
				model = new Model(aclass30_sub2_sub4_sub6_1s);
			}
		}
		if (aModel_1714 != null) {
			if (RSClient.loopCycle >= anInt1708)
				aModel_1714 = null;
			if (RSClient.loopCycle >= anInt1707 && RSClient.loopCycle < anInt1708) {
				Model model_1 = aModel_1714;
				model_1.method475(anInt1711 - super.x, anInt1712 - anInt1709, anInt1713 - super.y);
				if (super.turnDirection == 512) {
					model_1.method473();
					model_1.method473();
					model_1.method473();
				} else if (super.turnDirection == 1024) {
					model_1.method473();
					model_1.method473();
				} else if (super.turnDirection == 1536)
					model_1.method473();
				Model aclass30_sub2_sub4_sub6s[] = { model, model_1 };
				model = new Model(aclass30_sub2_sub4_sub6s);
				if (super.turnDirection == 512)
					model_1.method473();
				else if (super.turnDirection == 1024) {
					model_1.method473();
					model_1.method473();
				} else if (super.turnDirection == 1536) {
					model_1.method473();
					model_1.method473();
					model_1.method473();
				}
				model_1.method475(super.x - anInt1711, anInt1709 - anInt1712, super.y - anInt1713);
			}
		}
		model.oneSquareModel = true;
		return model;
	}

	public void updatePlayer(RSBuffer stream) {
		stream.pointer = 0;
		anInt1702 = stream.readUByte();
		headIcon = stream.readUByte();
		skullIcon = stream.readUByte();
		// hintIcon = stream.readUnsignedByte();
		desc = null;
		team = 0;
		for (int j = 0; j < 13; j++) {
			int k = stream.readUByte();
			if (k == 0) {
				equipment[j] = 0;
				continue;
			}
			int i1 = stream.readUByte();
			equipment[j] = (k << 8) + i1;
			if (j == 0 && equipment[0] == 65535) {
				desc = NpcDefintion.forID(stream.readUShort());
				break;
			}
			if (equipment[j] >= 512 && equipment[j] - 512 < ItemDefinition.totalItems) {
				int l1 = ItemDefinition.forID(equipment[j] - 512).team;
				if (l1 != 0)
					team = l1;
			}
		}
		for (int l = 0; l < 5; l++) {
			int j1 = stream.readUByte();
			if (j1 < 0 || j1 >= RSClient.player_outfit_color_array[l].length)
				j1 = 0;
			anIntArray1700[l] = j1;
		}
		super.anInt1511 = stream.readUShort();
		if (super.anInt1511 == 65535)
			super.anInt1511 = -1;
		super.anInt1512 = stream.readUShort();
		if (super.anInt1512 == 65535)
			super.anInt1512 = -1;
		super.anInt1554 = stream.readUShort();
		if (super.anInt1554 == 65535)
			super.anInt1554 = -1;
		super.anInt1555 = stream.readUShort();
		if (super.anInt1555 == 65535)
			super.anInt1555 = -1;
		super.anInt1556 = stream.readUShort();
		if (super.anInt1556 == 65535)
			super.anInt1556 = -1;
		super.anInt1557 = stream.readUShort();
		if (super.anInt1557 == 65535)
			super.anInt1557 = -1;
		super.anInt1505 = stream.readUShort();
		if (super.anInt1505 == 65535)
			super.anInt1505 = -1;
		if (desc != null) {
			super.anInt1511 = desc.standAnimation;
			super.anInt1554 = desc.walkAnimation;
			super.anInt1555 = desc.turn180Animation;
			super.anInt1556 = desc.turn90RightAnimation;
			super.anInt1557 = desc.turn90LeftAnimation;
			super.anInt1505 = desc.walkAnimation;
		}
		name = TextClass.fixName(TextClass.nameForLong(stream.readLong()));
		combatLevel = stream.readUByte();
		skill = stream.readUShort();
		visible = true;
		aLong1718 = 0L;
		RSClient.instance.refreshEquipment();
		for (int k1 = 0; k1 < 12; k1++) {
			aLong1718 <<= 4;
			if (equipment[k1] >= 256)
				aLong1718 += equipment[k1] - 256;
		}
		if (equipment[0] >= 256)
			aLong1718 += equipment[0] - 256 >> 4;
		if (equipment[1] >= 256)
			aLong1718 += equipment[1] - 256 >> 8;
		for (int i2 = 0; i2 < 5; i2++) {
			aLong1718 <<= 3;
			aLong1718 += anIntArray1700[i2];
		}
		aLong1718 <<= 1;
		aLong1718 += anInt1702;
	}

	public Model method452(boolean anims) {
		if (desc != null) {
			int j = -1;
			if (super.anim >= 0 && super.anInt1529 == 0)
				j = Sequence.anims[super.anim].anIntArray353[super.anInt1527];
			else if (super.anInt1517 >= 0)
				j = Sequence.anims[super.anInt1517].anIntArray353[super.anInt1518];
			Model model = desc.method164(-1, j, null);
			return model;
		}
		long l = aLong1718;
		int k = -1;
		int i1 = -1;
		int j1 = -1;
		int k1 = -1;
		if (super.anim >= 0 && super.anInt1529 == 0) {
			Sequence animation = Sequence.anims[super.anim];
			k = animation.anIntArray353[super.anInt1527];
			if (super.anInt1517 >= 0 && super.anInt1517 != super.anInt1511)
				i1 = Sequence.anims[super.anInt1517].anIntArray353[super.anInt1518];
			if (animation.anInt360 >= 0) {
				j1 = animation.anInt360;
				l += j1 - equipment[5] << 40;
			}
			if (animation.anInt361 >= 0) {
				k1 = animation.anInt361;
				l += k1 - equipment[3] << 48;
			}
		} else if (super.anInt1517 >= 0)
			k = Sequence.anims[super.anInt1517].anIntArray353[super.anInt1518];
		if (anims)
			k = Sequence.anims[super.anInt1511].anIntArray353[super.anInt1518];
		Model model_1 = (Model) mruNodes.insertFromCache(l);
		if (model_1 == null) {
			boolean flag = false;
			for (int i2 = 0; i2 < 12; i2++) {
				int k2 = equipment[i2];
				if (k1 >= 0 && i2 == 3)
					k2 = k1;
				if (j1 >= 0 && i2 == 5)
					k2 = j1;
				if (k2 >= 256 && k2 < 512 && !IdentityKit.cache[k2 - 256].method537())
					flag = true;
				if (k2 >= 512 && !ItemDefinition.forID(k2 - 512).method195(anInt1702))
					flag = true;
			}
			if (flag) {
				if (aLong1697 != -1L)
					model_1 = (Model) mruNodes.insertFromCache(aLong1697);
				if (model_1 == null)
					return null;
			}
		}
		if (model_1 == null) {
			Model aclass30_sub2_sub4_sub6s[] = new Model[12];
			int j2 = 0;
			for (int l2 = 0; l2 < 12; l2++) {
				int i3 = equipment[l2];
				if (k1 >= 0 && l2 == 3)
					i3 = k1;
				if (j1 >= 0 && l2 == 5)
					i3 = j1;
				if (i3 >= 256 && i3 < 512) {
					Model model_3 = IdentityKit.cache[i3 - 256].method538();
					if (model_3 != null)
						aclass30_sub2_sub4_sub6s[j2++] = model_3;
				}
				if (i3 >= 512) {
					Model model_4 = ItemDefinition.forID(i3 - 512).method196(anInt1702);
					if (model_4 != null) {
						aclass30_sub2_sub4_sub6s[j2++] = model_4;
					}
				}
			}
			model_1 = new Model(j2, aclass30_sub2_sub4_sub6s);
			for (int j3 = 0; j3 < 5; j3++)
				if (anIntArray1700[j3] != 0) {
					model_1.method476(RSClient.player_outfit_color_array[j3][0], RSClient.player_outfit_color_array[j3][anIntArray1700[j3]]);
					if (j3 == 1)
						model_1.method476(RSClient.anIntArray1204[0], RSClient.anIntArray1204[anIntArray1700[j3]]);
				}
			model_1.method469();
			model_1.resize(132, 132, 132);
			model_1.light(64, 768, -5, -20, -5, true);
			mruNodes.removeFromCache(model_1, l);
			aLong1697 = l;
		}
		if (aBoolean1699)
			return model_1;
		Model model_2 = Model.aModel_1621;
		model_2.method464(model_1, Animation.method532(k) & Animation.method532(i1));
		if (k != -1 && i1 != -1)
			model_2.method471(Sequence.anims[super.anim].anIntArray357, i1, k);
		else if (k != -1)
			model_2.method470(k);
		model_2.method466();
		model_2.triangleSkin = null;
		model_2.vertexSkin = null;
		return model_2;
	}

	public boolean isVisible() {
		return visible;
	}

	public int privelage;

	public Model method453() {
		if (!visible)
			return null;
		if (desc != null)
			return desc.getModelHead();
		boolean flag = false;
		for (int i = 0; i < 12; i++) {
			int j = equipment[i];
			if (j >= 256 && j < 512 && !IdentityKit.cache[j - 256].method539())
				flag = true;
			if (j >= 512 && !ItemDefinition.forID(j - 512).method192(anInt1702))
				flag = true;
		}
		if (flag)
			return null;
		Model aclass30_sub2_sub4_sub6s[] = new Model[12];
		int k = 0;
		for (int l = 0; l < 12; l++) {
			int i1 = equipment[l];
			if (i1 >= 256 && i1 < 512) {
				Model model_1 = IdentityKit.cache[i1 - 256].method540();
				if (model_1 != null)
					aclass30_sub2_sub4_sub6s[k++] = model_1;
			}
			if (i1 >= 512) {
				Model model_2 = ItemDefinition.forID(i1 - 512).method194(anInt1702);
				if (model_2 != null)
					aclass30_sub2_sub4_sub6s[k++] = model_2;
			}
		}
		Model model = new Model(k, aclass30_sub2_sub4_sub6s);
		for (int j1 = 0; j1 < 5; j1++)
			if (anIntArray1700[j1] != 0) {
				model.method476(RSClient.player_outfit_color_array[j1][0], RSClient.player_outfit_color_array[j1][anIntArray1700[j1]]);
				if (j1 == 1)
					model.method476(RSClient.anIntArray1204[0], RSClient.anIntArray1204[anIntArray1700[j1]]);
			}
		return model;
	}

	Player() {
		aLong1697 = -1L;
		aBoolean1699 = false;
		anIntArray1700 = new int[5];
		visible = false;
		bonusAmount = new int[12];
		equipment = new int[13];
	}

	private long aLong1697;
	public NpcDefintion desc;
	boolean aBoolean1699;
	final int[] anIntArray1700;
	public int team;
	private int anInt1702;
	public String name;
	public final int[] bonusAmount;
	static MemoryCache mruNodes = new MemoryCache(260);
	public int combatLevel;
	public int headIcon;
	public int skullIcon;
	public int ring;
	public int hintIcon;
	public int anInt1707;
	int anInt1708;
	int anInt1709;
	boolean visible;
	int anInt1711;
	int anInt1712;
	int anInt1713;
	Model aModel_1714;
	public final int[] equipment;
	private long aLong1718;
	int anInt1719;
	int anInt1720;
	int anInt1721;
	int anInt1722;
	int skill;
}
