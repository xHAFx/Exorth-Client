import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**est
 * 
 * @author Joshua Barry
 * 
 */
public final class NpcDefintion {

	public static NpcDefintion forID(int i) {
		for (int j = 0; j < 20; j++)
			if (cache[j].id == (long) i)
				return cache[j];
		cacheIndex = (cacheIndex + 1) % 20;
		NpcDefintion entityDef = cache[cacheIndex] = new NpcDefintion();
		stream.pointer = streamIndices[i];
		entityDef.id = i;
		entityDef.readValues(stream);

		/*
		 * try { BufferedWriter out; out = new BufferedWriter(new
		 * FileWriter(Signlink.cacheLocation() + "npcs.txt", true)); boolean
		 * flag = false; boolean flag2 = false; if (entityDef.actions != null) {
		 * for (int k = 0; k < entityDef.actions.length; k++) { if
		 * (entityDef.actions[k] != null) { if
		 * (entityDef.actions[k].toLowerCase().equals("attack")) { flag2 = true;
		 * } if (entityDef.actions[k].toLowerCase().equals("pickpocket")) { flag
		 * = true; } } } } if (flag2) { // combatable out.write("	<npcdef>");
		 * out.newLine(); out.write("		<id>" + i + "</id>"); out.newLine(); if
		 * (entityDef.name != null) { out.write("		<name>" + entityDef.name +
		 * "</name>"); out.newLine(); }
		 * out.write("		<isAttackable>true</isAttackable>"); out.newLine();
		 * out.write("		<attackType>MELEE</attackType>"); out.newLine();
		 * out.write("		<combatLevels>"); out.newLine();
		 * out.write("			<int>0</int>"); out.newLine();
		 * out.write("			<int>0</int>"); out.newLine();
		 * out.write("			<int>0</int>"); out.newLine();
		 * out.write("			<int>0</int>"); out.newLine();
		 * out.write("			<int>0</int>"); out.newLine();
		 * out.write("			<int>0</int>"); out.newLine();
		 * out.write("			<int>0</int>"); out.newLine();
		 * out.write("		</combatLevels>"); out.newLine();
		 * out.write("		<attackAnim>" + (entityDef.walkAnimation + 1) +
		 * "</attackAnim>"); out.newLine(); out.write("		<deathAnimation>" +
		 * (entityDef.walkAnimation + 5) + "</deathAnimation>"); out.newLine();
		 * out.write("		<defenceAnim>" + (entityDef.walkAnimation + 2) +
		 * "</defenceAnim>"); out.newLine();
		 * out.write("		<respawnTimer>5</respawnTimer>"); out.newLine();
		 * out.write("		<hiddenTimer>5</hiddenTimer>"); out.newLine();
		 * out.write("		<attackSpeed>5</attackSpeed>"); out.newLine();
		 * out.write("		<maxHit>1</maxHit>"); out.newLine(); if (entityDef.size
		 * > 1) { out.write("		<size>" + entityDef.size + "</size>");
		 * out.newLine(); } if (flag) {
		 * out.write("		<isThievable>true</isThievable>"); out.newLine(); }
		 * out.write("	</npcdef>"); out.newLine(); out.close(); } else { // not
		 * combatable out.write("	<npcdef>"); out.newLine(); out.write("		<id>"
		 * + i + "</id>"); out.newLine(); if (entityDef.name != null) {
		 * out.write("		<name>" + entityDef.name + "</name>"); out.newLine(); }
		 * if (entityDef.size > 1) { out.write("		<size>" + entityDef.size +
		 * "</size>"); out.newLine(); } if (flag) {
		 * out.write("		<isThievable>true</isThievable>"); out.newLine(); }
		 * out.write("	</npcdef>"); out.newLine(); out.close(); }
		 * 
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		return entityDef;
	}

	public Model getModelHead() {
		if (childrenIDs != null) {
			NpcDefintion entityDef = method161();
			if (entityDef == null)
				return null;
			else
				return entityDef.getModelHead();
		}
		if (dialogueModels == null)
			return null;
		boolean flag1 = false;
		for (int i = 0; i < dialogueModels.length; i++)
			if (!Model.method463(dialogueModels[i]))
				flag1 = true;
		if (flag1)
			return null;
		Model model_3[] = new Model[dialogueModels.length];
		for (int j = 0; j < dialogueModels.length; j++)
			model_3[j] = Model.method462(dialogueModels[j]);
		Model model;
		if (model_3.length == 1)
			model = model_3[0];
		else
			model = new Model(model_3.length, model_3);
		if (originalModelColors != null) {
			for (int k = 0; k < originalModelColors.length; k++)
				model.method476(originalModelColors[k], modifiedModelColors[k]);
		}
		return model;
	}

	public NpcDefintion method161() {
		int j = -1;
		if (varBitChild != -1) {
			VarBit varBit = VarBit.cache[varBitChild];
			int k = varBit.configId;
			int l = varBit.leastSignificantBit;
			int i1 = varBit.mostSignificantBit;
			int j1 = RSClient.anIntArray1232[i1 - l];
			j = clientInstance.variousSettings[k] >> l & j1;
		} else if (configChild != -1)
			j = clientInstance.variousSettings[configChild];
		if (j < 0 || j >= childrenIDs.length || childrenIDs[j] == -1)
			return null;
		else
			return forID(childrenIDs[j]);
	}

	public static void unpackConfig(CacheArchive streamLoader) {
		stream = new RSBuffer(streamLoader.getDataForName("npc.dat"));
		RSBuffer stream2 = new RSBuffer(streamLoader.getDataForName("npc.idx"));
		int totalNPCs = stream2.readUShort();
		streamIndices = new int[totalNPCs];
		int i = 2;
		for (int j = 0; j < totalNPCs; j++) {
			streamIndices[j] = i;
			i += stream2.readUShort();
		}
		cache = new NpcDefintion[20];
		for (int k = 0; k < 20; k++)
			cache[k] = new NpcDefintion();
		for (int index = 0; index < totalNPCs; index++) {
			NpcDefintion ed = forID(index);
			if (ed == null)
				continue;
			if (ed.name == null)
				continue;
		}
	}

	public static void clearCache() {
		memCache = null;
		streamIndices = null;
		cache = null;
		stream = null;
	}

	public Model method164(int j, int k, int ai[]) {
		if (childrenIDs != null) {
			NpcDefintion entityDef = method161();
			if (entityDef == null)
				return null;
			else
				return entityDef.method164(j, k, ai);
		}
		Model model = (Model) memCache.insertFromCache(id);
		if (model == null) {
			boolean flag = false;
			for (int i1 = 0; i1 < modelArray.length; i1++)
				if (!Model.method463(modelArray[i1]))
					flag = true;
			if (flag)
				return null;
			Model aclass30_sub2_sub4_sub6s[] = new Model[modelArray.length];
			for (int j1 = 0; j1 < modelArray.length; j1++)
				aclass30_sub2_sub4_sub6s[j1] = Model.method462(modelArray[j1]);
			if (aclass30_sub2_sub4_sub6s.length == 1)
				model = aclass30_sub2_sub4_sub6s[0];
			else
				model = new Model(aclass30_sub2_sub4_sub6s.length, aclass30_sub2_sub4_sub6s);
			if (originalModelColors != null) {
				for (int k1 = 0; k1 < originalModelColors.length; k1++)
					model.method476(originalModelColors[k1], modifiedModelColors[k1]);
			}
			model.method469();
			model.light(64 + modelLighting, 850 + modelShadowing, -1, -5, -1, true);
			memCache.removeFromCache(model, id);
		}
		Model model_1 = Model.aModel_1621;
		model_1.method464(model, Animation.method532(k) & Animation.method532(j));
		if (k != -1 && j != -1)
			model_1.method471(ai, j, k);
		else if (k != -1)
			model_1.method470(k);
		if (vertexXY != 128 || vertexZ != 128)
			model_1.resize(vertexXY, vertexXY, vertexZ);
		model_1.method466();
		model_1.triangleSkin = null;
		model_1.vertexSkin = null;
		if (size == 1)
			model_1.oneSquareModel = true;
		return model_1;
	}

	public void readValues(RSBuffer stream) {
		do {
			int i = stream.readUByte();
			if (i == 0)
				return;
			if (i == 1) {
				int j = stream.readUByte();
				modelArray = new int[j];
				for (int j1 = 0; j1 < j; j1++)
					modelArray[j1] = stream.readUShort();
			} else if (i == 2)
				name = stream.readString();
			else if (i == 3)
				description = stream.readBytes();
			else if (i == 12)
				size = stream.readByte();
			else if (i == 13)
				standAnimation = stream.readUShort();
			else if (i == 14)
				walkAnimation = stream.readUShort();
			else if (i == 17) {
				walkAnimation = stream.readUShort();
				turn180Animation = stream.readUShort();
				turn90LeftAnimation = stream.readUShort();
				turn90RightAnimation = stream.readUShort();
			} else if (i >= 30 && i < 40) {
				if (actions == null)
					actions = new String[5];
				actions[i - 30] = stream.readString();
				if (actions[i - 30].equalsIgnoreCase("hidden"))
					actions[i - 30] = null;
			} else if (i == 40) {
				int k = stream.readUByte();
				originalModelColors = new int[k];
				modifiedModelColors = new int[k];
				for (int k1 = 0; k1 < k; k1++) {
					originalModelColors[k1] = stream.readUShort();
					modifiedModelColors[k1] = stream.readUShort();
				}
			} else if (i == 60) {
				int l = stream.readUByte();
				dialogueModels = new int[l];
				for (int l1 = 0; l1 < l; l1++)
					dialogueModels[l1] = stream.readUShort();
			} else if (i == 90)
				stream.readUShort();
			else if (i == 91)
				stream.readUShort();
			else if (i == 92)
				stream.readUShort();
			else if (i == 93)
				displayMapIcon = false;
			else if (i == 95)
				combatLevel = stream.readUShort();
			else if (i == 97)
				vertexXY = stream.readUShort();
			else if (i == 98)
				vertexZ = stream.readUShort();
			else if (i == 99)
				aBoolean93 = true;
			else if (i == 100)
				modelLighting = stream.readByte();
			else if (i == 101)
				modelShadowing = stream.readByte() * 5;
			else if (i == 102)
				headIcon = stream.readUShort();
			else if (i == 103)
				getDegreesToTurn = stream.readUShort();
			else if (i == 106) {
				varBitChild = stream.readUShort();
				if (varBitChild == 65535)
					varBitChild = -1;
				configChild = stream.readUShort();
				if (configChild == 65535)
					configChild = -1;
				int i1 = stream.readUByte();
				childrenIDs = new int[i1 + 1];
				for (int i2 = 0; i2 <= i1; i2++) {
					childrenIDs[i2] = stream.readUShort();
					if (childrenIDs[i2] == 65535)
						childrenIDs[i2] = -1;
				}
			} else if (i == 107)
				aBoolean84 = false;
		} while (true);
	}

	public NpcDefintion() {
		turn90RightAnimation = -1;
		varBitChild = -1;
		turn180Animation = -1;
		configChild = -1;
		combatLevel = -1;
		anInt64 = 1834;
		walkAnimation = -1;
		size = 1;
		headIcon = -1;
		standAnimation = -1;
		id = -1L;
		getDegreesToTurn = 32;
		turn90LeftAnimation = -1;
		aBoolean84 = true;
		vertexZ = 128;
		displayMapIcon = true;
		vertexXY = 128;
		aBoolean93 = false;
	}

	public int turn90RightAnimation;
	public static int cacheIndex;
	public int varBitChild;
	public int turn180Animation;
	public int configChild;
	public static RSBuffer stream;
	public int combatLevel;
	public final int anInt64;
	public String name;
	public String actions[];
	public int walkAnimation;
	public byte size;
	public int[] modifiedModelColors;
	public static int[] streamIndices;
	public int[] dialogueModels;
	public int headIcon;
	public int[] originalModelColors;
	public int standAnimation;
	public long id;
	public int getDegreesToTurn;
	public static NpcDefintion[] cache;
	public static RSClient clientInstance;
	public int turn90LeftAnimation;
	public boolean aBoolean84;
	public int modelLighting;
	public int vertexZ;
	public boolean displayMapIcon;
	public int childrenIDs[];
	public byte description[];
	public int vertexXY;
	public int modelShadowing;
	public boolean aBoolean93;
	public int[] modelArray;
	public static MemoryCache memCache = new MemoryCache(30);
}