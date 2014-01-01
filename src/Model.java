public class Model extends Renderable {

	public static void nullLoader() {
		modelHeaderCache = null;
		aBooleanArray1663 = null;
		aBooleanArray1664 = null;
		vertexSY = null;
		depthBuffer = null;
		vertexMvX = null;
		vertexMvY = null;
		vertexMvZ = null;
		depthListIndices = null;
		faceLists = null;
		anIntArray1673 = null;
		anIntArrayArray1674 = null;
		anIntArray1675 = null;
		anIntArray1676 = null;
		anIntArray1677 = null;
		SINE = null;
		COSINE = null;
		HSL2RGB = null;
		modelIntArray4 = null;
	}

	public void readModel525(byte abyte0[], int modelID) {
		RSBuffer nc1 = new RSBuffer(abyte0);
		RSBuffer nc2 = new RSBuffer(abyte0);
		RSBuffer nc3 = new RSBuffer(abyte0);
		RSBuffer nc4 = new RSBuffer(abyte0);
		RSBuffer nc5 = new RSBuffer(abyte0);
		RSBuffer nc6 = new RSBuffer(abyte0);
		RSBuffer nc7 = new RSBuffer(abyte0);
		nc1.pointer = abyte0.length - 23;
		int numVertices = nc1.readUShort();
		int numTriangles = nc1.readUShort();
		int numTexTriangles = nc1.readUByte();
		ModelHeader ModelDef_1 = modelHeaderCache[modelID] = new ModelHeader();
		ModelDef_1.aByteArray368 = abyte0;
		ModelDef_1.anInt369 = (short) numVertices;
		ModelDef_1.anInt370 = (short) numTriangles;
		ModelDef_1.anInt371 = (short) numTexTriangles;
		int l1 = nc1.readUByte();
		boolean bool = (0x1 & l1 ^ 0xffffffff) == -2;
		int i2 = nc1.readUByte();
		int j2 = nc1.readUByte();
		int k2 = nc1.readUByte();
		int l2 = nc1.readUByte();
		int i3 = nc1.readUByte();
		int j3 = nc1.readUShort();
		int k3 = nc1.readUShort();
		int l3 = nc1.readUShort();
		int i4 = nc1.readUShort();
		int j4 = nc1.readUShort();
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		byte[] x = null;
		byte[] O = null;
		byte[] J = null;
		byte[] F = null;
		byte[] cb = null;
		byte[] gb = null;
		byte[] lb = null;
		int[] kb = null;
		int[] y = null;
		int[] N = null;
		short[] D = null;
		int[] triangleColours2 = new int[numTriangles];
		if (numTexTriangles > 0) {
			O = new byte[numTexTriangles];
			nc1.pointer = 0;
			for (int j5 = 0; j5 < numTexTriangles; j5++) {
				byte byte0 = O[j5] = nc1.readByte();
				if (byte0 == 0)
					k4++;
				if (byte0 >= 1 && byte0 <= 3)
					l4++;
				if (byte0 == 2)
					i5++;
			}
		}
		int k5 = numTexTriangles;
		int l5 = k5;
		k5 += numVertices;
		int i6 = k5;
		if (l1 == 1)
			k5 += numTriangles;
		int j6 = k5;
		k5 += numTriangles;
		int k6 = k5;
		if (i2 == 255)
			k5 += numTriangles;
		int l6 = k5;
		if (k2 == 1)
			k5 += numTriangles;
		int i7 = k5;
		if (i3 == 1)
			k5 += numVertices;
		int j7 = k5;
		if (j2 == 1)
			k5 += numTriangles;
		int k7 = k5;
		k5 += i4;
		int l7 = k5;
		if (l2 == 1)
			k5 += numTriangles * 2;
		int i8 = k5;
		k5 += j4;
		int j8 = k5;
		k5 += numTriangles * 2;
		int k8 = k5;
		k5 += j3;
		int l8 = k5;
		k5 += k3;
		int i9 = k5;
		k5 += l3;
		int j9 = k5;
		k5 += k4 * 6;
		int k9 = k5;
		k5 += l4 * 6;
		int l9 = k5;
		k5 += l4 * 6;
		int i10 = k5;
		k5 += l4;
		int j10 = k5;
		k5 += l4;
		int k10 = k5;
		k5 += l4 + i5 * 2;
		short[] vertexX1 = new short[numVertices];
		short[] vertexY1 = new short[numVertices];
		short[] vertexZ1 = new short[numVertices];
		int[] facePoint1 = new int[numTriangles];
		int[] facePoint2 = new int[numTriangles];
		int[] facePoint3 = new int[numTriangles];
		vertexVSkin = new short[numVertices];
		triangleDrawType = new short[numTriangles];
		facePriority = new short[numTriangles];
		triangleAlpha = new short[numTriangles];
		triangleTSkin = new short[numTriangles];
		if (i3 == 1)
			vertexVSkin = new short[numVertices];
		if (bool)
			triangleDrawType = new short[numTriangles];
		if (i2 == 255)
			facePriority = new short[numTriangles];
		else {
		}
		if (j2 == 1)
			triangleAlpha = new short[numTriangles];
		if (k2 == 1)
			triangleTSkin = new short[numTriangles];
		if (l2 == 1)
			D = new short[numTriangles];
		if (l2 == 1 && numTexTriangles > 0)
			x = new byte[numTriangles];
		triangleColours2 = new int[numTriangles];
		int[] texTrianglesPoint1 = null;
		int[] texTrianglesPoint2 = null;
		int[] texTrianglesPoint3 = null;
		if (numTexTriangles > 0) {
			texTrianglesPoint1 = new int[numTexTriangles];
			texTrianglesPoint2 = new int[numTexTriangles];
			texTrianglesPoint3 = new int[numTexTriangles];
			if (l4 > 0) {
				kb = new int[l4];
				N = new int[l4];
				y = new int[l4];
				gb = new byte[l4];
				lb = new byte[l4];
				F = new byte[l4];
			}
			if (i5 > 0) {
				cb = new byte[i5];
				J = new byte[i5];
			}
		}
		nc1.pointer = l5;
		nc2.pointer = k8;
		nc3.pointer = l8;
		nc4.pointer = i9;
		nc5.pointer = i7;
		int l10 = 0;
		int i11 = 0;
		int j11 = 0;
		for (int k11 = 0; k11 < numVertices; k11++) {
			int l11 = nc1.readUByte();
			int j12 = 0;
			if ((l11 & 1) != 0)
				j12 = nc2.method421();
			int l12 = 0;
			if ((l11 & 2) != 0)
				l12 = nc3.method421();
			int j13 = 0;
			if ((l11 & 4) != 0)
				j13 = nc4.method421();
			vertexX1[k11] = (short) (l10 + j12);
			vertexY1[k11] = (short) (i11 + l12);
			vertexZ1[k11] = (short) (j11 + j13);
			l10 = vertexX1[k11];
			i11 = vertexY1[k11];
			j11 = vertexZ1[k11];
			if (vertexVSkin != null)
				vertexVSkin[k11] = (short) nc5.readUByte();
		}
		nc1.pointer = j8;
		nc2.pointer = i6;
		nc3.pointer = k6;
		nc4.pointer = j7;
		nc5.pointer = l6;
		nc6.pointer = l7;
		nc7.pointer = i8;
		for (int i12 = 0; i12 < numTriangles; i12++) {
			triangleColours2[i12] = nc1.readUShort();
			if (l1 == 1) {
				triangleDrawType[i12] = nc2.readByte();
				if (triangleDrawType[i12] == 2)
					triangleColours2[i12] = 65535;
				triangleDrawType[i12] = 0;
			}
			if (i2 == 255) {
				facePriority[i12] = nc3.readByte();
			}
			if (j2 == 1) {
				triangleAlpha[i12] = nc4.readByte();
				if (triangleAlpha[i12] < 0)
					triangleAlpha[i12] = (short) (256 + triangleAlpha[i12]);
			}
			if (k2 == 1)
				triangleTSkin[i12] = (short) nc5.readUByte();
			if (l2 == 1)
				D[i12] = (short) (nc6.readUShort() - 1);
			if (x != null)
				if (D[i12] != -1)
					x[i12] = (byte) (nc7.readUByte() - 1);
				else
					x[i12] = -1;
		}
		nc1.pointer = k7;
		nc2.pointer = j6;
		int k12 = 0;
		int i13 = 0;
		int k13 = 0;
		int l13 = 0;
		for (int i14 = 0; i14 < numTriangles; i14++) {
			int j14 = nc2.readUByte();
			if (j14 == 1) {
				k12 = nc1.method421() + l13;
				l13 = k12;
				i13 = nc1.method421() + l13;
				l13 = i13;
				k13 = nc1.method421() + l13;
				l13 = k13;
				facePoint1[i14] = k12;
				facePoint2[i14] = i13;
				facePoint3[i14] = k13;
			}
			if (j14 == 2) {
				i13 = k13;
				k13 = nc1.method421() + l13;
				l13 = k13;
				facePoint1[i14] = k12;
				facePoint2[i14] = i13;
				facePoint3[i14] = k13;
			}
			if (j14 == 3) {
				k12 = k13;
				k13 = nc1.method421() + l13;
				l13 = k13;
				facePoint1[i14] = k12;
				facePoint2[i14] = i13;
				facePoint3[i14] = k13;
			}
			if (j14 == 4) {
				int l14 = k12;
				k12 = i13;
				i13 = l14;
				k13 = nc1.method421() + l13;
				l13 = k13;
				facePoint1[i14] = k12;
				facePoint2[i14] = i13;
				facePoint3[i14] = k13;
			}
		}
		nc1.pointer = j9;
		nc2.pointer = k9;
		nc3.pointer = l9;
		nc4.pointer = i10;
		nc5.pointer = j10;
		nc6.pointer = k10;
		for (int k14 = 0; k14 < numTexTriangles; k14++) {
			int i15 = O[k14] & 0xff;
			if (i15 == 0) {
				texTrianglesPoint1[k14] = nc1.readUShort();
				texTrianglesPoint2[k14] = nc1.readUShort();
				texTrianglesPoint3[k14] = nc1.readUShort();
			}
			if (i15 == 1) {
				texTrianglesPoint1[k14] = nc2.readUShort();
				texTrianglesPoint2[k14] = nc2.readUShort();
				texTrianglesPoint3[k14] = nc2.readUShort();
				kb[k14] = nc3.readUShort();
				N[k14] = nc3.readUShort();
				y[k14] = nc3.readUShort();
				gb[k14] = nc4.readByte();
				lb[k14] = nc5.readByte();
				F[k14] = nc6.readByte();
			}
			if (i15 == 2) {
				texTrianglesPoint1[k14] = nc2.readUShort();
				texTrianglesPoint2[k14] = nc2.readUShort();
				texTrianglesPoint3[k14] = nc2.readUShort();
				kb[k14] = nc3.readUShort();
				N[k14] = nc3.readUShort();
				y[k14] = nc3.readUShort();
				gb[k14] = nc4.readByte();
				lb[k14] = nc5.readByte();
				F[k14] = nc6.readByte();
				cb[k14] = nc6.readByte();
				J[k14] = nc6.readByte();
			}
			if (i15 == 3) {
				texTrianglesPoint1[k14] = nc2.readUShort();
				texTrianglesPoint2[k14] = nc2.readUShort();
				texTrianglesPoint3[k14] = nc2.readUShort();
				kb[k14] = nc3.readUShort();
				N[k14] = nc3.readUShort();
				y[k14] = nc3.readUShort();
				gb[k14] = nc4.readByte();
				lb[k14] = nc5.readByte();
				F[k14] = nc6.readByte();
			}
		}
		if (i2 != 255) {
			for (int i12 = 0; i12 < numTriangles; i12++)
				facePriority[i12] = (short) i2;
		}
		triangleColourOrTexture = triangleColours2;
		vertexCount = (short) numVertices;
		triangleCount = (short) numTriangles;
		vertexX = vertexX1;
		vertexY = vertexY1;
		vertexZ = vertexZ1;
		triangleA = facePoint1;
		triangleB = facePoint2;
		triangleC = facePoint3;
	}

	private Model(int modelId) {
		byte[] is = modelHeaderCache[modelId].aByteArray368;
		if (is[is.length - 1] == -1 && is[is.length - 2] == -1)
			readModel525(is, modelId);
		else
			readModel317(modelId);
	}

	private void readModel317(int i) {
		oneSquareModel = false;
		ModelHeader class21 = modelHeaderCache[i];
		vertexCount = (short) class21.anInt369;
		triangleCount = (short) class21.anInt370;
		textureTriangleCount = (short) class21.anInt371;
		vertexX = new short[vertexCount];
		vertexY = new short[vertexCount];
		vertexZ = new short[vertexCount];
		triangleA = new int[triangleCount];
		triangleB = new int[triangleCount];
		triangleC = new int[triangleCount];
		triPIndex = new short[textureTriangleCount];
		triMIndex = new short[textureTriangleCount];
		triNIndex = new short[textureTriangleCount];
		if (class21.anInt376 >= 0)
			vertexVSkin = new short[vertexCount];
		if (class21.anInt380 >= 0)
			triangleDrawType = new short[triangleCount];
		if (class21.anInt381 >= 0)
			facePriority = new short[triangleCount];
		else
			anInt1641 = (short) (-class21.anInt381 - 1);
		if (class21.anInt382 >= 0)
			triangleAlpha = new short[triangleCount];
		if (class21.anInt383 >= 0)
			triangleTSkin = new short[triangleCount];
		triangleColourOrTexture = new int[triangleCount];
		RSBuffer stream = new RSBuffer(class21.aByteArray368);
		stream.pointer = class21.anInt372;
		RSBuffer stream_1 = new RSBuffer(class21.aByteArray368);
		stream_1.pointer = class21.anInt373;
		RSBuffer stream_2 = new RSBuffer(class21.aByteArray368);
		stream_2.pointer = class21.anInt374;
		RSBuffer stream_3 = new RSBuffer(class21.aByteArray368);
		stream_3.pointer = class21.anInt375;
		RSBuffer stream_4 = new RSBuffer(class21.aByteArray368);
		stream_4.pointer = class21.anInt376;
		int k = 0;
		int l = 0;
		int i1 = 0;
		for (int j1 = 0; j1 < vertexCount; j1++) {
			int k1 = stream.readUByte();
			int i2 = 0;
			if ((k1 & 1) != 0)
				i2 = stream_1.method421();
			int k2 = 0;
			if ((k1 & 2) != 0)
				k2 = stream_2.method421();
			int i3 = 0;
			if ((k1 & 4) != 0)
				i3 = stream_3.method421();
			vertexX[j1] = (short) (k + i2);
			vertexY[j1] = (short) (l + k2);
			vertexZ[j1] = (short) (i1 + i3);
			k = vertexX[j1];
			l = vertexY[j1];
			i1 = vertexZ[j1];
			if (vertexVSkin != null)
				vertexVSkin[j1] = (short) stream_4.readUByte();
		}
		stream.pointer = class21.anInt379;
		stream_1.pointer = class21.anInt380;
		stream_2.pointer = class21.anInt381;
		stream_3.pointer = class21.anInt382;
		stream_4.pointer = class21.anInt383;
		for (int l1 = 0; l1 < triangleCount; l1++) {
			triangleColourOrTexture[l1] = stream.readUShort();
			if (triangleDrawType != null)
				triangleDrawType[l1] = (short) stream_1.readUByte();
			if (facePriority != null)
				facePriority[l1] = (short) stream_2.readUByte();
			if (triangleAlpha != null)
				triangleAlpha[l1] = (short) stream_3.readUByte();
			if (triangleTSkin != null)
				triangleTSkin[l1] = (short) stream_4.readUByte();
		}
		stream.pointer = class21.anInt377;
		stream_1.pointer = class21.anInt378;
		int j2 = 0;
		int l2 = 0;
		int j3 = 0;
		int k3 = 0;
		for (int l3 = 0; l3 < triangleCount; l3++) {
			int i4 = stream_1.readUByte();
			if (i4 == 1) {
				j2 = stream.method421() + k3;
				k3 = j2;
				l2 = stream.method421() + k3;
				k3 = l2;
				j3 = stream.method421() + k3;
				k3 = j3;
				triangleA[l3] = j2;
				triangleB[l3] = l2;
				triangleC[l3] = j3;
			}
			if (i4 == 2) {
				l2 = j3;
				j3 = stream.method421() + k3;
				k3 = j3;
				triangleA[l3] = j2;
				triangleB[l3] = l2;
				triangleC[l3] = j3;
			}
			if (i4 == 3) {
				j2 = j3;
				j3 = stream.method421() + k3;
				k3 = j3;
				triangleA[l3] = j2;
				triangleB[l3] = l2;
				triangleC[l3] = j3;
			}
			if (i4 == 4) {
				int k4 = j2;
				j2 = l2;
				l2 = k4;
				j3 = stream.method421() + k3;
				k3 = j3;
				triangleA[l3] = j2;
				triangleB[l3] = l2;
				triangleC[l3] = j3;
			}
		}
		stream.pointer = class21.anInt384;
		for (int j4 = 0; j4 < textureTriangleCount; j4++) {
			triPIndex[j4] = (short) stream.readUShort();
			triMIndex[j4] = (short) stream.readUShort();
			triNIndex[j4] = (short) stream.readUShort();
		}
	}

	public static void method460(byte abyte0[], int j) {
		if (abyte0 == null) {
			ModelHeader class21 = modelHeaderCache[j] = new ModelHeader();
			class21.anInt369 = 0;
			class21.anInt370 = 0;
			class21.anInt371 = 0;
			return;
		}
		if (abyte0.length <= 0)
			return;
		RSBuffer stream = new RSBuffer(abyte0);
		stream.pointer = abyte0.length - 18;
		ModelHeader class21_1 = modelHeaderCache[j] = new ModelHeader();
		class21_1.aByteArray368 = abyte0;
		class21_1.anInt369 = (short) stream.readUShort();
		class21_1.anInt370 = (short) stream.readUShort();
		class21_1.anInt371 = (short) stream.readUByte();
		int k = stream.readUByte();
		int l = stream.readUByte();
		int i1 = stream.readUByte();
		int j1 = stream.readUByte();
		int k1 = stream.readUByte();
		int l1 = stream.readUShort();
		int i2 = stream.readUShort();
		int j2 = stream.readUShort();
		int k2 = stream.readUShort();
		int l2 = 0;
		class21_1.anInt372 = (short) l2;
		l2 += class21_1.anInt369;
		class21_1.anInt378 = (short) l2;
		l2 += class21_1.anInt370;
		class21_1.anInt381 = (short) l2;
		if (l == 255)
			l2 += class21_1.anInt370;
		else
			class21_1.anInt381 = (short) (-l - 1);
		class21_1.anInt383 = (short) l2;
		if (j1 == 1)
			l2 += class21_1.anInt370;
		else
			class21_1.anInt383 = -1;
		class21_1.anInt380 = (short) l2;
		if (k == 1)
			l2 += class21_1.anInt370;
		else
			class21_1.anInt380 = -1;
		class21_1.anInt376 = (short) l2;
		if (k1 == 1)
			l2 += class21_1.anInt369;
		else
			class21_1.anInt376 = -1;
		class21_1.anInt382 = (short) l2;
		if (i1 == 1)
			l2 += class21_1.anInt370;
		else
			class21_1.anInt382 = -1;
		class21_1.anInt377 = (short) l2;
		l2 += k2;
		class21_1.anInt379 = (short) l2;
		l2 += class21_1.anInt370 * 2;
		class21_1.anInt384 = (short) l2;
		l2 += class21_1.anInt371 * 6;
		class21_1.anInt373 = (short) l2;
		l2 += l1;
		class21_1.anInt374 = (short) l2;
		l2 += i2;
		class21_1.anInt375 = (short) l2;
		l2 += j2;
	}

	public static void method459(ResourceProvider provider) {
		modelHeaderCache = new ModelHeader[29192];
		resourceProvider = provider;
	}

	public static void method461(int j) {
		modelHeaderCache[j] = null;
	}

	public static Model method462(int j) {
		if (modelHeaderCache == null)
			return null;
		ModelHeader class21 = modelHeaderCache[j];
		if (class21 == null) {
			resourceProvider.method558(0, j);
			return null;
		}
		return new Model(j);
	}

	public static boolean method463(int i) {
		if (modelHeaderCache == null)
			return false;
		ModelHeader class21 = modelHeaderCache[i];
		if (class21 == null) {
			resourceProvider.method558(0, i);
			return false;
		}
		return true;
	}

	public Model(boolean flag) {
		anInt1614 = 9;
		aBoolean1615 = false;
		anInt1616 = 360;
		anInt1617 = 1;
		aBoolean1618 = true;
		oneSquareModel = false;
		if (!flag)
			aBoolean1618 = !aBoolean1618;
	}

	public Model(int i, Model amodel[]) {
		anInt1614 = 9;
		aBoolean1615 = false;
		anInt1616 = 360;
		anInt1617 = 1;
		aBoolean1618 = true;
		oneSquareModel = false;
		anInt1620++;
		boolean flag = false;
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		vertexCount = 0;
		triangleCount = 0;
		textureTriangleCount = 0;
		anInt1641 = -1;
		for (int k = 0; k < i; k++) {
			Model model = amodel[k];
			if (model != null) {
				vertexCount += model.vertexCount;
				triangleCount += model.triangleCount;
				textureTriangleCount += model.textureTriangleCount;
				flag |= model.triangleDrawType != null;
				if (model.facePriority != null) {
					flag1 = true;
				} else {
					if (anInt1641 == -1)
						anInt1641 = model.anInt1641;
					if (anInt1641 != model.anInt1641)
						flag1 = true;
				}
				flag2 |= model.triangleAlpha != null;
				flag3 |= model.triangleTSkin != null;
			}
		}
		vertexX = new short[vertexCount];
		vertexY = new short[vertexCount];
		vertexZ = new short[vertexCount];
		vertexVSkin = new short[vertexCount];
		triangleA = new int[triangleCount];
		triangleB = new int[triangleCount];
		triangleC = new int[triangleCount];
		triPIndex = new short[textureTriangleCount];
		triMIndex = new short[textureTriangleCount];
		triNIndex = new short[textureTriangleCount];
		if (flag)
			triangleDrawType = new short[triangleCount];
		if (flag1)
			facePriority = new short[triangleCount];
		if (flag2)
			triangleAlpha = new short[triangleCount];
		if (flag3)
			triangleTSkin = new short[triangleCount];
		triangleColourOrTexture = new int[triangleCount];
		vertexCount = 0;
		triangleCount = 0;
		textureTriangleCount = 0;
		int l = 0;
		for (int i1 = 0; i1 < i; i1++) {
			Model model_1 = amodel[i1];
			if (model_1 != null) {
				for (int j1 = 0; j1 < model_1.triangleCount; j1++) {
					if (flag)
						if (model_1.triangleDrawType == null) {
							triangleDrawType[triangleCount] = 0;
						} else {
							int k1 = model_1.triangleDrawType[j1];
							if ((k1 & 2) == 2)
								k1 += l << 2;
							triangleDrawType[triangleCount] = (short) k1;
						}
					if (flag1)
						if (model_1.facePriority == null)
							facePriority[triangleCount] = model_1.anInt1641;
						else
							facePriority[triangleCount] = model_1.facePriority[j1];
					if (flag2)
						if (model_1.triangleAlpha == null)
							triangleAlpha[triangleCount] = 0;
						else
							triangleAlpha[triangleCount] = model_1.triangleAlpha[j1];
					if (flag3 && model_1.triangleTSkin != null)
						triangleTSkin[triangleCount] = model_1.triangleTSkin[j1];
					triangleColourOrTexture[triangleCount] = model_1.triangleColourOrTexture[j1];
					triangleA[triangleCount] = method465(model_1, model_1.triangleA[j1]);
					triangleB[triangleCount] = method465(model_1, model_1.triangleB[j1]);
					triangleC[triangleCount] = method465(model_1, model_1.triangleC[j1]);
					triangleCount++;
				}
				for (int l1 = 0; l1 < model_1.textureTriangleCount; l1++) {
					triPIndex[textureTriangleCount] = (short) method465(model_1, model_1.triPIndex[l1]);
					triMIndex[textureTriangleCount] = (short) method465(model_1, model_1.triMIndex[l1]);
					triNIndex[textureTriangleCount] = (short) method465(model_1, model_1.triNIndex[l1]);
					textureTriangleCount++;
				}
				l += model_1.textureTriangleCount;
			}
		}
	}

	public Model(Model amodel[]) {
		int i = 2;
		anInt1614 = 9;
		aBoolean1615 = false;
		anInt1616 = 360;
		anInt1617 = 1;
		aBoolean1618 = true;
		oneSquareModel = false;
		anInt1620++;
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		boolean flag4 = false;
		vertexCount = 0;
		triangleCount = 0;
		textureTriangleCount = 0;
		anInt1641 = -1;
		for (int k = 0; k < i; k++) {
			Model model = amodel[k];
			if (model != null) {
				vertexCount += model.vertexCount;
				triangleCount += model.triangleCount;
				textureTriangleCount += model.textureTriangleCount;
				flag1 |= model.triangleDrawType != null;
				if (model.facePriority != null) {
					flag2 = true;
				} else {
					if (anInt1641 == -1)
						anInt1641 = model.anInt1641;
					if (anInt1641 != model.anInt1641)
						flag2 = true;
				}
				flag3 |= model.triangleAlpha != null;
				flag4 |= model.triangleColourOrTexture != null;
			}
		}
		vertexX = new short[vertexCount];
		vertexY = new short[vertexCount];
		vertexZ = new short[vertexCount];
		triangleA = new int[triangleCount];
		triangleB = new int[triangleCount];
		triangleC = new int[triangleCount];
		triangleHslA = new int[triangleCount];
		triangleHslB = new int[triangleCount];
		triangleHslC = new int[triangleCount];
		triPIndex = new short[textureTriangleCount];
		triMIndex = new short[textureTriangleCount];
		triNIndex = new short[textureTriangleCount];
		if (flag1)
			triangleDrawType = new short[triangleCount];
		if (flag2)
			facePriority = new short[triangleCount];
		if (flag3)
			triangleAlpha = new short[triangleCount];
		if (flag4)
			triangleColourOrTexture = new int[triangleCount];
		vertexCount = 0;
		triangleCount = 0;
		textureTriangleCount = 0;
		int i1 = 0;
		for (int j1 = 0; j1 < i; j1++) {
			Model model_1 = amodel[j1];
			if (model_1 != null) {
				int k1 = vertexCount;
				for (int l1 = 0; l1 < model_1.vertexCount; l1++) {
					vertexX[vertexCount] = model_1.vertexX[l1];
					vertexY[vertexCount] = model_1.vertexY[l1];
					vertexZ[vertexCount] = model_1.vertexZ[l1];
					vertexCount++;
				}
				for (int i2 = 0; i2 < model_1.triangleCount; i2++) {
					triangleA[triangleCount] = model_1.triangleA[i2] + k1;
					triangleB[triangleCount] = model_1.triangleB[i2] + k1;
					triangleC[triangleCount] = model_1.triangleC[i2] + k1;
					triangleHslA[triangleCount] = model_1.triangleHslA[i2];
					triangleHslB[triangleCount] = model_1.triangleHslB[i2];
					triangleHslC[triangleCount] = model_1.triangleHslC[i2];
					if (flag1)
						if (model_1.triangleDrawType == null) {
							triangleDrawType[triangleCount] = 0;
						} else {
							int j2 = model_1.triangleDrawType[i2];
							if ((j2 & 2) == 2)
								j2 += i1 << 2;
							triangleDrawType[triangleCount] = (short) j2;
						}
					if (flag2)
						if (model_1.facePriority == null)
							facePriority[triangleCount] = model_1.anInt1641;
						else
							facePriority[triangleCount] = model_1.facePriority[i2];
					if (flag3)
						if (model_1.triangleAlpha == null)
							triangleAlpha[triangleCount] = 0;
						else
							triangleAlpha[triangleCount] = model_1.triangleAlpha[i2];
					if (flag4 && model_1.triangleColourOrTexture != null)
						triangleColourOrTexture[triangleCount] = model_1.triangleColourOrTexture[i2];
					triangleCount++;
				}
				for (int k2 = 0; k2 < model_1.textureTriangleCount; k2++) {
					triPIndex[textureTriangleCount] = (short) (model_1.triPIndex[k2] + k1);
					triMIndex[textureTriangleCount] = (short) (model_1.triMIndex[k2] + k1);
					triNIndex[textureTriangleCount] = (short) (model_1.triNIndex[k2] + k1);
					textureTriangleCount++;
				}
				i1 += model_1.textureTriangleCount;
			}
		}
		method466();
	}

	public Model(boolean flag, boolean flag1, boolean flag2, Model model) {
		anInt1614 = 9;
		aBoolean1615 = false;
		anInt1616 = 360;
		anInt1617 = 1;
		aBoolean1618 = true;
		oneSquareModel = false;
		anInt1620++;
		vertexCount = model.vertexCount;
		triangleCount = model.triangleCount;
		textureTriangleCount = model.textureTriangleCount;
		if (flag2) {
			vertexX = model.vertexX;
			vertexY = model.vertexY;
			vertexZ = model.vertexZ;
		} else {
			vertexX = new short[vertexCount];
			vertexY = new short[vertexCount];
			vertexZ = new short[vertexCount];
			for (int j = 0; j < vertexCount; j++) {
				vertexX[j] = model.vertexX[j];
				vertexY[j] = model.vertexY[j];
				vertexZ[j] = model.vertexZ[j];
			}
		}
		if (flag) {
			triangleColourOrTexture = model.triangleColourOrTexture;
		} else {
			triangleColourOrTexture = new int[triangleCount];
			for (int k = 0; k < triangleCount; k++)
				triangleColourOrTexture[k] = model.triangleColourOrTexture[k];
		}
		if (flag1) {
			triangleAlpha = model.triangleAlpha;
		} else {
			triangleAlpha = new short[triangleCount];
			if (model.triangleAlpha == null) {
				for (int l = 0; l < triangleCount; l++)
					triangleAlpha[l] = 0;
			} else {
				for (int i1 = 0; i1 < triangleCount; i1++)
					triangleAlpha[i1] = model.triangleAlpha[i1];
			}
		}
		vertexVSkin = model.vertexVSkin;
		triangleTSkin = model.triangleTSkin;
		triangleDrawType = model.triangleDrawType;
		triangleA = model.triangleA;
		triangleB = model.triangleB;
		triangleC = model.triangleC;
		facePriority = model.facePriority;
		anInt1641 = model.anInt1641;
		triPIndex = model.triPIndex;
		triMIndex = model.triMIndex;
		triNIndex = model.triNIndex;
	}

	public Model(boolean flag, boolean flag1, Model model) {
		anInt1614 = 9;
		aBoolean1615 = false;
		anInt1616 = 360;
		anInt1617 = 1;
		aBoolean1618 = true;
		oneSquareModel = false;
		anInt1620++;
		vertexCount = model.vertexCount;
		triangleCount = model.triangleCount;
		textureTriangleCount = model.textureTriangleCount;
		if (flag) {
			vertexY = new short[vertexCount];
			for (int j = 0; j < vertexCount; j++)
				vertexY[j] = model.vertexY[j];
		} else {
			vertexY = model.vertexY;
		}
		if (flag1) {
			triangleHslA = new int[triangleCount];
			triangleHslB = new int[triangleCount];
			triangleHslC = new int[triangleCount];
			for (int k = 0; k < triangleCount; k++) {
				triangleHslA[k] = model.triangleHslA[k];
				triangleHslB[k] = model.triangleHslB[k];
				triangleHslC[k] = model.triangleHslC[k];
			}
			triangleDrawType = new short[triangleCount];
			if (model.triangleDrawType == null) {
				for (int l = 0; l < triangleCount; l++)
					triangleDrawType[l] = 0;
			} else {
				for (int i1 = 0; i1 < triangleCount; i1++)
					triangleDrawType[i1] = model.triangleDrawType[i1];
			}
			super.vertexNormals = new VertexNormal[vertexCount];
			for (int j1 = 0; j1 < vertexCount; j1++) {
				VertexNormal class33 = super.vertexNormals[j1] = new VertexNormal();
				VertexNormal class33_1 = model.vertexNormals[j1];
				class33.x = class33_1.x;
				class33.y = class33_1.y;
				class33.z = class33_1.z;
				class33.magnitude = class33_1.magnitude;
			}
			vertexNormalOffset = model.vertexNormalOffset;
		} else {
			triangleHslA = model.triangleHslA;
			triangleHslB = model.triangleHslB;
			triangleHslC = model.triangleHslC;
			triangleDrawType = model.triangleDrawType;
		}
		vertexX = model.vertexX;
		vertexZ = model.vertexZ;
		triangleColourOrTexture = model.triangleColourOrTexture;
		triangleAlpha = model.triangleAlpha;
		facePriority = model.facePriority;
		anInt1641 = model.anInt1641;
		triangleA = model.triangleA;
		triangleB = model.triangleB;
		triangleC = model.triangleC;
		triPIndex = model.triPIndex;
		triMIndex = model.triMIndex;
		triNIndex = model.triNIndex;
		super.modelHeight = model.modelHeight;
		diagonal2DAboveorigin = model.diagonal2DAboveorigin;
		diagonal3DAboveorigin = model.diagonal3DAboveorigin;
		diagonal3D = model.diagonal3D;
		minX = model.minX;
		maxZ = model.maxZ;
		minZ = model.minZ;
		maxX = model.maxX;
	}

	public void method464(Model model, boolean flag) {
		vertexCount = model.vertexCount;
		triangleCount = model.triangleCount;
		textureTriangleCount = model.textureTriangleCount;
		if (anIntArray1622.length < vertexCount) {
			anIntArray1622 = new short[vertexCount + 10000];
			anIntArray1623 = new short[vertexCount + 10000];
			anIntArray1624 = new short[vertexCount + 10000];
		}
		vertexX = anIntArray1622;
		vertexY = anIntArray1623;
		vertexZ = anIntArray1624;
		for (int k = 0; k < vertexCount; k++) {
			vertexX[k] = model.vertexX[k];
			vertexY[k] = model.vertexY[k];
			vertexZ[k] = model.vertexZ[k];
		}
		if (flag) {
			triangleAlpha = model.triangleAlpha;
		} else {
			if (anIntArray1625.length < triangleCount)
				anIntArray1625 = new short[triangleCount + 100];
			triangleAlpha = anIntArray1625;
			if (model.triangleAlpha == null) {
				for (int l = 0; l < triangleCount; l++)
					triangleAlpha[l] = 0;
			} else {
				for (int i1 = 0; i1 < triangleCount; i1++)
					triangleAlpha[i1] = model.triangleAlpha[i1];
			}
		}
		triangleDrawType = model.triangleDrawType;
		triangleColourOrTexture = model.triangleColourOrTexture;
		facePriority = model.facePriority;
		anInt1641 = model.anInt1641;
		triangleSkin = model.triangleSkin;
		vertexSkin = model.vertexSkin;
		triangleA = model.triangleA;
		triangleB = model.triangleB;
		triangleC = model.triangleC;
		triangleHslA = model.triangleHslA;
		triangleHslB = model.triangleHslB;
		triangleHslC = model.triangleHslC;
		triPIndex = model.triPIndex;
		triMIndex = model.triMIndex;
		triNIndex = model.triNIndex;
	}

	public final int method465(Model model, int i) {
		int j = -1;
		int k = model.vertexX[i];
		int l = model.vertexY[i];
		int i1 = model.vertexZ[i];
		for (int j1 = 0; j1 < vertexCount; j1++) {
			if (k != vertexX[j1] || l != vertexY[j1] || i1 != vertexZ[j1])
				continue;
			j = j1;
			break;
		}
		if (j == -1) {
			vertexX[vertexCount] = (short) k;
			vertexY[vertexCount] = (short) l;
			vertexZ[vertexCount] = (short) i1;
			if (model.vertexVSkin != null)
				vertexVSkin[vertexCount] = model.vertexVSkin[i];
			j = vertexCount++;
		}
		return j;
	}

	public void method466() {
		super.modelHeight = 0;
		diagonal2DAboveorigin = 0;
		maxY = 0;
		for (int i = 0; i < vertexCount; i++) {
			int j = vertexX[i];
			int k = vertexY[i];
			int l = vertexZ[i];
			if (-k > super.modelHeight)
				super.modelHeight = -k;
			if (k > maxY)
				maxY = k;
			int i1 = j * j + l * l;
			if (i1 > diagonal2DAboveorigin)
				diagonal2DAboveorigin = i1;
		}
		diagonal2DAboveorigin = (int) (Math.sqrt(diagonal2DAboveorigin) + 0.98999999999999999D);
		diagonal3DAboveorigin = (int) (Math.sqrt(diagonal2DAboveorigin * diagonal2DAboveorigin + super.modelHeight * super.modelHeight) + 0.98999999999999999D);
		diagonal3D = diagonal3DAboveorigin
				+ (int) (Math.sqrt(diagonal2DAboveorigin * diagonal2DAboveorigin + maxY * maxY) + 0.98999999999999999D);
	}

	public void method467() {
		super.modelHeight = 0;
		maxY = 0;
		for (int i = 0; i < vertexCount; i++) {
			int j = vertexY[i];
			if (-j > super.modelHeight)
				super.modelHeight = -j;
			if (j > maxY)
				maxY = j;
		}
		diagonal3DAboveorigin = (int) (Math.sqrt(diagonal2DAboveorigin * diagonal2DAboveorigin + super.modelHeight * super.modelHeight) + 0.98999999999999999D);
		diagonal3D = diagonal3DAboveorigin
				+ (int) (Math.sqrt(diagonal2DAboveorigin * diagonal2DAboveorigin + maxY * maxY) + 0.98999999999999999D);
	}

	public void method468(int i) {
		super.modelHeight = 0;
		diagonal2DAboveorigin = 0;
		maxY = 0;
		minX = 0xf423f;
		maxX = 0xfff0bdc1;
		maxZ = 0xfffe7961;
		minZ = 0x1869f;
		for (int j = 0; j < vertexCount; j++) {
			int k = vertexX[j];
			int l = vertexY[j];
			int i1 = vertexZ[j];
			if (k < minX)
				minX = k;
			if (k > maxX)
				maxX = k;
			if (i1 < minZ)
				minZ = i1;
			if (i1 > maxZ)
				maxZ = i1;
			if (-l > super.modelHeight)
				super.modelHeight = -l;
			if (l > maxY)
				maxY = l;
			int j1 = k * k + i1 * i1;
			if (j1 > diagonal2DAboveorigin)
				diagonal2DAboveorigin = j1;
		}
		diagonal2DAboveorigin = (int) Math.sqrt(diagonal2DAboveorigin);
		diagonal3DAboveorigin = (int) Math.sqrt(diagonal2DAboveorigin * diagonal2DAboveorigin + super.modelHeight * super.modelHeight);
		if (i != 21073) {
			return;
		} else {
			diagonal3D = diagonal3DAboveorigin + (int) Math.sqrt(diagonal2DAboveorigin * diagonal2DAboveorigin + maxY * maxY);
			return;
		}
	}

	public void method469() {
		try {
			if (vertexVSkin != null) {
				int ai[] = new int[256];
				int j = 0;
				for (int l = 0; l < vertexCount; l++) {
					int j1 = vertexVSkin[l];
					ai[j1]++;
					if (j1 > j)
						j = j1;
				}
				vertexSkin = new short[j + 1][];
				for (int k1 = 0; k1 <= j; k1++) {
					vertexSkin[k1] = new short[ai[k1]];
					ai[k1] = 0;
				}
				for (int j2 = 0; j2 < vertexCount; j2++) {
					int l2 = vertexVSkin[j2];
					vertexSkin[l2][ai[l2]++] = (short) j2;
				}
				vertexVSkin = null;
			}
			if (triangleTSkin != null) {
				int ai1[] = new int[256];
				int k = 0;
				for (int i1 = 0; i1 < triangleCount; i1++) {
					int l1 = triangleTSkin[i1];
					ai1[l1]++;
					if (l1 > k)
						k = l1;
				}
				triangleSkin = new short[k + 1][];
				for (int i2 = 0; i2 <= k; i2++) {
					triangleSkin[i2] = new short[ai1[i2]];
					ai1[i2] = 0;
				}
				for (int k2 = 0; k2 < triangleCount; k2++) {
					int i3 = triangleTSkin[k2];
					triangleSkin[i3][ai1[i3]++] = (short) k2;
				}
				triangleTSkin = null;
			}
		} catch (Exception e) {
		}
	}

	public boolean method470(int i) {
		if (vertexSkin == null)
			return false;
		if (i == -1)
			return false;
		Animation class36 = Animation.method531(i);
		if (class36 == null)
			return false;
		SkinList class18 = class36.aClass18_637;
		vertexXModifier = 0;
		vertexYModifier = 0;
		vertexZModifier = 0;
		for (int k = 0; k < class36.anInt638; k++) {
			int l = class36.anIntArray639[k];
			method472(class18.anIntArray342[l], class18.anIntArrayArray343[l], class36.anIntArray640[k], class36.anIntArray641[k],
					class36.anIntArray642[k]);
		}
		return true;
	}

	public boolean method471(int ai[], int j, int k) {
		if (k == -1)
			return false;
		if (ai == null || j == -1) {
			method470(k);
			return false;
		}
		Animation class36 = Animation.method531(k);
		if (class36 == null)
			return false;
		Animation class36_1 = Animation.method531(j);
		if (class36_1 == null) {
			method470(k);
			return false;
		}
		SkinList class18 = class36.aClass18_637;
		vertexXModifier = 0;
		vertexYModifier = 0;
		vertexZModifier = 0;
		int l = 0;
		int i1 = ai[l++];
		for (int j1 = 0; j1 < class36.anInt638; j1++) {
			int k1;
			for (k1 = class36.anIntArray639[j1]; k1 > i1; i1 = ai[l++])
				;
			if (k1 != i1 || class18.anIntArray342[k1] == 0)
				method472(class18.anIntArray342[k1], class18.anIntArrayArray343[k1], class36.anIntArray640[j1], class36.anIntArray641[j1],
						class36.anIntArray642[j1]);
		}
		vertexXModifier = 0;
		vertexYModifier = 0;
		vertexZModifier = 0;
		l = 0;
		i1 = ai[l++];
		for (int l1 = 0; l1 < class36_1.anInt638; l1++) {
			int i2;
			for (i2 = class36_1.anIntArray639[l1]; i2 > i1; i1 = ai[l++])
				;
			if (i2 == i1 || class18.anIntArray342[i2] == 0)
				method472(class18.anIntArray342[i2], class18.anIntArrayArray343[i2], class36_1.anIntArray640[l1],
						class36_1.anIntArray641[l1], class36_1.anIntArray642[l1]);
		}
		return true;
	}

	public void method472(int i, int ai[], int j, int k, int l) {
		int i1 = ai.length;
		if (i == 0) {
			int j1 = 0;
			vertexXModifier = 0;
			vertexYModifier = 0;
			vertexZModifier = 0;
			for (int k2 = 0; k2 < i1; k2++) {
				int l3 = ai[k2];
				if (l3 < vertexSkin.length) {
					short ai5[] = vertexSkin[l3];
					for (int i5 = 0; i5 < ai5.length; i5++) {
						int j6 = ai5[i5];
						vertexXModifier += vertexX[j6];
						vertexYModifier += vertexY[j6];
						vertexZModifier += vertexZ[j6];
						j1++;
					}
				}
			}
			if (j1 > 0) {
				vertexXModifier = vertexXModifier / j1 + j;
				vertexYModifier = vertexYModifier / j1 + k;
				vertexZModifier = vertexZModifier / j1 + l;
				return;
			} else {
				vertexXModifier = j;
				vertexYModifier = k;
				vertexZModifier = l;
				return;
			}
		}
		if (i == 1) {
			for (int k1 = 0; k1 < i1; k1++) {
				int l2 = ai[k1];
				if (l2 < vertexSkin.length) {
					short ai1[] = vertexSkin[l2];
					for (int i4 = 0; i4 < ai1.length; i4++) {
						int j5 = ai1[i4];
						vertexX[j5] += j;
						vertexY[j5] += k;
						vertexZ[j5] += l;
					}
				}
			}
			return;
		}
		if (i == 2) {
			for (int l1 = 0; l1 < i1; l1++) {
				int i3 = ai[l1];
				if (i3 < vertexSkin.length) {
					short ai2[] = vertexSkin[i3];
					for (int j4 = 0; j4 < ai2.length; j4++) {
						int k5 = ai2[j4];
						vertexX[k5] -= vertexXModifier;
						vertexY[k5] -= vertexYModifier;
						vertexZ[k5] -= vertexZModifier;
						int k6 = (j & 0xff) * 8;
						int l6 = (k & 0xff) * 8;
						int i7 = (l & 0xff) * 8;
						if (i7 != 0) {
							int j7 = SINE[i7];
							int i8 = COSINE[i7];
							int l8 = vertexY[k5] * j7 + vertexX[k5] * i8 >> 16;
							vertexY[k5] = (short) (vertexY[k5] * i8 - vertexX[k5] * j7 >> 16);
							vertexX[k5] = (short) l8;
						}
						if (k6 != 0) {
							int k7 = SINE[k6];
							int j8 = COSINE[k6];
							int i9 = vertexY[k5] * j8 - vertexZ[k5] * k7 >> 16;
							vertexZ[k5] = (short) (vertexY[k5] * k7 + vertexZ[k5] * j8 >> 16);
							vertexY[k5] = (short) i9;
						}
						if (l6 != 0) {
							int l7 = SINE[l6];
							int k8 = COSINE[l6];
							int j9 = vertexZ[k5] * l7 + vertexX[k5] * k8 >> 16;
							vertexZ[k5] = (short) (vertexZ[k5] * k8 - vertexX[k5] * l7 >> 16);
							vertexX[k5] = (short) j9;
						}
						vertexX[k5] += vertexXModifier;
						vertexY[k5] += vertexYModifier;
						vertexZ[k5] += vertexZModifier;
					}
				}
			}
			return;
		}
		if (i == 3) {
			for (int i2 = 0; i2 < i1; i2++) {
				int j3 = ai[i2];
				if (j3 < vertexSkin.length) {
					short ai3[] = vertexSkin[j3];
					for (int k4 = 0; k4 < ai3.length; k4++) {
						int l5 = ai3[k4];
						vertexX[l5] -= vertexXModifier;
						vertexY[l5] -= vertexYModifier;
						vertexZ[l5] -= vertexZModifier;
						vertexX[l5] = (short) ((vertexX[l5] * j) / 128);
						vertexY[l5] = (short) ((vertexY[l5] * k) / 128);
						vertexZ[l5] = (short) ((vertexZ[l5] * l) / 128);
						vertexX[l5] += vertexXModifier;
						vertexY[l5] += vertexYModifier;
						vertexZ[l5] += vertexZModifier;
					}
				}
			}
			return;
		}
		if (i == 5 && triangleSkin != null && triangleAlpha != null) {
			for (int j2 = 0; j2 < i1; j2++) {
				int k3 = ai[j2];
				if (k3 < triangleSkin.length) {
					short ai4[] = triangleSkin[k3];
					for (int l4 = 0; l4 < ai4.length; l4++) {
						int i6 = ai4[l4];
						triangleAlpha[i6] += j * 8;
						if (triangleAlpha[i6] < 0)
							triangleAlpha[i6] = 0;
						if (triangleAlpha[i6] > 255)
							triangleAlpha[i6] = 255;
					}
				}
			}
		}
	}

	public void method473() {
		for (int j = 0; j < vertexCount; j++) {
			int k = vertexX[j];
			vertexX[j] = vertexZ[j];
			vertexZ[j] = (short) -k;
		}
	}

	public void method474(int i) {
		int k = SINE[i];
		int l = COSINE[i];
		for (int i1 = 0; i1 < vertexCount; i1++) {
			int j1 = vertexY[i1] * l - vertexZ[i1] * k >> 16;
			vertexZ[i1] = (short) (vertexY[i1] * k + vertexZ[i1] * l >> 16);
			vertexY[i1] = (short) j1;
		}
	}

	public void method475(int i, int j, int l) {
		for (int i1 = 0; i1 < vertexCount; i1++) {
			vertexX[i1] += i;
			vertexY[i1] += j;
			vertexZ[i1] += l;
		}
	}

	public void method476(int i, int j) {
		for (int k = 0; k < triangleCount; k++)
			if (triangleColourOrTexture[k] == i)
				triangleColourOrTexture[k] = j;
	}

	public void method477() {
		for (int j = 0; j < vertexCount; j++)
			vertexZ[j] = (short) -vertexZ[j];
		for (int k = 0; k < triangleCount; k++) {
			int l = triangleA[k];
			triangleA[k] = triangleC[k];
			triangleC[k] = l;
		}
	}

	public void resize(int i, int j, int l) {
		for (int i1 = 0; i1 < vertexCount; i1++) {
			vertexX[i1] = (short) ((vertexX[i1] * i) / 128);
			vertexY[i1] = (short) ((vertexY[i1] * l) / 128);
			vertexZ[i1] = (short) ((vertexZ[i1] * j) / 128);
		}
	}

	public void light(int lightMod, int magMultiplyer, int l_x, int l_y, int l_z, boolean flatShading) {
		int _mag_pre = (int) Math.sqrt(l_x * l_x + l_y * l_y + l_z * l_z);
		int mag = magMultiplyer * _mag_pre >> 8;
		if (triangleHslA == null) {
			triangleHslA = new int[triangleCount];
			triangleHslB = new int[triangleCount];
			triangleHslC = new int[triangleCount];
		}
		if (super.vertexNormals == null) {
			super.vertexNormals = new VertexNormal[vertexCount];
			for (int l1 = 0; l1 < vertexCount; l1++)
				super.vertexNormals[l1] = new VertexNormal();
		}
		for (int triID = 0; triID < triangleCount; triID++) {// todo - rename
																// this to
																// camelcode in
																// future (peter
																// plz do this,
																// looks fucking
																// complicated
																// >:)
			/*
			 * if (triangleColourOrTexture != null || triangleAlpha != null) if
			 * (triangleColourOrTexture[triID] == 65535 ||
			 * triangleColourOrTexture[triID] == 16705) { triangleAlpha[triID] =
			 * 255; }
			 */
			int t_a = triangleA[triID];
			int t_b = triangleB[triID];
			int t_c = triangleC[triID];
			int d_a_b_x = vertexX[t_b] - vertexX[t_a];
			int d_a_b_y = vertexY[t_b] - vertexY[t_a];
			int d_a_b_z = vertexZ[t_b] - vertexZ[t_a];
			int d_c_a_x = vertexX[t_c] - vertexX[t_a];
			int d_c_a_y = vertexY[t_c] - vertexY[t_a];
			int d_c_a_z = vertexZ[t_c] - vertexZ[t_a];
			int normalX = d_a_b_y * d_c_a_z - d_c_a_y * d_a_b_z;
			int normalY = d_a_b_z * d_c_a_x - d_c_a_z * d_a_b_x;
			int normalZ;
			for (normalZ = d_a_b_x * d_c_a_y - d_c_a_x * d_a_b_y; normalX > 8192 || normalY > 8192 || normalZ > 8192 || normalX < -8192
					|| normalY < -8192 || normalZ < -8192; normalZ >>= 1) {
				normalX >>= 1;
				normalY >>= 1;
			}
			int normal_length = (int) Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
			if (normal_length <= 0)
				normal_length = 1;
			normalX = (normalX * 256) / normal_length;// Normalization
			normalY = (normalY * 256) / normal_length;
			normalZ = (normalZ * 256) / normal_length;
			if (triangleDrawType == null || (triangleDrawType[triID] & 1) == 0) {
				VertexNormal vertexNormal_2 = super.vertexNormals[t_a];
				vertexNormal_2.x += normalX;
				vertexNormal_2.y += normalY;
				vertexNormal_2.z += normalZ;
				vertexNormal_2.magnitude++;
				vertexNormal_2 = super.vertexNormals[t_b];
				vertexNormal_2.x += normalX;
				vertexNormal_2.y += normalY;
				vertexNormal_2.z += normalZ;
				vertexNormal_2.magnitude++;
				vertexNormal_2 = super.vertexNormals[t_c];
				vertexNormal_2.x += normalX;
				vertexNormal_2.y += normalY;
				vertexNormal_2.z += normalZ;
				vertexNormal_2.magnitude++;
			} else {
				int lightness = lightMod + (l_x * normalX + l_y * normalY + l_z * normalZ) / (mag + mag / 2);
				triangleHslA[triID] = mixLightness(triangleColourOrTexture[triID], lightness, triangleDrawType[triID]);
			}
		}
		// todo - this can be condensed - DONE
		if (flatShading) {
			doShading(lightMod, mag, l_x, l_y, l_z);
			calculateDiagonals();
		} else {
			vertexNormalOffset = new VertexNormal[vertexCount];
			for (int vertexPointer = 0; vertexPointer < vertexCount; vertexPointer++) {
				VertexNormal vertexNormal = super.vertexNormals[vertexPointer];
				VertexNormal vertexNormal_1 = vertexNormalOffset[vertexPointer] = new VertexNormal();
				vertexNormal_1.x = vertexNormal.x;
				vertexNormal_1.y = vertexNormal.y;
				vertexNormal_1.z = vertexNormal.z;
				vertexNormal_1.magnitude = vertexNormal.magnitude;
			}
			calculateDiagonalsAndStats();
		}
	}

	public void calculateDiagonals() {
		super.modelHeight = 0;
		diagonal2DAboveorigin = 0;
		maxY = 0;
		for (int verticePointer = 0; verticePointer < vertexCount; verticePointer++) {
			int v_x = vertexX[verticePointer];
			int v_y = vertexY[verticePointer];
			int v_z = vertexZ[verticePointer];
			if (-v_y > super.modelHeight)
				super.modelHeight = -v_y;
			if (v_y > maxY)
				maxY = v_y;
			int bounds_diagonal = v_x * v_x + v_z * v_z;
			if (bounds_diagonal > diagonal2DAboveorigin)
				diagonal2DAboveorigin = bounds_diagonal;
		}
		diagonal2DAboveorigin = (int) (Math.sqrt(diagonal2DAboveorigin) + 0.98999999999999999D);
		diagonal3DAboveorigin = (int) (Math.sqrt(diagonal2DAboveorigin * diagonal2DAboveorigin + super.modelHeight * super.modelHeight) + 0.98999999999999999D);
		diagonal3D = diagonal3DAboveorigin
				+ (int) (Math.sqrt(diagonal2DAboveorigin * diagonal2DAboveorigin + maxY * maxY) + 0.98999999999999999D);
	}

	public void normalise() {// normalise?
		super.modelHeight = 0;
		maxY = 0;
		for (int i = 0; i < vertexCount; i++) {
			int j = vertexY[i];
			if (-j > super.modelHeight)
				super.modelHeight = -j;
			if (j > maxY)
				maxY = j;
		}
		diagonal3DAboveorigin = (int) (Math.sqrt(diagonal2DAboveorigin * diagonal2DAboveorigin + super.modelHeight * super.modelHeight) + 0.98999999999999999D);
		diagonal3D = diagonal3DAboveorigin
				+ (int) (Math.sqrt(diagonal2DAboveorigin * diagonal2DAboveorigin + maxY * maxY) + 0.98999999999999999D);
	}

	private void calculateDiagonalsAndStats() {
		super.modelHeight = 0;
		diagonal2DAboveorigin = 0;
		maxY = 0;
		minX = 0xf423f;// todo - change to int - 999999
		maxX = 0xfff0bdc1;// 4293967297
		maxZ = 0xfffe7961;// 4294867297
		minZ = 0x1869f;// 99999
		for (int j = 0; j < vertexCount; j++) {
			int v_x = vertexX[j];
			int v_y = vertexY[j];
			int v_z = vertexZ[j];
			if (v_x < minX)
				minX = v_x;
			if (v_x > maxX)
				maxX = v_x;
			if (v_z < minZ)
				minZ = v_z;
			if (v_z > maxZ)
				maxZ = v_z;
			if (-v_y > super.modelHeight)
				super.modelHeight = -v_y;
			if (v_y > maxY)
				maxY = v_y;
			int _diagonal_2D_aboveorigin = v_x * v_x + v_z * v_z;
			if (_diagonal_2D_aboveorigin > diagonal2DAboveorigin)
				diagonal2DAboveorigin = _diagonal_2D_aboveorigin;
		}
		diagonal2DAboveorigin = (int) Math.sqrt(diagonal2DAboveorigin);
		diagonal3DAboveorigin = (int) Math.sqrt(diagonal2DAboveorigin * diagonal2DAboveorigin + super.modelHeight * super.modelHeight);
		diagonal3D = diagonal3DAboveorigin + (int) Math.sqrt(diagonal2DAboveorigin * diagonal2DAboveorigin + maxY * maxY);
	}

	public void doShading(int intensity, int falloff, int l_x, int l_y, int l_z) {
		for (int triID = 0; triID < triangleCount; triID++) {
			int triA = triangleA[triID];
			int triB = triangleB[triID];
			int triC = triangleC[triID];
			if (triangleDrawType == null) {
				int t_hsl = triangleColourOrTexture[triID];
				VertexNormal vertexNormal = super.vertexNormals[triA];
				int l = intensity + (l_x * vertexNormal.x + l_y * vertexNormal.y + l_z * vertexNormal.z)
						/ (falloff * vertexNormal.magnitude);
				triangleHslA[triID] = mixLightness(t_hsl, l, 0);
				vertexNormal = super.vertexNormals[triB];
				l = intensity + (l_x * vertexNormal.x + l_y * vertexNormal.y + l_z * vertexNormal.z) / (falloff * vertexNormal.magnitude);
				triangleHslB[triID] = mixLightness(t_hsl, l, 0);
				vertexNormal = super.vertexNormals[triC];
				l = intensity + (l_x * vertexNormal.x + l_y * vertexNormal.y + l_z * vertexNormal.z) / (falloff * vertexNormal.magnitude);
				triangleHslC[triID] = mixLightness(t_hsl, l, 0);
			} else if ((triangleDrawType[triID] & 1) == 0) {
				// Bit 1 of triangle_draw_type ON means mix_lightness returns
				// just lightness
				// instead of mixed hsl
				int t_hsl = triangleColourOrTexture[triID];
				int t_flags = triangleDrawType[triID];
				VertexNormal vertexNormal_1 = super.vertexNormals[triA];
				int l = intensity + (l_x * vertexNormal_1.x + l_y * vertexNormal_1.y + l_z * vertexNormal_1.z)
						/ (falloff * vertexNormal_1.magnitude);
				triangleHslA[triID] = mixLightness(t_hsl, l, t_flags);
				vertexNormal_1 = super.vertexNormals[triB];
				l = intensity + (l_x * vertexNormal_1.x + l_y * vertexNormal_1.y + l_z * vertexNormal_1.z)
						/ (falloff * vertexNormal_1.magnitude);
				triangleHslB[triID] = mixLightness(t_hsl, l, t_flags);
				vertexNormal_1 = super.vertexNormals[triC];
				l = intensity + (l_x * vertexNormal_1.x + l_y * vertexNormal_1.y + l_z * vertexNormal_1.z)
						/ (falloff * vertexNormal_1.magnitude);
				triangleHslC[triID] = mixLightness(t_hsl, l, t_flags);
			}
		}
		super.vertexNormals = null;
		vertexNormalOffset = null;
		vertexVSkin = null;
		triangleTSkin = null;
		if (triangleDrawType != null) {
			for (int l1 = 0; l1 < triangleCount; l1++)
				if ((triangleDrawType[l1] & 2) == 2)
					return;
		}
		// triangleColourOrTexture = null;
	}

	private static int mixLightness(int hsl, int l, int flags) {
		if ((flags & 2) == 2) {
			if (l < 0)
				l = 0;
			else if (l > 127)
				l = 127;
			l = 127 - l;
			return l;
		}
		l = l * (hsl & 0x7f) >> 7;
		if (l < 2)
			l = 2;
		else if (l > 126)
			l = 126;
		return (hsl & 0xff80) + l;
	}

	public static final int method481(int i, int j, int k) {
		if (i == 65535)
			return 0;
		if ((k & 2) == 2) {
			if (j < 0)
				j = 0;
			else if (j > 127)
				j = 127;
			j = 127 - j;
			return j;
		}
		j = j * (i & 0x7f) >> 7;
		if (j < 2)
			j = 2;
		else if (j > 126)
			j = 126;
		return (i & 0xff80) + j;
	}

	public final void method482(int j, int k, int l, int i1, int j1, int k1, boolean chatHeadFix) {
		int i = 0;
		int l1 = Rasterizer.textureInt1;
		int i2 = Rasterizer.textureInt2;
		int j2 = SINE[i];
		int k2 = COSINE[i];
		int l2 = SINE[j];
		int i3 = COSINE[j];
		int j3 = SINE[k];
		int k3 = COSINE[k];
		int l3 = SINE[l];
		int i4 = COSINE[l];
		int j4 = j1 * l3 + k1 * i4 >> 16;
		for (int k4 = 0; k4 < vertexCount; k4++) {
			int l4 = vertexX[k4];
			int i5 = vertexY[k4];
			int j5 = vertexZ[k4];
			if (k != 0) {
				int k5 = i5 * j3 + l4 * k3 >> 16;
				i5 = i5 * k3 - l4 * j3 >> 16;
				l4 = k5;
			}
			if (i != 0) {
				int l5 = i5 * k2 - j5 * j2 >> 16;
				j5 = i5 * j2 + j5 * k2 >> 16;
				i5 = l5;
			}
			if (j != 0) {
				int i6 = j5 * l2 + l4 * i3 >> 16;
				j5 = j5 * i3 - l4 * l2 >> 16;
				l4 = i6;
			}
			l4 += i1;
			i5 += j1;
			j5 += k1;
			int j6 = i5 * i4 - j5 * l3 >> 16;
			j5 = i5 * l3 + j5 * i4 >> 16;
			i5 = j6;
			depthBuffer[k4] = (short) (j5 - j4);
			vertexSX[k4] = (short) (l1 + (l4 << 9) / j5);
			vertexSY[k4] = (short) (i2 + (i5 << 9) / j5);
			if (textureTriangleCount > 0) {
				vertexMvX[k4] = (short) l4;
				vertexMvY[k4] = (short) i5;
				vertexMvZ[k4] = (short) j5;
			}
		}
		try {
			method483(false, false, 0, chatHeadFix);
			return;
		} catch (Exception _ex) {
			return;
		}
	}

	public final void method443(int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2) {
		int j2 = l1 * i1 - j1 * l >> 16;
		int k2 = k1 * j + j2 * k >> 16;
		int l2 = diagonal2DAboveorigin * k >> 16;
		int i3 = k2 + l2;
		if (i3 <= 50 || k2 >= 3500)
			return;
		int j3 = l1 * l + j1 * i1 >> 16;
		int k3 = j3 - diagonal2DAboveorigin << 9;
		if (k3 / i3 >= RSRaster.centerY)
			return;
		int l3 = j3 + diagonal2DAboveorigin << 9;
		if (l3 / i3 <= -RSRaster.centerY)
			return;
		int i4 = k1 * k - j2 * j >> 16;
		int j4 = diagonal2DAboveorigin * j >> 16;
		int k4 = i4 + j4 << 9;
		if (k4 / i3 <= -RSRaster.anInt1387)
			return;
		int l4 = j4 + (super.modelHeight * k >> 16);
		int i5 = i4 - l4 << 9;
		if (i5 / i3 >= RSRaster.anInt1387)
			return;
		int j5 = l2 + (super.modelHeight * j >> 16);
		boolean flag = false;
		if (k2 - j5 <= 50)
			flag = true;
		boolean flag1 = false;
		if (i2 > 0 && aBoolean1684) {
			int k5 = k2 - l2;
			if (k5 <= 50)
				k5 = 50;
			if (j3 > 0) {
				k3 /= i3;
				l3 /= k5;
			} else {
				l3 /= i3;
				k3 /= k5;
			}
			if (i4 > 0) {
				i5 /= i3;
				k4 /= k5;
			} else {
				k4 /= i3;
				i5 /= k5;
			}
			int i6 = cursorXPos - Rasterizer.textureInt1;
			int k6 = cursorYPos - Rasterizer.textureInt2;
			if (i6 > k3 && i6 < l3 && k6 > i5 && k6 < k4)
				if (oneSquareModel)
					resourceIDTAG[resourceCount++] = i2;
				else
					flag1 = true;
		}
		int l5 = Rasterizer.textureInt1;
		int j6 = Rasterizer.textureInt2;
		int l6 = 0;
		int i7 = 0;
		if (i != 0) {
			l6 = SINE[i];
			i7 = COSINE[i];
		}
		for (int j7 = 0; j7 < vertexCount; j7++) {
			int k7 = vertexX[j7];
			int l7 = vertexY[j7];
			int i8 = vertexZ[j7];
			if (i != 0) {
				int j8 = i8 * l6 + k7 * i7 >> 16;
				i8 = i8 * i7 - k7 * l6 >> 16;
				k7 = j8;
			}
			k7 += j1;
			l7 += k1;
			i8 += l1;
			int k8 = i8 * l + k7 * i1 >> 16;
			i8 = i8 * i1 - k7 * l >> 16;
			k7 = k8;
			k8 = l7 * k - i8 * j >> 16;
			i8 = l7 * j + i8 * k >> 16;
			l7 = k8;
			depthBuffer[j7] = (short) (i8 - k2);
			if (i8 >= 50) {
				vertexSX[j7] = (short) (l5 + (k7 << 9) / i8);
				vertexSY[j7] = (short) (j6 + (l7 << 9) / i8);
			} else {
				vertexSX[j7] = -5000;
				flag = true;
			}
			if (flag || textureTriangleCount > 0) {
				vertexMvX[j7] = (short) k7;
				vertexMvY[j7] = (short) l7;
				vertexMvZ[j7] = (short) i8;
			}
		}
		try {
			method483(flag, flag1, i2, false);
			return;
		} catch (Exception _ex) {
			return;
		}
	}

	public final void method483(boolean flag, boolean flag1, int i, boolean chatHeadFix) {
		for (int j = 0; j < diagonal3D; j++)
			depthListIndices[j] = 0;
		for (int k = 0; k < triangleCount; k++)
			if (triangleDrawType == null || triangleDrawType[k] != -1) {
				int l = triangleA[k];
				int k1 = triangleB[k];
				int j2 = triangleC[k];
				int i3 = vertexSX[l];
				int l3 = vertexSX[k1];
				int k4 = vertexSX[j2];
				if (flag && (i3 == -5000 || l3 == -5000 || k4 == -5000)) {
					aBooleanArray1664[k] = true;
					int j5 = (depthBuffer[l] + depthBuffer[k1] + depthBuffer[j2]) / 3 + diagonal3DAboveorigin;
					faceLists[j5][depthListIndices[j5]++] = (short) k;
				} else {
					if (flag1 && method486(cursorXPos, cursorYPos, vertexSY[l], vertexSY[k1], vertexSY[j2], i3, l3, k4)) {
						resourceIDTAG[resourceCount++] = i;
						flag1 = false;
					}
					if ((i3 - l3) * (vertexSY[j2] - vertexSY[k1]) - (vertexSY[l] - vertexSY[k1]) * (k4 - l3) > 0) {
						aBooleanArray1664[k] = false;
						if (i3 < 0 || l3 < 0 || k4 < 0 || i3 > RSRaster.centerX || l3 > RSRaster.centerX || k4 > RSRaster.centerX)
							aBooleanArray1663[k] = true;
						else
							aBooleanArray1663[k] = false;
						int k5 = (depthBuffer[l] + depthBuffer[k1] + depthBuffer[j2]) / 3 + diagonal3DAboveorigin;
						faceLists[k5][depthListIndices[k5]++] = (short) k;
					}
				}
			}
		if (facePriority == null) {
			for (int i1 = diagonal3D - 1; i1 >= 0; i1--) {
				int l1 = depthListIndices[i1];
				if (l1 > 0) {
					short[] ai = faceLists[i1];
					for (int j3 = 0; j3 < l1; j3++)
						method484(ai[j3], chatHeadFix);
				}
			}
			return;
		}
		for (int j1 = 0; j1 < 12; j1++) {
			anIntArray1673[j1] = 0;
			anIntArray1677[j1] = 0;
		}
		for (int i2 = diagonal3D - 1; i2 >= 0; i2--) {
			int k2 = depthListIndices[i2];
			if (k2 > 0) {
				short[] ai1 = faceLists[i2];
				for (int i4 = 0; i4 < k2; i4++) {
					int l4 = ai1[i4];
					int l5 = facePriority[l4];
					int j6 = anIntArray1673[l5]++;
					anIntArrayArray1674[l5][j6] = (short) l4;
					if (l5 < 10)
						anIntArray1677[l5] += i2;
					else if (l5 == 10)
						anIntArray1675[j6] = (short) i2;
					else
						anIntArray1676[j6] = (short) i2;
				}
			}
		}
		int l2 = 0;
		if (anIntArray1673[1] > 0 || anIntArray1673[2] > 0)
			l2 = (anIntArray1677[1] + anIntArray1677[2]) / (anIntArray1673[1] + anIntArray1673[2]);
		int k3 = 0;
		if (anIntArray1673[3] > 0 || anIntArray1673[4] > 0)
			k3 = (anIntArray1677[3] + anIntArray1677[4]) / (anIntArray1673[3] + anIntArray1673[4]);
		int j4 = 0;
		if (anIntArray1673[6] > 0 || anIntArray1673[8] > 0)
			j4 = (anIntArray1677[6] + anIntArray1677[8]) / (anIntArray1673[6] + anIntArray1673[8]);
		int i6 = 0;
		int k6 = anIntArray1673[10];
		short[] ai2 = anIntArrayArray1674[10];
		short[] ai3 = anIntArray1675;
		if (i6 == k6) {
			i6 = 0;
			k6 = anIntArray1673[11];
			ai2 = anIntArrayArray1674[11];
			ai3 = anIntArray1676;
		}
		int i5;
		if (i6 < k6)
			i5 = ai3[i6];
		else
			i5 = -1000;
		for (int l6 = 0; l6 < 10; l6++) {
			while (l6 == 0 && i5 > l2) {
				method484(ai2[i6++], chatHeadFix);
				if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = anIntArray1673[11];
					ai2 = anIntArrayArray1674[11];
					ai3 = anIntArray1676;
				}
				if (i6 < k6)
					i5 = ai3[i6];
				else
					i5 = -1000;
			}
			while (l6 == 3 && i5 > k3) {
				method484(ai2[i6++], chatHeadFix);
				if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = anIntArray1673[11];
					ai2 = anIntArrayArray1674[11];
					ai3 = anIntArray1676;
				}
				if (i6 < k6)
					i5 = ai3[i6];
				else
					i5 = -1000;
			}
			while (l6 == 5 && i5 > j4) {
				method484(ai2[i6++], chatHeadFix);
				if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = anIntArray1673[11];
					ai2 = anIntArrayArray1674[11];
					ai3 = anIntArray1676;
				}
				if (i6 < k6)
					i5 = ai3[i6];
				else
					i5 = -1000;
			}
			int i7 = anIntArray1673[l6];
			short[] ai4 = anIntArrayArray1674[l6];
			for (int j7 = 0; j7 < i7; j7++)
				method484(ai4[j7], chatHeadFix);
		}
		while (i5 != -1000) {
			method484(ai2[i6++], chatHeadFix);
			if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
				i6 = 0;
				ai2 = anIntArrayArray1674[11];
				k6 = anIntArray1673[11];
				ai3 = anIntArray1676;
			}
			if (i6 < k6)
				i5 = ai3[i6];
			else
				i5 = -1000;
		}
	}

	public final void method484(int i, boolean chatHeadFix) {
		if (aBooleanArray1664[i]) {
			method485(i);
			return;
		}
		int j = triangleA[i];
		int k = triangleB[i];
		int l = triangleC[i];
		Rasterizer.restrict_edges = aBooleanArray1663[i];
		if (triangleAlpha == null)
			Rasterizer.alpha = 0;
		else
			Rasterizer.alpha = triangleAlpha[i];
		int i1;
		if (triangleDrawType == null)
			i1 = 0;
		else
			i1 = triangleDrawType[i] & 3;
		if (i1 == 0) {
			Rasterizer.method374(vertexSY[j], vertexSY[k], vertexSY[l], vertexSX[j], vertexSX[k], vertexSX[l], triangleHslA[i],
					triangleHslB[i], triangleHslC[i], chatHeadFix);
			return;
		}
		if (i1 == 1) {
			Rasterizer.method376(vertexSY[j], vertexSY[k], vertexSY[l], vertexSX[j], vertexSX[k], vertexSX[l], HSL2RGB[triangleHslA[i]]);
			return;
		}
		if (i1 == 2) {
			int j1 = triangleDrawType[i] >> 2;
			int l1 = triPIndex[j1];
			int j2 = triMIndex[j1];
			int l2 = triNIndex[j1];
			Rasterizer.method378(vertexSY[j], vertexSY[k], vertexSY[l], vertexSX[j], vertexSX[k], vertexSX[l], triangleHslA[i],
					triangleHslB[i], triangleHslC[i], vertexMvX[l1], vertexMvX[j2], vertexMvX[l2], vertexMvY[l1], vertexMvY[j2],
					vertexMvY[l2], vertexMvZ[l1], vertexMvZ[j2], vertexMvZ[l2], triangleColourOrTexture[i]);
			return;
		}
		if (i1 == 3) {
			int k1 = triangleDrawType[i] >> 2;
			int i2 = triPIndex[k1];
			int k2 = triMIndex[k1];
			int i3 = triNIndex[k1];
			Rasterizer.method378(vertexSY[j], vertexSY[k], vertexSY[l], vertexSX[j], vertexSX[k], vertexSX[l], triangleHslA[i],
					triangleHslA[i], triangleHslA[i], vertexMvX[i2], vertexMvX[k2], vertexMvX[i3], vertexMvY[i2], vertexMvY[k2],
					vertexMvY[i3], vertexMvZ[i2], vertexMvZ[k2], vertexMvZ[i3], triangleColourOrTexture[i]);
		}
	}

	public final void method485(int i) {
		if (triangleColourOrTexture != null)
			if (triangleColourOrTexture[i] == 65535)
				return;
		int j = Rasterizer.textureInt1;
		int k = Rasterizer.textureInt2;
		int l = 0;
		int i1 = triangleA[i];
		int j1 = triangleB[i];
		int k1 = triangleC[i];
		int l1 = vertexMvZ[i1];
		int i2 = vertexMvZ[j1];
		int j2 = vertexMvZ[k1];
		if (l1 >= 50) {
			anIntArray1678[l] = vertexSX[i1];
			anIntArray1679[l] = vertexSY[i1];
			anIntArray1680[l++] = triangleHslA[i];
		} else {
			int k2 = vertexMvX[i1];
			int k3 = vertexMvY[i1];
			int k4 = triangleHslA[i];
			if (j2 >= 50) {
				int k5 = (50 - l1) * modelIntArray4[j2 - l1];
				anIntArray1678[l] = j + (k2 + ((vertexMvX[k1] - k2) * k5 >> 16) << 9) / 50;
				anIntArray1679[l] = k + (k3 + ((vertexMvY[k1] - k3) * k5 >> 16) << 9) / 50;
				anIntArray1680[l++] = k4 + ((triangleHslC[i] - k4) * k5 >> 16);
			}
			if (i2 >= 50) {
				int l5 = (50 - l1) * modelIntArray4[i2 - l1];
				anIntArray1678[l] = j + (k2 + ((vertexMvX[j1] - k2) * l5 >> 16) << 9) / 50;
				anIntArray1679[l] = k + (k3 + ((vertexMvY[j1] - k3) * l5 >> 16) << 9) / 50;
				anIntArray1680[l++] = k4 + ((triangleHslB[i] - k4) * l5 >> 16);
			}
		}
		if (i2 >= 50) {
			anIntArray1678[l] = vertexSX[j1];
			anIntArray1679[l] = vertexSY[j1];
			anIntArray1680[l++] = triangleHslB[i];
		} else {
			int l2 = vertexMvX[j1];
			int l3 = vertexMvY[j1];
			int l4 = triangleHslB[i];
			if (l1 >= 50) {
				int i6 = (50 - i2) * modelIntArray4[l1 - i2];
				anIntArray1678[l] = j + (l2 + ((vertexMvX[i1] - l2) * i6 >> 16) << 9) / 50;
				anIntArray1679[l] = k + (l3 + ((vertexMvY[i1] - l3) * i6 >> 16) << 9) / 50;
				anIntArray1680[l++] = l4 + ((triangleHslA[i] - l4) * i6 >> 16);
			}
			if (j2 >= 50) {
				int j6 = (50 - i2) * modelIntArray4[j2 - i2];
				anIntArray1678[l] = j + (l2 + ((vertexMvX[k1] - l2) * j6 >> 16) << 9) / 50;
				anIntArray1679[l] = k + (l3 + ((vertexMvY[k1] - l3) * j6 >> 16) << 9) / 50;
				anIntArray1680[l++] = l4 + ((triangleHslC[i] - l4) * j6 >> 16);
			}
		}
		if (j2 >= 50) {
			anIntArray1678[l] = vertexSX[k1];
			anIntArray1679[l] = vertexSY[k1];
			anIntArray1680[l++] = triangleHslC[i];
		} else {
			int i3 = vertexMvX[k1];
			int i4 = vertexMvY[k1];
			int i5 = triangleHslC[i];
			if (i2 >= 50) {
				int k6 = (50 - j2) * modelIntArray4[i2 - j2];
				anIntArray1678[l] = j + (i3 + ((vertexMvX[j1] - i3) * k6 >> 16) << 9) / 50;
				anIntArray1679[l] = k + (i4 + ((vertexMvY[j1] - i4) * k6 >> 16) << 9) / 50;
				anIntArray1680[l++] = i5 + ((triangleHslB[i] - i5) * k6 >> 16);
			}
			if (l1 >= 50) {
				int l6 = (50 - j2) * modelIntArray4[l1 - j2];
				anIntArray1678[l] = j + (i3 + ((vertexMvX[i1] - i3) * l6 >> 16) << 9) / 50;
				anIntArray1679[l] = k + (i4 + ((vertexMvY[i1] - i4) * l6 >> 16) << 9) / 50;
				anIntArray1680[l++] = i5 + ((triangleHslA[i] - i5) * l6 >> 16);
			}
		}
		int j3 = anIntArray1678[0];
		int j4 = anIntArray1678[1];
		int j5 = anIntArray1678[2];
		int i7 = anIntArray1679[0];
		int j7 = anIntArray1679[1];
		int k7 = anIntArray1679[2];
		if ((j3 - j4) * (k7 - j7) - (i7 - j7) * (j5 - j4) > 0) {
			Rasterizer.restrict_edges = false;
			if (l == 3) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > RSRaster.centerX || j4 > RSRaster.centerX || j5 > RSRaster.centerX)
					Rasterizer.restrict_edges = true;
				int l7;
				if (triangleDrawType == null)
					l7 = 0;
				else
					l7 = triangleDrawType[i] & 3;
				if (l7 == 0)
					Rasterizer.method374(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2], false);
				else if (l7 == 1)
					Rasterizer.method376(i7, j7, k7, j3, j4, j5, HSL2RGB[triangleHslA[i]]);
				else if (l7 == 2) {
					int j8 = triangleDrawType[i] >> 2;
					int k9 = triPIndex[j8];
					int k10 = triMIndex[j8];
					int k11 = triNIndex[j8];
					Rasterizer.method378(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2], vertexMvX[k9],
							vertexMvX[k10], vertexMvX[k11], vertexMvY[k9], vertexMvY[k10], vertexMvY[k11], vertexMvZ[k9], vertexMvZ[k10],
							vertexMvZ[k11], triangleColourOrTexture[i]);
				} else if (l7 == 3) {
					int k8 = triangleDrawType[i] >> 2;
					int l9 = triPIndex[k8];
					int l10 = triMIndex[k8];
					int l11 = triNIndex[k8];
					Rasterizer.method378(i7, j7, k7, j3, j4, j5, triangleHslA[i], triangleHslA[i], triangleHslA[i], vertexMvX[l9],
							vertexMvX[l10], vertexMvX[l11], vertexMvY[l9], vertexMvY[l10], vertexMvY[l11], vertexMvZ[l9], vertexMvZ[l10],
							vertexMvZ[l11], triangleColourOrTexture[i]);
				}
			}
			if (l == 4) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > RSRaster.centerX || j4 > RSRaster.centerX || j5 > RSRaster.centerX
						|| anIntArray1678[3] < 0 || anIntArray1678[3] > RSRaster.centerX)
					Rasterizer.restrict_edges = true;
				int i8;
				if (triangleDrawType == null)
					i8 = 0;
				else
					i8 = triangleDrawType[i] & 3;
				if (i8 == 0) {
					Rasterizer.method374(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2], false);
					Rasterizer.method374(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], anIntArray1680[0], anIntArray1680[2],
							anIntArray1680[3], false);
					return;
				}
				if (i8 == 1) {
					int l8 = HSL2RGB[triangleHslA[i]];
					Rasterizer.method376(i7, j7, k7, j3, j4, j5, l8);
					Rasterizer.method376(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], l8);
					return;
				}
				if (i8 == 2) {
					int i9 = triangleDrawType[i] >> 2;
					int i10 = triPIndex[i9];
					int i11 = triMIndex[i9];
					int i12 = triNIndex[i9];
					Rasterizer.method378(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2], vertexMvX[i10],
							vertexMvX[i11], vertexMvX[i12], vertexMvY[i10], vertexMvY[i11], vertexMvY[i12], vertexMvZ[i10], vertexMvZ[i11],
							vertexMvZ[i12], triangleColourOrTexture[i]);
					Rasterizer.method378(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], anIntArray1680[0], anIntArray1680[2],
							anIntArray1680[3], vertexMvX[i10], vertexMvX[i11], vertexMvX[i12], vertexMvY[i10], vertexMvY[i11],
							vertexMvY[i12], vertexMvZ[i10], vertexMvZ[i11], vertexMvZ[i12], triangleColourOrTexture[i]);
					return;
				}
				if (i8 == 3) {
					int j9 = triangleDrawType[i] >> 2;
					int j10 = triPIndex[j9];
					int j11 = triMIndex[j9];
					int j12 = triNIndex[j9];
					Rasterizer.method378(i7, j7, k7, j3, j4, j5, triangleHslA[i], triangleHslA[i], triangleHslA[i], vertexMvX[j10],
							vertexMvX[j11], vertexMvX[j12], vertexMvY[j10], vertexMvY[j11], vertexMvY[j12], vertexMvZ[j10], vertexMvZ[j11],
							vertexMvZ[j12], triangleColourOrTexture[i]);
					Rasterizer.method378(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], triangleHslA[i], triangleHslA[i],
							triangleHslA[i], vertexMvX[j10], vertexMvX[j11], vertexMvX[j12], vertexMvY[j10], vertexMvY[j11],
							vertexMvY[j12], vertexMvZ[j10], vertexMvZ[j11], vertexMvZ[j12], triangleColourOrTexture[i]);
				}
			}
		}
	}

	public final boolean method486(int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
		if (j < k && j < l && j < i1)
			return false;
		if (j > k && j > l && j > i1)
			return false;
		if (i < j1 && i < k1 && i < l1)
			return false;
		return i <= j1 || i <= k1 || i <= l1;
	}

	public short anInt1614;
	public boolean aBoolean1615;
	public short anInt1616;
	public short anInt1617;
	public boolean aBoolean1618;
	public static short anInt1619 = -192;
	public static short anInt1620;
	public static Model aModel_1621 = new Model(true);
	public static short anIntArray1622[] = new short[2000];
	public static short anIntArray1623[] = new short[2000];
	public static short anIntArray1624[] = new short[2000];
	public static short anIntArray1625[] = new short[2000];
	public short vertexCount;
	public short[] vertexX;
	public short vertexY[];
	public short vertexZ[];
	public short triangleCount;
	public int triangleA[];
	public int triangleB[];
	public int triangleC[];
	public int triangleHslA[];
	public int triangleHslB[];
	public int triangleHslC[];
	public short triangleDrawType[];
	public short facePriority[];
	public short[] triangleAlpha;
	public int triangleColourOrTexture[];
	public short anInt1641;
	public short textureTriangleCount;
	public short triPIndex[];
	public short triMIndex[];
	public short triNIndex[];
	public int minX;
	public int maxX;
	public int maxZ;
	public int minZ;
	public int diagonal2DAboveorigin;
	public int maxY;
	public int diagonal3D;
	public int diagonal3DAboveorigin;
	public int anInt1654;
	public short vertexVSkin[];
	public short triangleTSkin[];
	public short vertexSkin[][];
	public short triangleSkin[][];
	public boolean oneSquareModel;
	VertexNormal vertexNormalOffset[];
	static ModelHeader modelHeaderCache[];
	static ResourceProvider resourceProvider;
	static boolean aBooleanArray1663[] = new boolean[4096];
	static boolean aBooleanArray1664[] = new boolean[4096];
	static short vertexSX[] = new short[4096];
	static short vertexSY[] = new short[4096];
	static short depthBuffer[] = new short[4096];
	static short vertexMvX[] = new short[4096];
	static short vertexMvY[] = new short[4096];
	static short vertexMvZ[] = new short[4096];
	static short depthListIndices[] = new short[1500];
	static short faceLists[][] = new short[1500][512];
	static int anIntArray1673[] = new int[12];
	static short anIntArrayArray1674[][] = new short[12][2000];
	static short anIntArray1675[] = new short[2000];
	static short anIntArray1676[] = new short[2000];
	static int anIntArray1677[] = new int[12];
	static int anIntArray1678[] = new int[10];
	static int anIntArray1679[] = new int[10];
	static int anIntArray1680[] = new int[10];
	static int vertexXModifier;
	static int vertexYModifier;
	static int vertexZModifier;
	public static boolean aBoolean1684;
	public static int cursorXPos;
	public static int cursorYPos;
	public static int resourceCount;
	public static int resourceIDTAG[] = new int[1000];
	public static int SINE[];
	public static int COSINE[];
	static int HSL2RGB[];
	static int modelIntArray4[];
	static {
		SINE = Rasterizer.sineTable;
		COSINE = Rasterizer.cosineTable;
		HSL2RGB = Rasterizer.hsl2rgb;
		modelIntArray4 = Rasterizer.anIntArray1469;
	}
}