import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public final class ObjectDefinition {

	/**
	 * TODO: AKZU READ THIS Original model colors[0] usually, control the
	 * texture!!!!!
	 */
	public static ObjectDefinition forID(int i) {
		if (i >= totalObjects)
			i = 1;
		if (i == 17454)
			i = 17435;
		if (i == 12166 || i == 12165 || i == 12163)
			i = 12144;
		if (i == 1905 || i == 1904)
			i = 1902;
		if (i == 11828)
			i = 11652;
		if (i == 11829)
			i = 11653;
		if (i == 14395)
			i = 11652;
		for (int j = 0; j < 20; j++)
			if (cache[j].type == i)
				return cache[j];
		cacheIndex = (cacheIndex + 1) % 20;
		ObjectDefinition objDefinition = cache[cacheIndex];
		try {
			stream.pointer = streamIndices[i];
		} catch (Exception e) {
		}
		objDefinition.type = (short) i;
		objDefinition.setDefaults();
		objDefinition.readValues(stream);
		// if (i == 17454) {
		// System.err.println(objDefinition.anIntArray773);
		// }
		// loadObjects(objDefinition);
		/*
		 * if (i == 4767) { objDefinition.objectBrightness = -13; }
		 */
		/*
		 * for (int kk = 0; kk < objDefinition.anIntArray773.length; kk++) if
		 * (objDefinition.anIntArray773[kk] == 17492) System.err.println(i);
		 */
		// varrock roof piece (bar)
		if (i >= 15557 && i <= 15558) {
			objDefinition.aBoolean769 = false;
			objDefinition.objectShadowing = 0;
			objDefinition.objectBrightness = 50;
		}
		// oak..
		// if (i == 1281)
		// objDefinition.objectBrightness = -4;
		// rc alter
		if (i == 2485)
			objDefinition.animID = -1;
		// if (i >= 23261 && i <= 23271)
		// objDefinition.objectBrightness = -7;
		/*
		 * loadObjects(objDefinition); if (i == 23266) objDefinition.aBoolean769
		 * = false; if (objDefinition.anInt781 > 7296) objDefinition.anInt781 =
		 * -1; if (i == 26807) objDefinition.actions[1] = "P-mod Options"; if (i
		 * == 3172 || i == 3173 || i == 3174) objDefinition.mapScene = 48; if (i
		 * == 11652) { objDefinition.anIntArray773[0] = 11687;
		 * objDefinition.anIntArray773[1] = 11688;
		 * objDefinition.anIntArray773[2] = 11689;
		 * objDefinition.anIntArray773[3] = 11690;
		 * objDefinition.anIntArray773[4] = 11691; } if (i == 11776) {
		 * objDefinition.anIntArray773[0] = 11687;
		 * objDefinition.anIntArray773[1] = 11688;
		 * objDefinition.anIntArray773[2] = 11689;
		 * objDefinition.anIntArray773[3] = 11690;
		 * objDefinition.anIntArray773[4] = 11691; } if (i == 11777) {
		 * objDefinition.anIntArray773[0] = 11685; } if (i == 11653) {
		 * objDefinition.anIntArray773[0] = 11685; } if (i == 11773) {
		 * objDefinition.anIntArray773[0] = 11686; } if (i == 11772) {
		 * objDefinition.anIntArray773[0] = 11687;
		 * objDefinition.anIntArray773[1] = 11688;
		 * objDefinition.anIntArray773[2] = 11689;
		 * objDefinition.anIntArray773[3] = 11690;
		 * objDefinition.anIntArray773[4] = 11691; } if (i == 11775) {
		 * objDefinition.anIntArray773[0] = 11687;
		 * objDefinition.anIntArray773[1] = 11688; } if (i == 11778) {
		 * objDefinition.anIntArray773[0] = 11686; } if (i == 11779) {
		 * objDefinition.anIntArray773[0] = 11686; }
		 */
		return objDefinition;
	}

	private void setDefaults() {
		anIntArray773 = null;
		anIntArray776 = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		width = 1;
		height = 1;
		isSolid = true;
		isRangeable = true;
		hasActions = false;
		aBoolean762 = false;
		aBoolean769 = false;
		aBoolean764 = false;
		animID = -1;
		anInt775 = 16;
		objectBrightness = 0;
		objectShadowing = 0;
		actions = null;
		mapIcon = -1;
		mapScene = -1;
		aBoolean751 = false;
		aBoolean779 = true;
		anInt748 = 128;
		anInt772 = 128;
		anInt740 = 128;
		anInt768 = 0;
		anInt738 = 0;
		anInt745 = 0;
		anInt783 = 0;
		aBoolean736 = false;
		aBoolean766 = false;
		anInt760 = -1;
		anInt774 = -1;
		anInt749 = -1;
		childrenIDs = null;
	}

	// TODO: Dummy??
	/*
	 * public void method574(OnDemandFetcher class42_sub1) { if (anIntArray773
	 * == null) return; for (int j = 0; j < anIntArray773.length; j++)
	 * class42_sub1.method560(anIntArray773[j] & 0xffff, 0); }
	 */
	public static void nullLoader() {
		mruNodes1 = null;
		mruNodes2 = null;
		streamIndices = null;
		cache = null;
		stream = null;
	}

	static int totalObjects;

	public static void unpackConfig(CacheArchive streamLoader) {
		stream = new RSBuffer(streamLoader.getDataForName("loc.dat"));
		RSBuffer stream = new RSBuffer(streamLoader.getDataForName("loc.idx"));
		totalObjects = stream.readUShort();
		streamIndices = new int[totalObjects];
		int i = 2;
		for (int j = 0; j < totalObjects; j++) {
			streamIndices[j] = i;
			i += stream.readUShort();
		}
		cache = new ObjectDefinition[20];
		for (int k = 0; k < 20; k++)
			cache[k] = new ObjectDefinition();
	}

	public boolean isCachedType(int i) {
		if (anIntArray776 == null) {
			if (anIntArray773 == null)
				return true;
			if (i != 10)
				return true;
			boolean flag1 = true;
			for (int k = 0; k < anIntArray773.length; k++)
				flag1 &= Model.method463(anIntArray773[k] & 0xffff);
			return flag1;
		}
		for (int j = 0; j < anIntArray776.length; j++)
			if (anIntArray776[j] == i)
				return Model.method463(anIntArray773[j] & 0xffff);
		return true;
	}

	public Model generateModel(int i, int j, int k, int l, int i1, int j1, int k1) {
		Model model = method581(i, k1, j);
		if (model == null)
			return null;
		if (aBoolean762 || aBoolean769)
			model = new Model(aBoolean762, aBoolean769, model);
		if (aBoolean762) {
			int l1 = (k + l + i1 + j1) / 4;
			for (int i2 = 0; i2 < model.vertexCount; i2++) {
				int j2 = model.vertexX[i2];
				int k2 = model.vertexZ[i2];
				int l2 = k + ((l - k) * (j2 + 64)) / 128;
				int i3 = j1 + ((i1 - j1) * (j2 + 64)) / 128;
				int j3 = l2 + ((i3 - l2) * (k2 + 64)) / 128;
				model.vertexY[i2] += j3 - l1;
			}
			model.method467();
		}
		return model;
	}

	public boolean method579() {
		if (anIntArray773 == null)
			return true;
		boolean flag1 = true;
		for (int i = 0; i < anIntArray773.length; i++)
			flag1 &= Model.method463(anIntArray773[i] & 0xffff);
		return flag1;
	}

	public ObjectDefinition method580() {
		int i = -1;
		if (anInt774 != -1) {
			VarBit varBit = VarBit.cache[anInt774];
			int config = varBit.configId;
			int k = varBit.leastSignificantBit;
			int l = varBit.mostSignificantBit;
			int i1 = RSClient.anIntArray1232[l - k];
			i = clientInstance.variousSettings[config] >> k & i1;
		} else if (anInt749 != -1)
			i = clientInstance.variousSettings[anInt749];
		if (i < 0 || i >= childrenIDs.length || childrenIDs[i] == -1)
			return null;
		else
			return forID(childrenIDs[i]);
	}

	private Model method581(int j, int k, int l) {
		Model model = null;
		long l1;
		if (anIntArray776 == null) {
			if (j != 10)
				return null;
			l1 = (long) ((type << 6) + l) + ((long) (k + 1) << 32);
			Model model_1 = (Model) mruNodes2.insertFromCache(l1);
			if (model_1 != null)
				return model_1;
			if (anIntArray773 == null)
				return null;
			boolean flag1 = aBoolean751 ^ (l > 3);
			int k1 = anIntArray773.length;
			for (int i2 = 0; i2 < k1; i2++) {
				int l2 = anIntArray773[i2];
				if (flag1)
					l2 += 0x10000;
				model = (Model) mruNodes1.insertFromCache(l2);
				if (model == null) {
					model = Model.method462(l2 & 0xffff);
					if (model == null)
						return null;
					if (flag1)
						model.method477();
					mruNodes1.removeFromCache(model, l2);
				}
				if (k1 > 1)
					aModelArray741s[i2] = model;
			}
			if (k1 > 1)
				model = new Model(k1, aModelArray741s);
		} else {
			int i1 = -1;
			for (int j1 = 0; j1 < anIntArray776.length; j1++) {
				if (anIntArray776[j1] != j)
					continue;
				i1 = j1;
				break;
			}
			if (i1 == -1)
				return null;
			// l1 = (long)((type << 6) + (i1 << 3) + l) + ((long)(k + 1) << 32);
			l1 = (long) ((type << 8) + (i1 << 3) + l) + ((long) (k + 1) << 32);// small
			// roof
			// corner
			// bug
			// fix
			Model model_2 = (Model) mruNodes2.insertFromCache(l1);
			if (model_2 != null)
				return model_2;
			int j2 = anIntArray773[i1];
			boolean flag3 = aBoolean751 ^ (l > 3);
			if (flag3)
				j2 += 0x10000;
			model = (Model) mruNodes1.insertFromCache(j2);
			if (model == null) {
				model = Model.method462(j2 & 0xffff);
				if (model == null)
					return null;
				if (flag3)
					model.method477();
				mruNodes1.removeFromCache(model, j2);
			}
		}
		boolean flag;
		flag = anInt748 != 128 || anInt772 != 128 || anInt740 != 128;
		boolean flag2;
		flag2 = anInt738 != 0 || anInt745 != 0 || anInt783 != 0;
		Model model_3 = new Model(modifiedModelColors == null, Animation.method532(k), l == 0 && k == -1 && !flag && !flag2, model);
		if (k != -1) {
			model_3.method469();
			model_3.method470(k);
			model_3.triangleSkin = null;
			model_3.vertexSkin = null;
		}
		while (l-- > 0)
			model_3.method473();
		if (modifiedModelColors != null) {
			for (int k2 = 0; k2 < modifiedModelColors.length; k2++)
				model_3.method476(modifiedModelColors[k2], originalModelColors[k2]);
		}
		if (flag)
			model_3.resize(anInt748, anInt740, anInt772);
		if (flag2)
			model_3.method475(anInt738, anInt745, anInt783);
		model_3.light(60 + objectBrightness, 1350 + (objectShadowing * 5), -10, -5, -10, !aBoolean769);
		// model_3.light(64 + objectBrightness, 768 + (objectShadowing * 5),
		// -50, -10, -50, !aBoolean769);
		if (anInt760 == 1)
			model_3.anInt1654 = model_3.modelHeight;
		mruNodes2.removeFromCache(model_3, l1);
		return model_3;
	}

	public static void writeString(DataOutputStream dos, String input) throws IOException {
		dos.write(input.getBytes());
		dos.writeByte(10);
	}

	public static void writeDWordBigEndian(DataOutputStream dat, int i) throws IOException {
		dat.write((byte) (i >> 16));
		dat.write((byte) (i >> 8));
		dat.write((byte) (i >> 8));
	}

	/**
	 * Load object colors from 474 dump public static void loadObjects(ObjectDef
	 * def) { try { BufferedReader buf = new BufferedReader(new
	 * FileReader(signlink.cacheLocation() + "loc.txt")); String line; int id =
	 * 0; int length = 0; int[] mod = new int[5]; int[] orig = new int[5]; while
	 * ((line = buf.readLine()) != null) { id =
	 * Integer.parseInt(line.substring(line.indexOf("[ID]")+4,
	 * line.indexOf("[SIZE]"))); length =
	 * Integer.parseInt(line.substring(line.indexOf("[SIZE]")+6,
	 * line.indexOf("[MOD(0)]"))); mod[0] =
	 * Integer.parseInt(line.substring(line.indexOf("[MOD(0)]")+8,
	 * line.indexOf("[ORIG(0)]"))); if (length <= 1) orig[0] =
	 * Integer.parseInt(line.substring(line.indexOf("[ORIG(0)]")+10)); else
	 * orig[0] = Integer.parseInt(line.substring(line.indexOf("[ORIG(0)]")+10,
	 * line.indexOf("[MOD(1)]")));
	 * 
	 * if (length == 2) { mod[1] =
	 * Integer.parseInt(line.substring(line.indexOf("[MOD(1)]")+8,
	 * line.indexOf("[ORIG(1)]"))); orig[1] =
	 * Integer.parseInt(line.substring(line.indexOf("[ORIG(1)]")+10)); }
	 * 
	 * if (length == 3) { mod[2] =
	 * Integer.parseInt(line.substring(line.indexOf("[MOD(2)]")+8,
	 * line.indexOf("[ORIG(2)]"))); orig[2] =
	 * Integer.parseInt(line.substring(line.indexOf("[ORIG(2)]")+10)); } if
	 * (def.type == id) { def.originalModelColors = new int[length];
	 * def.modifiedModelColors = new int[length]; for (int i2 = 0; i2 < length;
	 * i2++) { def.modifiedModelColors[i2] = mod[i2];
	 * def.originalModelColors[i2] = orig[i2]; } } } buf.close(); } catch
	 * (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e)
	 * { // TODO Auto-generated catch block e.printStackTrace(); } }
	 */
	/*
	 * public static void loadObjects(ObjectDef def) { try { BufferedReader buf
	 * = new BufferedReader(new FileReader(signlink.cacheLocation() +
	 * "loc.txt")); String line; int id = 0; int bright = 0; int shadow = 0;
	 * while ((line = buf.readLine()) != null) { id =
	 * Integer.parseInt(line.substring(line.indexOf("[ID]")+4,
	 * line.indexOf("[BRIGHT]"))); bright =
	 * Integer.parseInt(line.substring(line.indexOf("[BRIGHT]")+8,
	 * line.indexOf("[SHAD]"))); shadow =
	 * Integer.parseInt(line.substring(line.indexOf("[SHAD]")+6)); if (def.type
	 * == id) { def.objectBrightness = (byte) bright; def.objectShadowing =
	 * (byte) shadow; } } buf.close(); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 */
	public static void loadObjects(ObjectDefinition def) {
		try {
			BufferedReader buf = new BufferedReader(new FileReader(Signlink.cacheLocation() + "loc.txt"));
			String line;
			int id = 0;
			boolean flag = false;
			while ((line = buf.readLine()) != null) {
				id = Integer.parseInt(line.substring(line.indexOf("[ID]") + 4, line.indexOf("[FLAG]")));
				flag = line.substring(line.indexOf("[FLAG]") + 6).startsWith("false") ? true : false;
				if (def.type == id) {
					def.aBoolean769 = flag;
				}
			}
			buf.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Writes the objectDef cache (loc.dat, loc.idx).
	 */
	public static void writeObjects() {
		try {
			DataOutputStream dat = new DataOutputStream(new FileOutputStream(Signlink.cacheLocation() + "loc.dat"));
			DataOutputStream idx = new DataOutputStream(new FileOutputStream(Signlink.cacheLocation() + "loc.idx"));
			idx.writeShort(ObjectDefinition.totalObjects);
			dat.writeShort(ObjectDefinition.totalObjects);
			for (int index = 0; index < ObjectDefinition.totalObjects; index++) {
				ObjectDefinition obj = ObjectDefinition.forID(index);
				int offset1 = dat.size();
				if (obj.anIntArray773 != null) {
					if (obj.anIntArray776 != null) {
						dat.writeByte(1);
						dat.writeByte(obj.anIntArray773.length);
						if (obj.anIntArray773.length > 0) {
							for (int i = 0; i < obj.anIntArray773.length; i++) {
								dat.writeShort(obj.anIntArray773[i]);
								dat.writeByte(obj.anIntArray776[i]);
							}
						}
					} else {
						dat.writeByte(5);
						dat.writeByte(obj.anIntArray773.length);
						if (obj.anIntArray773.length > 0) {
							for (int i = 0; i < obj.anIntArray773.length; i++) {
								dat.writeShort(obj.anIntArray773[i]);
							}
						}
					}
				}
				if (obj.name != null) {
					dat.writeByte(2);
					writeString(dat, obj.name);
				}
				if (obj.description != null) {
					dat.writeByte(3);
					writeString(dat, new String(obj.description));
				}
				if (obj.width != 1) {
					dat.writeByte(14);
					dat.writeByte(obj.width);
				}
				if (obj.height != 1) {
					dat.writeByte(15);
					dat.writeByte(obj.height);
				}
				if (!obj.isSolid) {
					dat.writeByte(17);
				}
				if (!obj.isRangeable) {
					dat.writeByte(18);
				}
				if (obj.hasActions) {
					dat.writeByte(19);
					dat.writeByte(1);
				}
				if (obj.aBoolean762) {
					dat.writeByte(21);
				}
				if (obj.aBoolean769) {
					dat.writeByte(22);
				}
				if (obj.aBoolean764) {
					dat.writeByte(23);
				}
				if (obj.animID != -1) {
					dat.writeByte(24);
					dat.writeShort(obj.animID);
				}
				if (obj.anInt775 != 16) {
					dat.writeByte(28);
					dat.writeByte(obj.anInt775);
				}
				if (obj.objectBrightness != 0) {
					dat.writeByte(29);
					dat.writeByte(obj.objectBrightness);
				}
				if (obj.objectShadowing != 0) {
					dat.writeByte(39);
					dat.writeByte(obj.objectShadowing);
				}
				if (obj.actions != null) {
					for (int i = 0; i < obj.actions.length; i++) {
						dat.writeByte(30 + i);
						if (obj.actions[i] != null) {
							writeString(dat, obj.actions[i]);
						} else {
							writeString(dat, "hidden");
						}
					}
				}
				if (obj.modifiedModelColors != null || obj.originalModelColors != null) {
					dat.writeByte(40);
					dat.writeByte(obj.modifiedModelColors.length);
					for (int i = 0; i < obj.modifiedModelColors.length; i++) {
						dat.writeShort(obj.modifiedModelColors[i]);
						dat.writeShort(obj.originalModelColors[i]);
					}
				}
				if (obj.mapIcon != -1) {
					dat.writeByte(60);
					dat.writeShort(obj.mapIcon);
				}
				if (obj.aBoolean751) {
					dat.writeByte(62);
				}
				if (!obj.aBoolean779) {
					dat.writeByte(64);
				}
				if (obj.anInt748 != 128) {
					dat.writeByte(65);
					dat.writeShort(obj.anInt748);
				}
				if (obj.anInt772 != 128) {
					dat.writeByte(66);
					dat.writeShort(obj.anInt772);
				}
				if (obj.anInt740 != 128) {
					dat.writeByte(67);
					dat.writeShort(obj.anInt740);
				}
				if (obj.mapScene != -1) {
					dat.writeByte(68);
					dat.writeShort(obj.mapScene);
				}
				if (obj.anInt768 != 0) {
					dat.writeByte(69);
					dat.writeByte(obj.anInt768);
				}
				if (obj.anInt738 != 0) {
					dat.writeByte(70);
					dat.writeShort(obj.anInt738);
				}
				if (obj.anInt745 != 0) {
					dat.writeByte(71);
					dat.writeShort(obj.anInt745);
				}
				if (obj.anInt783 != 0) {
					dat.writeByte(72);
					dat.writeShort(obj.anInt783);
				}
				if (obj.aBoolean736) {
					dat.writeByte(73);
				}
				if (obj.aBoolean766) {
					dat.writeByte(74);
				}
				if (obj.anInt760 != -1) {
					dat.writeByte(75);
					dat.writeByte(obj.anInt760);
				}
				if (obj.anInt774 != -1 || obj.anInt749 != -1 || obj.childrenIDs != null) {
					dat.writeByte(77);
					dat.writeShort(obj.anInt774);
					dat.writeShort(obj.anInt749);
					dat.writeByte(obj.childrenIDs.length - 1);
					for (int i = 0; i < obj.childrenIDs.length; i++) {
						dat.writeShort(obj.childrenIDs[i]);
					}
				}
				dat.writeByte(0);
				int offset2 = dat.size();
				int writeOffset = offset2 - offset1;
				idx.writeShort(writeOffset);
			}
			dat.close();
			idx.close();
			System.out.println("Finished writing.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readValues(RSBuffer stream) {
		int i = -1;
		label0: do {
			int j;
			do {
				j = stream.readUByte();
				if (j == 0)
					break label0;
				if (j == 1) {
					int k = stream.readUByte();
					if (k > 0)
						if (anIntArray773 == null || lowMem) {
							anIntArray776 = new int[k];
							anIntArray773 = new int[k];
							for (int k1 = 0; k1 < k; k1++) {
								anIntArray773[k1] = stream.readUShort();
								anIntArray776[k1] = stream.readUByte();
							}
						} else {
							stream.pointer += k * 3;
						}
				} else if (j == 2)
					name = stream.readString();
				else if (j == 3)
					description = stream.readBytes();
				else if (j == 5) {
					int l = stream.readUByte();
					if (l > 0)
						if (anIntArray773 == null || lowMem) {
							anIntArray776 = null;
							anIntArray773 = new int[l];
							for (int l1 = 0; l1 < l; l1++)
								anIntArray773[l1] = stream.readUShort();
						} else {
							stream.pointer += l * 2;
						}
				} else if (j == 14)
					width = (byte) stream.readUByte();
				else if (j == 15)
					height = (byte) stream.readUByte();
				else if (j == 17)
					isSolid = false;
				else if (j == 18)
					isRangeable = false;
				else if (j == 19) {
					i = stream.readUByte();
					if (i == 1)
						hasActions = true;
				} else if (j == 21)
					aBoolean762 = true;
				else if (j == 22)
					aBoolean769 = true;
				else if (j == 23)
					aBoolean764 = true;
				else if (j == 24) {
					animID = stream.readUShort();
					if (animID == 65535)
						animID = -1;
				} else if (j == 28)
					anInt775 = stream.readUByte();
				else if (j == 29)
					objectBrightness = stream.readByte();
				else if (j == 39)
					objectShadowing = stream.readByte();
				else if (j >= 30 && j < 39) {
					if (actions == null)
						actions = new String[5];
					actions[j - 30] = stream.readString();
					if (actions[j - 30].equalsIgnoreCase("hidden"))
						actions[j - 30] = null;
				} else if (j == 40) {
					int i1 = stream.readUByte();
					modifiedModelColors = new int[i1];
					originalModelColors = new int[i1];
					for (int i2 = 0; i2 < i1; i2++) {
						modifiedModelColors[i2] = stream.readUShort();
						originalModelColors[i2] = stream.readUShort();
					}
				} else if (j == 60)
					mapIcon = (byte) stream.readUShort();
				else if (j == 62)
					aBoolean751 = true;
				else if (j == 64)
					aBoolean779 = false;
				else if (j == 65)
					anInt748 = stream.readUShort();
				else if (j == 66)
					anInt772 = stream.readUShort();
				else if (j == 67)
					anInt740 = stream.readUShort();
				else if (j == 68)
					mapScene = (byte) stream.readUShort();
				else if (j == 69)
					anInt768 = stream.readUByte();
				else if (j == 70)
					anInt738 = (short) stream.readShort();
				else if (j == 71) {
					anInt745 = (short) stream.readShort();
				} else if (j == 72)
					anInt783 = (short) stream.readShort();
				else if (j == 73)
					aBoolean736 = true;
				else if (j == 74) {
					aBoolean766 = true;
				} else {
					if (j != 75)
						continue;
					anInt760 = stream.readUByte();
				}
				continue label0;
			} while (j != 77);
			anInt774 = stream.readUShort();
			if (anInt774 == 65535)
				anInt774 = -1;
			anInt749 = stream.readUShort();
			if (anInt749 == 65535)
				anInt749 = -1;
			int j1 = stream.readUByte();
			childrenIDs = new int[j1 + 1];
			for (int j2 = 0; j2 <= j1; j2++) {
				childrenIDs[j2] = stream.readUShort();
				if (childrenIDs[j2] == 65535)
					childrenIDs[j2] = -1;
			}
		} while (true);
		if (i == -1) {
			hasActions = anIntArray773 != null && (anIntArray776 == null || anIntArray776[0] == 10);
			if (actions != null)
				hasActions = true;
			if (name == null || name == "null")
				hasActions = false;
		}
		if (aBoolean766) {
			isSolid = false;
			isRangeable = false;
		}
		if (anInt760 == -1)
			anInt760 = isSolid ? 1 : 0;
	}

	private ObjectDefinition() {
		type = -1;
	}

	public boolean aBoolean736;
	private byte objectBrightness;
	private short anInt738;
	public String name;
	private int anInt740;
	private static final Model[] aModelArray741s = new Model[4];
	private byte objectShadowing;
	public byte width;
	private short anInt745;
	public byte mapIcon;
	private int[] originalModelColors;
	private int anInt748;
	public int anInt749;
	private boolean aBoolean751;
	public static boolean lowMem = false;
	private static RSBuffer stream;
	public short type;
	private static int[] streamIndices;
	public boolean isRangeable;
	public byte mapScene;
	public int childrenIDs[];
	private int anInt760;
	public byte height;
	public boolean aBoolean762;
	public boolean aBoolean764;
	public static RSClient clientInstance;
	private boolean aBoolean766;
	public boolean isSolid;
	public int anInt768;
	boolean aBoolean769;
	private static int cacheIndex;
	private int anInt772;
	public int[] anIntArray773;
	public int anInt774;
	public int anInt775;
	private int[] anIntArray776;
	public byte description[];
	public boolean hasActions;
	public boolean aBoolean779;
	public static MemoryCache mruNodes2 = new MemoryCache(30);
	public int animID;
	private static ObjectDefinition[] cache;
	private short anInt783;
	private int[] modifiedModelColors;
	public static MemoryCache mruNodes1 = new MemoryCache(500);
	public String actions[];
}
