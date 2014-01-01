final class Region {

	public Region(byte[][][] renderRuleFlags, int[][][] vertexHeights) {
		Region.lowestPlane = 99;
		this.sizeX = 104; // width
		this.sizeY = 104; // height
		this.vertexHeights = vertexHeights;
		this.renderRuleFlags = renderRuleFlags;
		this.underlayFloorIds = new byte[4][sizeX][sizeY];
		this.overlayFloorIds = new byte[4][sizeX][sizeY];
		this.overlayClippingPaths = new byte[4][sizeX][sizeY];
		this.overlayRotations = new byte[4][sizeX][sizeY];
		this.tileCullingBitmap = new int[4][sizeX + 1][sizeY + 1];
		this.tileShadowIntensity = new byte[4][sizeX + 1][sizeY + 1];
		this.tileLightnessIntensity = new int[sizeX + 1][sizeY + 1];
		this.hueBuffer = new int[sizeY];
		this.saturationBuffer = new int[sizeY];
		this.lightnessBuffer = new int[sizeY];
		this.hueDivider = new int[sizeY];
		this.blendDirectionTracker = new int[sizeY];
	}

	public static int calculateNoise(int x, int seed) {
		int n = x + seed * 57;
		n = n << 13 ^ n;
		int noise = n * (n * n * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
		return noise >> 19 & 0xff;
	}

	public final void createRegionScene(CollisionMap[] collisionMaps, SceneGraph sceneGraph) {
		for (int tileY = 0; tileY < 4; tileY++) {
			for (int tileX = 0; tileX < 104; tileX++) {
				for (int tileZ = 0; tileZ < 104; tileZ++)
					if ((renderRuleFlags[tileY][tileX][tileZ] & 1) == 1) {
						int k1 = tileY;
						if ((renderRuleFlags[1][tileX][tileZ] & 2) == 2)
							k1--;
						if (k1 >= 0)
							collisionMaps[k1].orClipTableSET(tileZ, tileX);
					}
			}
		}
		for (int height = 0; height < 4; height++) {
			byte objectShadowArrays[][] = tileShadowIntensity[height];
			byte lightOffset = 96;
			char fallOff = '\u0400';// 0300 before
			byte lowestX = -50;
			byte lowestY = -10;
			byte lowestZ = -50;
			int lowest_magnitude = (int) Math.sqrt(lowestX * lowestX + lowestY * lowestY + lowestZ * lowestZ);
			int sqrtA = fallOff * lowest_magnitude >> 8;
			for (int tileY = 1; tileY < sizeY - 1; tileY++) {
				for (int tileX = 1; tileX < sizeX - 1; tileX++) {
					int highDetailX = vertexHeights[height][tileX + 1][tileY] - vertexHeights[height][tileX - 1][tileY];
					int highDetailY = vertexHeights[height][tileX][tileY + 1] - vertexHeights[height][tileX][tileY - 1];
					int normalMagnitude = (int) Math.sqrt(highDetailX * highDetailX + 0x10000 + highDetailY * highDetailY);
					int normalX = (highDetailX << 8) / normalMagnitude;
					int normalY = 0x10000 / normalMagnitude;
					int normalZ = (highDetailY << 8) / normalMagnitude;
					int diffuseLightness = lightOffset + (lowestX * normalX + lowestY * normalY + lowestZ * normalZ) / sqrtA;
					int j17 = (objectShadowArrays[tileX - 1][tileY] >> 2) + (objectShadowArrays[tileX + 1][tileY] >> 3)
							+ (objectShadowArrays[tileX][tileY - 1] >> 2) + (objectShadowArrays[tileX][tileY + 1] >> 3)
							+ (objectShadowArrays[tileX][tileY] >> 1);
					tileLightnessIntensity[tileX][tileY] = diffuseLightness - j17;
				}
			}
			for (int tileZ = 0; tileZ < sizeY; tileZ++) {
				hueBuffer[tileZ] = 0;
				saturationBuffer[tileZ] = 0;
				lightnessBuffer[tileZ] = 0;
				hueDivider[tileZ] = 0;
				blendDirectionTracker[tileZ] = 0;
			}
			for (int tileX = -5; tileX < sizeX + 5; tileX++) {
				for (int tileZ = 0; tileZ < sizeY; tileZ++) {
					int k9 = tileX + 5;
					if (k9 >= 0 && k9 < sizeX) {
						int l12 = underlayFloorIds[height][k9][tileZ] & 0xff;
						if (l12 > 0) {
							Floor floor = Floor.cache[l12 - 1];
							hueBuffer[tileZ] += floor.anInt397;
							saturationBuffer[tileZ] += floor.anInt395;
							lightnessBuffer[tileZ] += floor.anInt396;
							hueDivider[tileZ] += floor.anInt398;
							blendDirectionTracker[tileZ]++;
						}
					}
					int tileLowestx = tileX - 5;
					if (tileLowestx >= 0 && tileLowestx < sizeX) {
						int tileType = underlayFloorIds[height][tileLowestx][tileZ] & 0xff;
						if (tileType > 0) {
							Floor floor = Floor.cache[tileType - 1];
							hueBuffer[tileZ] -= floor.anInt397;
							saturationBuffer[tileZ] -= floor.anInt395;
							lightnessBuffer[tileZ] -= floor.anInt396;
							hueDivider[tileZ] -= floor.anInt398;
							blendDirectionTracker[tileZ]--;
						}
					}
				}
				if (tileX >= 1 && tileX < sizeX - 1) {
					int tileHue = 0;
					int tileSaturation = 0;
					int tileLight = 0;
					int tileHueShift = 0;
					int buffSize = 0;
					for (int tileZ = -5; tileZ < sizeY + 5; tileZ++) {
						int tile_b_z = tileZ + 5;
						if (tile_b_z >= 0 && tile_b_z < sizeY) {
							tileHue += hueBuffer[tile_b_z];
							tileSaturation += saturationBuffer[tile_b_z];
							tileLight += lightnessBuffer[tile_b_z];
							tileHueShift += hueDivider[tile_b_z];
							buffSize += blendDirectionTracker[tile_b_z];
						}
						int tileUndergroundZ = tileZ - 5;
						if (tileUndergroundZ >= 0 && tileUndergroundZ < sizeY) {
							tileHue -= hueBuffer[tileUndergroundZ];
							tileSaturation -= saturationBuffer[tileUndergroundZ];
							tileLight -= lightnessBuffer[tileUndergroundZ];
							tileHueShift -= hueDivider[tileUndergroundZ];
							buffSize -= blendDirectionTracker[tileUndergroundZ];
						}
						if (tileZ >= 1
								&& tileZ < sizeY - 1
								&& (!lowMem || (renderRuleFlags[0][tileX][tileZ] & 2) != 0 || (renderRuleFlags[height][tileX][tileZ] & 0x10) == 0
										&& getLogicHeight(tileZ, height, tileX) == anInt131)) {
							if (height < lowestPlane)
								lowestPlane = height;
							int tile_layer0_type = underlayFloorIds[height][tileX][tileZ] & 0xff;
							int tile_layer1_type = overlayFloorIds[height][tileX][tileZ] & 0xff;
							if (tile_layer0_type > 0 || tile_layer1_type > 0) {
								int y_a = vertexHeights[height][tileX][tileZ];
								int y_b = vertexHeights[height][tileX + 1][tileZ];
								int y_d = vertexHeights[height][tileX + 1][tileZ + 1];
								int y_c = vertexHeights[height][tileX][tileZ + 1];
								int lightness_a = tileLightnessIntensity[tileX][tileZ];
								int lightness_b = tileLightnessIntensity[tileX + 1][tileZ];
								int lightness_d = tileLightnessIntensity[tileX + 1][tileZ + 1];
								int lightness_c = tileLightnessIntensity[tileX][tileZ + 1];
								int underlayHslReal = -1;
								int underlayHsl = -1;
								if (tile_layer0_type > 0) {
									int h = (tileHue * 256) / tileHueShift;
									int s = tileSaturation / buffSize;
									int l = tileLight / buffSize;
									underlayHslReal = packHSL(h, s, l);
									underlayHsl = packHSL(h, s, l);
								}
								if (height > 0) {
									boolean isUnderlayHidden = true;
									if (tile_layer0_type == 0 && overlayClippingPaths[height][tileX][tileZ] != 0)
										isUnderlayHidden = false;
									if (tile_layer1_type > 0 && !Floor.cache[tile_layer1_type - 1].aBoolean393)
										isUnderlayHidden = false;
									if (isUnderlayHidden && y_a == y_b && y_a == y_d && y_a == y_c)
										tileCullingBitmap[height][tileX][tileZ] |= 0x924;
								}
								int underlay_rgb = 0;
								if (underlayHslReal != -1)
									underlay_rgb = Rasterizer.hsl2rgb[mixLightness(underlayHsl, 96)];
								if (tile_layer1_type == 0) {
									sceneGraph.addTile(height, tileX, tileZ, 0, 0, -1, y_a, y_b, y_d, y_c,
											mixLightness(underlayHslReal, lightness_a), mixLightness(underlayHslReal, lightness_b),
											mixLightness(underlayHslReal, lightness_d), mixLightness(underlayHslReal, lightness_c), 0, 0,
											0, 0, underlay_rgb, 0);
								} else {
									int k22 = overlayClippingPaths[height][tileX][tileZ] + 1;
									byte byte4 = overlayRotations[height][tileX][tileZ];
									Floor floor = Floor.cache[tile_layer1_type - 1];
									int i23 = floor.anInt391;
									int j23;
									int k23;
									if (i23 >= 0) {
										k23 = Rasterizer.method369(i23);
										j23 = -1;
									} else if (floor.overlay == 0xff00ff) {
										k23 = 0;
										if (floor.minimapColour != 0)
											k23 = Rasterizer.hsl2rgb[mixLightnessSigned(floor.anInt399_2, 96)];
										j23 = -2;
										i23 = -1;
									} else {
										j23 = packHSL(floor.anInt394_2, floor.anInt395_2, floor.anInt396_2);// ground
										k23 = Rasterizer.hsl2rgb[mixLightnessSigned(j23, 96)];// minimap
									}
									sceneGraph.addTile(height, tileX, tileZ, k22, byte4, i23, y_a, y_b, y_d, y_c,
											mixLightness(underlayHslReal, lightness_a), mixLightness(underlayHslReal, lightness_b),
											mixLightness(underlayHslReal, lightness_d), mixLightness(underlayHslReal, lightness_c),
											mixLightnessSigned(j23, lightness_a), mixLightnessSigned(j23, lightness_b),
											mixLightnessSigned(j23, lightness_d), mixLightnessSigned(j23, lightness_c), underlay_rgb, k23);
								}
							}
						}
					}
				}
			}
			for (int j8 = 1; j8 < sizeY - 1; j8++) {
				for (int i10 = 1; i10 < sizeX - 1; i10++)
					sceneGraph.setTileLogicHeight(height, i10, j8, getLogicHeight(j8, height, i10));
			}
		}
		sceneGraph.shadeModels(79, 1368, -4, -5, -4);
		for (int j1 = 0; j1 < sizeX; j1++) {
			for (int l1 = 0; l1 < sizeY; l1++)
				if ((renderRuleFlags[1][j1][l1] & 2) == 2)
					sceneGraph.applyBridgeMode(l1, j1);
		}
		int i2 = 1;
		int j2 = 2;
		int k2 = 4;
		for (int l2 = 0; l2 < 4; l2++) {
			if (l2 > 0) {
				i2 <<= 3;
				j2 <<= 3;
				k2 <<= 3;
			}
			for (int i3 = 0; i3 <= l2; i3++) {
				for (int k3 = 0; k3 <= sizeY; k3++) {
					for (int i4 = 0; i4 <= sizeX; i4++) {
						if ((tileCullingBitmap[i3][i4][k3] & i2) != 0) {
							int k4 = k3;
							int l5 = k3;
							int i7 = i3;
							int k8 = i3;
							for (; k4 > 0 && (tileCullingBitmap[i3][i4][k4 - 1] & i2) != 0; k4--)
								;
							for (; l5 < sizeY && (tileCullingBitmap[i3][i4][l5 + 1] & i2) != 0; l5++)
								;
							label0: for (; i7 > 0; i7--) {
								for (int j10 = k4; j10 <= l5; j10++)
									if ((tileCullingBitmap[i7 - 1][i4][j10] & i2) == 0)
										break label0;
							}
							label1: for (; k8 < l2; k8++) {
								for (int k10 = k4; k10 <= l5; k10++)
									if ((tileCullingBitmap[k8 + 1][i4][k10] & i2) == 0)
										break label1;
							}
							int l10 = ((k8 + 1) - i7) * ((l5 - k4) + 1);
							if (l10 >= 8) {
								char c1 = '\360';
								int k14 = vertexHeights[k8][i4][k4] - c1;
								int l15 = vertexHeights[i7][i4][k4];
								SceneGraph.createCullingCluster(l2, i4 * 128, l15, i4 * 128, l5 * 128 + 128, k14, k4 * 128, 1);
								for (int l16 = i7; l16 <= k8; l16++) {
									for (int l17 = k4; l17 <= l5; l17++)
										tileCullingBitmap[l16][i4][l17] &= ~i2;
								}
							}
						}
						if ((tileCullingBitmap[i3][i4][k3] & j2) != 0) {
							int l4 = i4;
							int i6 = i4;
							int j7 = i3;
							int l8 = i3;
							for (; l4 > 0 && (tileCullingBitmap[i3][l4 - 1][k3] & j2) != 0; l4--)
								;
							for (; i6 < sizeX && (tileCullingBitmap[i3][i6 + 1][k3] & j2) != 0; i6++)
								;
							label2: for (; j7 > 0; j7--) {
								for (int i11 = l4; i11 <= i6; i11++)
									if ((tileCullingBitmap[j7 - 1][i11][k3] & j2) == 0)
										break label2;
							}
							label3: for (; l8 < l2; l8++) {
								for (int j11 = l4; j11 <= i6; j11++)
									if ((tileCullingBitmap[l8 + 1][j11][k3] & j2) == 0)
										break label3;
							}
							int k11 = ((l8 + 1) - j7) * ((i6 - l4) + 1);
							if (k11 >= 8) {
								char c2 = '\360';
								int l14 = vertexHeights[l8][l4][k3] - c2;
								int i16 = vertexHeights[j7][l4][k3];
								SceneGraph.createCullingCluster(l2, l4 * 128, i16, i6 * 128 + 128, k3 * 128, l14, k3 * 128, 2);
								for (int i17 = j7; i17 <= l8; i17++) {
									for (int i18 = l4; i18 <= i6; i18++)
										tileCullingBitmap[i17][i18][k3] &= ~j2;
								}
							}
						}
						if ((tileCullingBitmap[i3][i4][k3] & k2) != 0) {
							int i5 = i4;
							int j6 = i4;
							int k7 = k3;
							int i9 = k3;
							for (; k7 > 0 && (tileCullingBitmap[i3][i4][k7 - 1] & k2) != 0; k7--)
								;
							for (; i9 < sizeY && (tileCullingBitmap[i3][i4][i9 + 1] & k2) != 0; i9++)
								;
							label4: for (; i5 > 0; i5--) {
								for (int l11 = k7; l11 <= i9; l11++)
									if ((tileCullingBitmap[i3][i5 - 1][l11] & k2) == 0)
										break label4;
							}
							label5: for (; j6 < sizeX; j6++) {
								for (int i12 = k7; i12 <= i9; i12++)
									if ((tileCullingBitmap[i3][j6 + 1][i12] & k2) == 0)
										break label5;
							}
							if (((j6 - i5) + 1) * ((i9 - k7) + 1) >= 4) {
								int j12 = vertexHeights[i3][i5][k7];
								SceneGraph.createCullingCluster(l2, i5 * 128, j12, j6 * 128 + 128, i9 * 128 + 128, j12, k7 * 128, 4);
								for (int k13 = i5; k13 <= j6; k13++) {
									for (int i15 = k7; i15 <= i9; i15++)
										tileCullingBitmap[i3][k13][i15] &= ~k2;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * http://www.uiowa.edu/~examserv/mathmatters/tutorial_quiz/geometry/
	 * findingvertexofparabola.html Barry
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static int calculateVertexHeight(int x, int y) {
		int noise = (perlinNoise(x + 45365, y + 0x16713, 4) - 128) + (perlinNoise(x + 10294, y + 37821, 2) - 128 >> 1)
				+ (perlinNoise(x, y, 1) - 128 >> 2);
		noise = (int) ((double) noise * 0.29999999999999999D) + 35;
		if (noise < 10)
			noise = 10;
		else if (noise > 60)
			noise = 60;
		return noise;
	}

	public final void clearRegion(int z, int height, int width, int x) {
		for (int j1 = z; j1 <= z + height; j1++) {
			for (int k1 = x; k1 <= x + width; k1++)
				if (k1 >= 0 && k1 < sizeX && j1 >= 0 && j1 < sizeY) {
					tileShadowIntensity[0][k1][j1] = 127;
					if (k1 == x && k1 > 0)
						vertexHeights[0][k1][j1] = vertexHeights[0][k1 - 1][j1];
					if (k1 == x + width && k1 < sizeX - 1)
						vertexHeights[0][k1][j1] = vertexHeights[0][k1 + 1][j1];
					if (j1 == z && j1 > 0)
						vertexHeights[0][k1][j1] = vertexHeights[0][k1][j1 - 1];
					if (j1 == z + height && j1 < sizeY - 1)
						vertexHeights[0][k1][j1] = vertexHeights[0][k1][j1 + 1];
				}
		}
	}

	public void addObject(int i, SceneGraph sceneGraph, CollisionMap tileSetting, int j, int k, int l, int i1, int j1) {
		if (lowMem && (renderRuleFlags[0][l][i] & 2) == 0) {
			if ((renderRuleFlags[k][l][i] & 0x10) != 0)
				return;
			if (getLogicHeight(i, k, l) != anInt131)
				return;
		}
		if (k < lowestPlane)
			lowestPlane = k;
		int k1 = vertexHeights[k][l][i];
		int l1 = vertexHeights[k][l + 1][i];
		int i2 = vertexHeights[k][l + 1][i + 1];
		int j2 = vertexHeights[k][l][i + 1];
		int k2 = k1 + l1 + i2 + j2 >> 2;
		ObjectDefinition objectDefinition = ObjectDefinition.forID(i1);
		int l2 = l + (i << 7) + (i1 << 14) + 0x40000000;
		if (!objectDefinition.hasActions)
			l2 += 0x80000000;
		byte byte0 = (byte) ((j1 << 6) + j);
		if (j == 22) {
			if (lowMem && !objectDefinition.hasActions && !objectDefinition.aBoolean736)
				return;
			Object obj;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null)
				obj = objectDefinition.generateModel(22, j1, k1, l1, i2, j2, -1);
			else
				obj = new GameObject(i1, j1, 22, l1, i2, k1, j2, objectDefinition.animID, true);
			sceneGraph.addGroundDecoration(k, k2, i, ((Renderable) (obj)), byte0, l2, l);
			if (objectDefinition.isSolid && objectDefinition.hasActions && tileSetting != null)
				tileSetting.orClipTableSET(i, l);
			return;
		}
		if (j == 10 || j == 11) {
			Object obj1;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null)
				obj1 = objectDefinition.generateModel(10, j1, k1, l1, i2, j2, -1);
			else
				obj1 = new GameObject(i1, j1, 10, l1, i2, k1, j2, objectDefinition.animID, true);
			if (obj1 != null) {
				int i5 = 0;
				if (j == 11)
					i5 += 256;
				int j4;
				int l4;
				if (j1 == 1 || j1 == 3) {
					j4 = objectDefinition.height;
					l4 = objectDefinition.width;
				} else {
					j4 = objectDefinition.width;
					l4 = objectDefinition.height;
				}
				if (sceneGraph.addEntityB(l2, byte0, k2, l4, ((Renderable) (obj1)), j4, k, i5, i, l) && objectDefinition.aBoolean779) {
					Model model;
					if (obj1 instanceof Model)
						model = (Model) obj1;
					else
						model = objectDefinition.generateModel(10, j1, k1, l1, i2, j2, -1);
					if (model != null) {
						for (int j5 = 0; j5 <= j4; j5++) {
							for (int k5 = 0; k5 <= l4; k5++) {
								int l5 = model.diagonal2DAboveorigin / 4;
								if (l5 > 30)
									l5 = 30;
								if (l5 > tileShadowIntensity[k][l + j5][i + k5])
									tileShadowIntensity[k][l + j5][i + k5] = (byte) l5;
							}
						}
					}
				}
			}
			if (objectDefinition.isSolid && tileSetting != null)
				tileSetting.method212(objectDefinition.isRangeable, objectDefinition.width, objectDefinition.height, l, i, j1);
			return;
		}
		if (j >= 12) {
			Object obj2;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null)
				obj2 = objectDefinition.generateModel(j, j1, k1, l1, i2, j2, -1);
			else
				obj2 = new GameObject(i1, j1, j, l1, i2, k1, j2, objectDefinition.animID, true);
			sceneGraph.addEntityB(l2, byte0, k2, 1, ((Renderable) (obj2)), 1, k, 0, i, l);
			if (j >= 12 && j <= 17 && j != 13 && k > 0)
				tileCullingBitmap[k][l][i] |= 0x924;
			if (objectDefinition.isSolid && tileSetting != null)
				tileSetting.method212(objectDefinition.isRangeable, objectDefinition.width, objectDefinition.height, l, i, j1);
			return;
		}
		if (j == 0) {
			Object obj3;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null)
				obj3 = objectDefinition.generateModel(0, j1, k1, l1, i2, j2, -1);
			else
				obj3 = new GameObject(i1, j1, 0, l1, i2, k1, j2, objectDefinition.animID, true);
			sceneGraph.addWallObject(anIntArray152[j1], ((Renderable) (obj3)), l2, i, byte0, l, null, k2, 0, k);
			if (j1 == 0) {
				if (objectDefinition.aBoolean779) {
					tileShadowIntensity[k][l][i] = 50;
					tileShadowIntensity[k][l][i + 1] = 50;
				}
				if (objectDefinition.aBoolean764)
					tileCullingBitmap[k][l][i] |= 0x249;
			} else if (j1 == 1) {
				if (objectDefinition.aBoolean779) {
					tileShadowIntensity[k][l][i + 1] = 50;
					tileShadowIntensity[k][l + 1][i + 1] = 50;
				}
				if (objectDefinition.aBoolean764)
					tileCullingBitmap[k][l][i + 1] |= 0x492;
			} else if (j1 == 2) {
				if (objectDefinition.aBoolean779) {
					tileShadowIntensity[k][l + 1][i] = 50;
					tileShadowIntensity[k][l + 1][i + 1] = 50;
				}
				if (objectDefinition.aBoolean764)
					tileCullingBitmap[k][l + 1][i] |= 0x249;
			} else if (j1 == 3) {
				if (objectDefinition.aBoolean779) {
					tileShadowIntensity[k][l][i] = 50;
					tileShadowIntensity[k][l + 1][i] = 50;
				}
				if (objectDefinition.aBoolean764)
					tileCullingBitmap[k][l][i] |= 0x492;
			}
			if (objectDefinition.isSolid && tileSetting != null)
				tileSetting.markWall(i, j1, l, j, objectDefinition.isRangeable);
			if (objectDefinition.anInt775 != 16)
				sceneGraph.method290(i, objectDefinition.anInt775, l, k);
			return;
		}
		if (j == 1) {
			Object obj4;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null)
				obj4 = objectDefinition.generateModel(1, j1, k1, l1, i2, j2, -1);
			else
				obj4 = new GameObject(i1, j1, 1, l1, i2, k1, j2, objectDefinition.animID, true);
			sceneGraph.addWallObject(anIntArray140[j1], ((Renderable) (obj4)), l2, i, byte0, l, null, k2, 0, k);
			if (objectDefinition.aBoolean779)
				if (j1 == 0)
					tileShadowIntensity[k][l][i + 1] = 50;
				else if (j1 == 1)
					tileShadowIntensity[k][l + 1][i + 1] = 50;
				else if (j1 == 2)
					tileShadowIntensity[k][l + 1][i] = 50;
				else if (j1 == 3)
					tileShadowIntensity[k][l][i] = 50;
			if (objectDefinition.isSolid && tileSetting != null)
				tileSetting.markWall(i, j1, l, j, objectDefinition.isRangeable);
			return;
		}
		if (j == 2) {
			int i3 = j1 + 1 & 3;
			Object obj11;
			Object obj12;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null) {
				obj11 = objectDefinition.generateModel(2, 4 + j1, k1, l1, i2, j2, -1);
				obj12 = objectDefinition.generateModel(2, i3, k1, l1, i2, j2, -1);
			} else {
				obj11 = new GameObject(i1, 4 + j1, 2, l1, i2, k1, j2, objectDefinition.animID, true);
				obj12 = new GameObject(i1, i3, 2, l1, i2, k1, j2, objectDefinition.animID, true);
			}
			sceneGraph.addWallObject(anIntArray152[j1], ((Renderable) (obj11)), l2, i, byte0, l, ((Renderable) (obj12)), k2,
					anIntArray152[i3], k);
			if (objectDefinition.aBoolean764)
				if (j1 == 0) {
					tileCullingBitmap[k][l][i] |= 0x249;
					tileCullingBitmap[k][l][i + 1] |= 0x492;
				} else if (j1 == 1) {
					tileCullingBitmap[k][l][i + 1] |= 0x492;
					tileCullingBitmap[k][l + 1][i] |= 0x249;
				} else if (j1 == 2) {
					tileCullingBitmap[k][l + 1][i] |= 0x249;
					tileCullingBitmap[k][l][i] |= 0x492;
				} else if (j1 == 3) {
					tileCullingBitmap[k][l][i] |= 0x492;
					tileCullingBitmap[k][l][i] |= 0x249;
				}
			if (objectDefinition.isSolid && tileSetting != null)
				tileSetting.markWall(i, j1, l, j, objectDefinition.isRangeable);
			if (objectDefinition.anInt775 != 16)
				sceneGraph.method290(i, objectDefinition.anInt775, l, k);
			return;
		}
		if (j == 3) {
			Object obj5;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null)
				obj5 = objectDefinition.generateModel(3, j1, k1, l1, i2, j2, -1);
			else
				obj5 = new GameObject(i1, j1, 3, l1, i2, k1, j2, objectDefinition.animID, true);
			sceneGraph.addWallObject(anIntArray140[j1], ((Renderable) (obj5)), l2, i, byte0, l, null, k2, 0, k);
			if (objectDefinition.aBoolean779)
				if (j1 == 0)
					tileShadowIntensity[k][l][i + 1] = 50;
				else if (j1 == 1)
					tileShadowIntensity[k][l + 1][i + 1] = 50;
				else if (j1 == 2)
					tileShadowIntensity[k][l + 1][i] = 50;
				else if (j1 == 3)
					tileShadowIntensity[k][l][i] = 50;
			if (objectDefinition.isSolid && tileSetting != null)
				tileSetting.markWall(i, j1, l, j, objectDefinition.isRangeable);
			return;
		}
		if (j == 9) {
			Object obj6;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null)
				obj6 = objectDefinition.generateModel(j, j1, k1, l1, i2, j2, -1);
			else
				obj6 = new GameObject(i1, j1, j, l1, i2, k1, j2, objectDefinition.animID, true);
			sceneGraph.addEntityB(l2, byte0, k2, 1, ((Renderable) (obj6)), 1, k, 0, i, l);
			if (objectDefinition.isSolid && tileSetting != null)
				tileSetting.method212(objectDefinition.isRangeable, objectDefinition.width, objectDefinition.height, l, i, j1);
			return;
		}
		if (objectDefinition.aBoolean762)
			if (j1 == 1) {
				int j3 = j2;
				j2 = i2;
				i2 = l1;
				l1 = k1;
				k1 = j3;
			} else if (j1 == 2) {
				int k3 = j2;
				j2 = l1;
				l1 = k3;
				k3 = i2;
				i2 = k1;
				k1 = k3;
			} else if (j1 == 3) {
				int l3 = j2;
				j2 = k1;
				k1 = l1;
				l1 = i2;
				i2 = l3;
			}
		if (j == 4) {
			Object obj7;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null)
				obj7 = objectDefinition.generateModel(4, 0, k1, l1, i2, j2, -1);
			else
				obj7 = new GameObject(i1, 0, 4, l1, i2, k1, j2, objectDefinition.animID, true);
			sceneGraph.addWallDecoration(l2, i, j1 * 512, k, 0, k2, ((Renderable) (obj7)), l, byte0, 0, anIntArray152[j1]);
			return;
		}
		if (j == 5) {
			int i4 = 16;
			int k4 = sceneGraph.getWallObjectUID(k, l, i);
			if (k4 > 0)
				i4 = ObjectDefinition.forID(k4 >> 14 & 0x7fff).anInt775;
			Object obj13;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null)
				obj13 = objectDefinition.generateModel(4, 0, k1, l1, i2, j2, -1);
			else
				obj13 = new GameObject(i1, 0, 4, l1, i2, k1, j2, objectDefinition.animID, true);
			sceneGraph.addWallDecoration(l2, i, j1 * 512, k, anIntArray137[j1] * i4, k2, ((Renderable) (obj13)), l, byte0,
					anIntArray144[j1] * i4, anIntArray152[j1]);
			return;
		}
		if (j == 6) {
			Object obj8;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null)
				obj8 = objectDefinition.generateModel(4, 0, k1, l1, i2, j2, -1);
			else
				obj8 = new GameObject(i1, 0, 4, l1, i2, k1, j2, objectDefinition.animID, true);
			sceneGraph.addWallDecoration(l2, i, j1, k, 0, k2, ((Renderable) (obj8)), l, byte0, 0, 256);
			return;
		}
		if (j == 7) {
			Object obj9;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null)
				obj9 = objectDefinition.generateModel(4, 0, k1, l1, i2, j2, -1);
			else
				obj9 = new GameObject(i1, 0, 4, l1, i2, k1, j2, objectDefinition.animID, true);
			sceneGraph.addWallDecoration(l2, i, j1, k, 0, k2, ((Renderable) (obj9)), l, byte0, 0, 512);
			return;
		}
		if (j == 8) {
			Object obj10;
			if (objectDefinition.animID == -1 && objectDefinition.childrenIDs == null)
				obj10 = objectDefinition.generateModel(4, 0, k1, l1, i2, j2, -1);
			else
				obj10 = new GameObject(i1, 0, 4, l1, i2, k1, j2, objectDefinition.animID, true);
			sceneGraph.addWallDecoration(l2, i, j1, k, 0, k2, ((Renderable) (obj10)), l, byte0, 0, 768);
		}
	}

	public static int perlinNoise(int x, int y, int scale) {
		int l = x / scale;
		int i1 = x & scale - 1;
		int j1 = y / scale;
		int k1 = y & scale - 1;
		int l1 = randomNoiseWeighedSum(l, j1);
		int i2 = randomNoiseWeighedSum(l + 1, j1);
		int j2 = randomNoiseWeighedSum(l, j1 + 1);
		int k2 = randomNoiseWeighedSum(l + 1, j1 + 1);
		int l2 = getInterpolate(l1, i2, i1, scale);
		int i3 = getInterpolate(j2, k2, i1, scale);
		return getInterpolate(l2, i3, k1, scale);
	}

	/**
	 * Packs separate hue, saturation and lightness_buffer into a 18bit HSL word
	 * 
	 * @param hue
	 *            The hue value to pack
	 * @param saturation
	 *            The saturation value to pack
	 * @param lightness
	 *            The lightness_buffer to pack
	 * @return The packed HSL word
	 */

	public int packHSL(int hue, int saturation, int lightness) {
		if (lightness > 179)
			saturation /= 2;
		if (lightness > 192)
			saturation /= 2;
		if (lightness > 217)
			saturation /= 2;
		if (lightness > 243)
			saturation /= 2;
		return (hue / 4 << 10) + (saturation / 32 << 7) + lightness / 2;
	}

	public static boolean isObjectCachedType(int objectId, int pbjectType) {
		ObjectDefinition objDefinition = ObjectDefinition.forID(objectId);
		if (pbjectType == 11)
			pbjectType = 10;
		if (pbjectType >= 5 && pbjectType <= 8)
			pbjectType = 4;
		return objDefinition.isCachedType(pbjectType);
	}

	public final void loadTerrainSubBlock(int subblockY, int rotation, CollisionMap[] collisionMaps, int blockX, int subblockX,
			byte data[], int subblockZ, int k1, int block_z) {
		for (int tileX = 0; tileX < 8; tileX++) {
			for (int tileZ = 0; tileZ < 8; tileZ++)
				if (blockX + tileX > 0 && blockX + tileX < 103 && block_z + tileZ > 0 && block_z + tileZ < 103)
					collisionMaps[k1].clips[blockX + tileX][block_z + tileZ] &= 0xfeffffff;
		}
		RSBuffer buffer = new RSBuffer(data);
		for (int tileY = 0; tileY < 4; tileY++) {
			for (int tileZ = 0; tileZ < 64; tileZ++) {
				for (int tileX = 0; tileX < 64; tileX++)
					if (tileY == subblockY && tileZ >= subblockX && tileZ < subblockX + 8 && tileX >= subblockZ && tileX < subblockZ + 8) {
						loadTerrainTile(block_z + MapUtility.rotateTerrainBlockX(tileX & 7, rotation, tileZ & 7), 0, buffer, blockX
								+ MapUtility.rotateTerrainBlockZ(rotation, tileX & 7, tileZ & 7), k1, rotation, 0);
					} else
						loadTerrainTile(-1, 0, buffer, -1, 0, 0, 0);
			}
		}
	}

	public final void loadTerrainBlock(byte data[], int i, int j, int k, int l, CollisionMap collisionMaps[]) {
		for (int i1 = 0; i1 < 4; i1++) {
			for (int j1 = 0; j1 < 64; j1++) {
				for (int k1 = 0; k1 < 64; k1++)
					if (j + j1 > 0 && j + j1 < 103 && i + k1 > 0 && i + k1 < 103)
						collisionMaps[i1].clips[j + j1][i + k1] &= 0xfeffffff;
			}
		}
		RSBuffer stream = new RSBuffer(data);
		for (int l1 = 0; l1 < 4; l1++) {
			for (int i2 = 0; i2 < 64; i2++) {
				for (int j2 = 0; j2 < 64; j2++)
					loadTerrainTile(j2 + i, l, stream, i2 + j, l1, 0, k);
			}
		}
	}

	public void loadTerrainTile(int i, int j, RSBuffer stream, int k, int l, int i1, int k1) {
		if (k >= 0 && k < 104 && i >= 0 && i < 104) {
			renderRuleFlags[l][k][i] = 0;
			do {
				int l1 = stream.readUByte();
				if (l1 == 0)
					if (l == 0) {
						vertexHeights[0][k][i] = -calculateVertexHeight(0xe3b7b + k + k1, 0x87cce + i + j) * 8;
						return;
					} else {
						vertexHeights[l][k][i] = vertexHeights[l - 1][k][i] - 240;
						return;
					}
				if (l1 == 1) {
					int j2 = stream.readUByte();
					if (j2 == 1)
						j2 = 0;
					if (l == 0) {
						vertexHeights[0][k][i] = -j2 * 8;
						return;
					} else {
						vertexHeights[l][k][i] = vertexHeights[l - 1][k][i] - j2 * 8;
						return;
					}
				}
				if (l1 <= 49) {
					overlayFloorIds[l][k][i] = stream.readByte();
					overlayClippingPaths[l][k][i] = (byte) ((l1 - 2) / 4);
					overlayRotations[l][k][i] = (byte) ((l1 - 2) + i1 & 3);
				} else if (l1 <= 81)
					renderRuleFlags[l][k][i] = (byte) (l1 - 49);
				else
					underlayFloorIds[l][k][i] = (byte) (l1 - 81);
			} while (true);
		}
		do {
			int i2 = stream.readUByte();
			if (i2 == 0)
				break;
			if (i2 == 1) {
				stream.readUByte();
				return;
			}
			if (i2 <= 49)
				stream.readUByte();
		} while (true);
	}

	public int getLogicHeight(int i, int j, int k) {
		if ((renderRuleFlags[j][k][i] & 8) != 0)
			return 0;
		if (j > 0 && (renderRuleFlags[1][k][i] & 2) != 0)
			return j - 1;
		else
			return j;
	}

	public final void loadObjectBlock(CollisionMap collisionMaps[], SceneGraph sceneGraph, int i, int j, int k, int l, byte data[], int i1,
			int j1, int k1) {
		label0: {
			RSBuffer buffer = new RSBuffer(data);
			int objectId = -1;
			do {
				int deltaId = buffer.readUSmart2();
				if (deltaId == 0)
					break label0;
				objectId += deltaId;
				int pos = 0;
				do {
					int deltaPos = buffer.readSmarts();
					if (deltaPos == 0)
						break;
					pos += deltaPos - 1;
					int tileZ = pos & 0x3f;
					int tileX = pos >> 6 & 0x3f;
					int tileY = pos >> 12;
					int objectInfo = buffer.readUByte();
					int objectType = objectInfo >> 2;
					int objRot = objectInfo & 3;
					if (tileY == i && tileX >= i1 && tileX < i1 + 8 && tileZ >= k && tileZ < k + 8) {
						ObjectDefinition objDefinition = ObjectDefinition.forID(objectId);
						int j4 = j
								+ MapUtility.method157(j1, (objRot == 0 || objRot == 2) ? objDefinition.height : objDefinition.width,
										tileX & 7, tileZ & 7, (objRot == 0 || objRot == 2) ? objDefinition.width : objDefinition.height);
						int k4 = k1
								+ MapUtility.method158(tileZ & 7,
										(objRot == 0 || objRot == 2) ? objDefinition.height : objDefinition.width, j1,
										(objRot == 0 || objRot == 2) ? objDefinition.width : objDefinition.height, tileX & 7);
						if (j4 > 0 && k4 > 0 && j4 < 103 && k4 < 103) {
							int l4 = tileY;
							if ((renderRuleFlags[1][j4][k4] & 2) == 2)
								l4--;
							CollisionMap class11 = null;
							if (l4 >= 0)
								class11 = collisionMaps[l4];
							addObject(k4, sceneGraph, class11, objectType, l, j4, objectId, objRot + j1 & 3);
						}
					}
				} while (true);
			} while (true);
		}
	}

	/**
	 * Implements a cosine interpolation.
	 * 
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 * @return
	 */
	public static int getInterpolate(int i, int j, int k, int l) {
		int i1 = 0x10000 - Rasterizer.cosineTable[(k * 1024) / l] >> 1;
		return (i * (0x10000 - i1) >> 16) + (j * i1 >> 16);
	}

	public int mixLightnessSigned(int hsl, int lightness) {
		if (hsl == -2)
			return 0xbc614e; // 12345678 - Barry
		if (hsl == -1) {
			if (lightness < 0)
				lightness = 0;
			else if (lightness > 127)
				lightness = 127;
			lightness = 127 - lightness;
			return lightness;
		}
		lightness = (lightness * (hsl & 0x7f)) / 128;
		if (lightness < 2)
			lightness = 2;
		else if (lightness > 126)
			lightness = 126;
		return (hsl & 0xff80) + lightness;
	}

	/**
	 * tbh unsure about this.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static int randomNoiseWeighedSum(int x, int y) {
		int secondVDist = calculateNoise(x - 1, y - 1) + calculateNoise(x + 1, y - 1) + calculateNoise(x - 1, y + 1)
				+ calculateNoise(x + 1, y + 1);
		int firstVDist = calculateNoise(x - 1, y) + calculateNoise(x + 1, y) + calculateNoise(x, y - 1) + calculateNoise(x, y + 1);
		int vLocal = calculateNoise(x, y);
		return secondVDist / 16 + firstVDist / 8 + vLocal / 4;
	}

	public static int mixLightness(int i, int j) {
		if (i == -1)
			return 0xbc614e;
		j = (j * (i & 0x7f)) / 128;
		if (j < 2)
			j = 2;
		else if (j > 126)
			j = 126;
		return (i & 0xff80) + j;
	}

	public static void addObject(SceneGraph sceneGraph, int i, int j, int k, int l, CollisionMap tileSetting, int ai[][][], int i1, int j1,
			int k1) {
		int l1 = ai[l][i1][j];
		int i2 = ai[l][i1 + 1][j];
		int j2 = ai[l][i1 + 1][j + 1];
		int k2 = ai[l][i1][j + 1];
		int l2 = l1 + i2 + j2 + k2 >> 2;
		ObjectDefinition objDefinition = ObjectDefinition.forID(j1);
		int i3 = i1 + (j << 7) + (j1 << 14) + 0x40000000;
		if (!objDefinition.hasActions)
			i3 += 0x80000000;
		byte byte1 = (byte) ((i << 6) + k);
		if (k == 22) {
			Object obj;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null)
				obj = objDefinition.generateModel(22, i, l1, i2, j2, k2, -1);
			else
				obj = new GameObject(j1, i, 22, i2, j2, l1, k2, objDefinition.animID, true);
			sceneGraph.addGroundDecoration(k1, l2, j, ((Renderable) (obj)), byte1, i3, i1);
			if (objDefinition.isSolid && objDefinition.hasActions)
				tileSetting.orClipTableSET(j, i1);
			return;
		}
		if (k == 10 || k == 11) {
			Object obj1;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null)
				obj1 = objDefinition.generateModel(10, i, l1, i2, j2, k2, -1);
			else
				obj1 = new GameObject(j1, i, 10, i2, j2, l1, k2, objDefinition.animID, true);
			if (obj1 != null) {
				int j5 = 0;
				if (k == 11)
					j5 += 256;
				int k4;
				int i5;
				if (i == 1 || i == 3) {
					k4 = objDefinition.height;
					i5 = objDefinition.width;
				} else {
					k4 = objDefinition.width;
					i5 = objDefinition.height;
				}
				sceneGraph.addEntityB(i3, byte1, l2, i5, ((Renderable) (obj1)), k4, k1, j5, j, i1);
			}
			if (objDefinition.isSolid)
				tileSetting.method212(objDefinition.isRangeable, objDefinition.width, objDefinition.height, i1, j, i);
			return;
		}
		if (k >= 12) {
			Object obj2;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null)
				obj2 = objDefinition.generateModel(k, i, l1, i2, j2, k2, -1);
			else
				obj2 = new GameObject(j1, i, k, i2, j2, l1, k2, objDefinition.animID, true);
			sceneGraph.addEntityB(i3, byte1, l2, 1, ((Renderable) (obj2)), 1, k1, 0, j, i1);
			if (objDefinition.isSolid)
				tileSetting.method212(objDefinition.isRangeable, objDefinition.width, objDefinition.height, i1, j, i);
			return;
		}
		if (k == 0) {
			Object obj3;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null)
				obj3 = objDefinition.generateModel(0, i, l1, i2, j2, k2, -1);
			else
				obj3 = new GameObject(j1, i, 0, i2, j2, l1, k2, objDefinition.animID, true);
			sceneGraph.addWallObject(anIntArray152[i], ((Renderable) (obj3)), i3, j, byte1, i1, null, l2, 0, k1);
			if (objDefinition.isSolid)
				tileSetting.markWall(j, i, i1, k, objDefinition.isRangeable);
			return;
		}
		if (k == 1) {
			Object obj4;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null)
				obj4 = objDefinition.generateModel(1, i, l1, i2, j2, k2, -1);
			else
				obj4 = new GameObject(j1, i, 1, i2, j2, l1, k2, objDefinition.animID, true);
			sceneGraph.addWallObject(anIntArray140[i], ((Renderable) (obj4)), i3, j, byte1, i1, null, l2, 0, k1);
			if (objDefinition.isSolid)
				tileSetting.markWall(j, i, i1, k, objDefinition.isRangeable);
			return;
		}
		if (k == 2) {
			int j3 = i + 1 & 3;
			Object obj11;
			Object obj12;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null) {
				obj11 = objDefinition.generateModel(2, 4 + i, l1, i2, j2, k2, -1);
				obj12 = objDefinition.generateModel(2, j3, l1, i2, j2, k2, -1);
			} else {
				obj11 = new GameObject(j1, 4 + i, 2, i2, j2, l1, k2, objDefinition.animID, true);
				obj12 = new GameObject(j1, j3, 2, i2, j2, l1, k2, objDefinition.animID, true);
			}
			sceneGraph.addWallObject(anIntArray152[i], ((Renderable) (obj11)), i3, j, byte1, i1, ((Renderable) (obj12)), l2,
					anIntArray152[j3], k1);
			if (objDefinition.isSolid)
				tileSetting.markWall(j, i, i1, k, objDefinition.isRangeable);
			return;
		}
		if (k == 3) {
			Object obj5;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null)
				obj5 = objDefinition.generateModel(3, i, l1, i2, j2, k2, -1);
			else
				obj5 = new GameObject(j1, i, 3, i2, j2, l1, k2, objDefinition.animID, true);
			sceneGraph.addWallObject(anIntArray140[i], ((Renderable) (obj5)), i3, j, byte1, i1, null, l2, 0, k1);
			if (objDefinition.isSolid)
				tileSetting.markWall(j, i, i1, k, objDefinition.isRangeable);
			return;
		}
		if (k == 9) {
			Object obj6;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null)
				obj6 = objDefinition.generateModel(k, i, l1, i2, j2, k2, -1);
			else
				obj6 = new GameObject(j1, i, k, i2, j2, l1, k2, objDefinition.animID, true);
			sceneGraph.addEntityB(i3, byte1, l2, 1, ((Renderable) (obj6)), 1, k1, 0, j, i1);
			if (objDefinition.isSolid)
				tileSetting.method212(objDefinition.isRangeable, objDefinition.width, objDefinition.height, i1, j, i);
			return;
		}
		if (objDefinition.aBoolean762)
			if (i == 1) {
				int k3 = k2;
				k2 = j2;
				j2 = i2;
				i2 = l1;
				l1 = k3;
			} else if (i == 2) {
				int l3 = k2;
				k2 = i2;
				i2 = l3;
				l3 = j2;
				j2 = l1;
				l1 = l3;
			} else if (i == 3) {
				int i4 = k2;
				k2 = l1;
				l1 = i2;
				i2 = j2;
				j2 = i4;
			}
		if (k == 4) {
			Object obj7;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null)
				obj7 = objDefinition.generateModel(4, 0, l1, i2, j2, k2, -1);
			else
				obj7 = new GameObject(j1, 0, 4, i2, j2, l1, k2, objDefinition.animID, true);
			sceneGraph.addWallDecoration(i3, j, i * 512, k1, 0, l2, ((Renderable) (obj7)), i1, byte1, 0, anIntArray152[i]);
			return;
		}
		if (k == 5) {
			int j4 = 16;
			int l4 = sceneGraph.getWallObjectUID(k1, i1, j);
			if (l4 > 0)
				j4 = ObjectDefinition.forID(l4 >> 14 & 0x7fff).anInt775;
			Object obj13;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null)
				obj13 = objDefinition.generateModel(4, 0, l1, i2, j2, k2, -1);
			else
				obj13 = new GameObject(j1, 0, 4, i2, j2, l1, k2, objDefinition.animID, true);
			sceneGraph.addWallDecoration(i3, j, i * 512, k1, anIntArray137[i] * j4, l2, ((Renderable) (obj13)), i1, byte1, anIntArray144[i]
					* j4, anIntArray152[i]);
			return;
		}
		if (k == 6) {
			Object obj8;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null)
				obj8 = objDefinition.generateModel(4, 0, l1, i2, j2, k2, -1);
			else
				obj8 = new GameObject(j1, 0, 4, i2, j2, l1, k2, objDefinition.animID, true);
			sceneGraph.addWallDecoration(i3, j, i, k1, 0, l2, ((Renderable) (obj8)), i1, byte1, 0, 256);
			return;
		}
		if (k == 7) {
			Object obj9;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null)
				obj9 = objDefinition.generateModel(4, 0, l1, i2, j2, k2, -1);
			else
				obj9 = new GameObject(j1, 0, 4, i2, j2, l1, k2, objDefinition.animID, true);
			sceneGraph.addWallDecoration(i3, j, i, k1, 0, l2, ((Renderable) (obj9)), i1, byte1, 0, 512);
			return;
		}
		if (k == 8) {
			Object obj10;
			if (objDefinition.animID == -1 && objDefinition.childrenIDs == null)
				obj10 = objDefinition.generateModel(4, 0, l1, i2, j2, k2, -1);
			else
				obj10 = new GameObject(j1, 0, 4, i2, j2, l1, k2, objDefinition.animID, true);
			sceneGraph.addWallDecoration(i3, j, i, k1, 0, l2, ((Renderable) (obj10)), i1, byte1, 0, 768);
		}
	}

	public static boolean isObjectBlockedCached(int i, byte[] data, int i_250_) {
		boolean fullyCached = true;
		RSBuffer buffer = new RSBuffer(data);
		int i_252_ = -1;
		for (;;) {
			int deltaId = buffer.readSmarts();
			if (deltaId == 0)
				break;
			i_252_ += deltaId;
			int i_254_ = 0;
			boolean isFound = false;
			for (;;) {
				if (isFound) {
					int deltaPos = buffer.readSmarts();
					if (deltaPos == 0)
						break;
					buffer.readUByte();
				} else {
					int i_257_ = buffer.readSmarts();
					if (i_257_ == 0)
						break;
					i_254_ += i_257_ - 1;
					int i_258_ = i_254_ & 0x3f;
					int i_259_ = i_254_ >> 6 & 0x3f;
					int i_260_ = buffer.readUByte() >> 2;
					int i_261_ = i_259_ + i;
					int i_262_ = i_258_ + i_250_;
					if (i_261_ > 0 && i_262_ > 0 && i_261_ < 103 && i_262_ < 103) {
						ObjectDefinition objDefinition = ObjectDefinition.forID(i_252_);
						if (i_260_ != 22 || !lowMem || objDefinition.hasActions || objDefinition.aBoolean736) {
							fullyCached &= objDefinition.method579();
							isFound = true;
						}
					}
				}
			}
		}
		return fullyCached;
	}

	public final void method190(int i, CollisionMap aclass11[], int j, SceneGraph sceneGraph, byte abyte0[]) {
		label0: {
			RSBuffer stream = new RSBuffer(abyte0);
			int l = -1;
			do {
				int i1 = stream.readSmarts();
				if (i1 == 0)
					break label0;
				l += i1;
				int j1 = 0;
				do {
					int k1 = stream.readSmarts();
					if (k1 == 0)
						break;
					j1 += k1 - 1;
					int l1 = j1 & 0x3f;
					int i2 = j1 >> 6 & 0x3f;
					int j2 = j1 >> 12;
					int k2 = stream.readUByte();
					int l2 = k2 >> 2; // TODO: type
					int i3 = k2 & 3; // TODO: Face
					int j3 = i2 + i;
					int k3 = l1 + j;
					if (j3 > 0 && k3 > 0 && j3 < 103 && k3 < 103) {
						int l3 = j2;
						if ((renderRuleFlags[1][j3][k3] & 2) == 2)
							l3--;
						CollisionMap class11 = null;
						if (l3 >= 0)
							class11 = aclass11[l3];
						addObject(k3, sceneGraph, class11, l2, j2, j3, l, i3);
					}
				} while (true);
			} while (true);
		}
	}

	public final int[] hueBuffer;
	public final int[] saturationBuffer;
	public final int[] lightnessBuffer;
	public final int[] hueDivider;
	public final int[] blendDirectionTracker;
	public final int[][][] vertexHeights;
	public final byte[][][] overlayFloorIds;
	static int anInt131;
	public final byte[][][] tileShadowIntensity;
	public final int[][][] tileCullingBitmap;
	public final byte[][][] overlayClippingPaths;
	public static final int anIntArray137[] = { 1, 0, -1, 0 };
	public static final int anInt138 = 323;
	public final int[][] tileLightnessIntensity;
	public static final int anIntArray140[] = { 16, 32, 64, 128 };
	public final byte[][][] underlayFloorIds;
	public static final int anIntArray144[] = { 0, -1, 0, 1 };
	static int lowestPlane = 99;
	public final int sizeX;
	public final int sizeY;
	public final byte[][][] overlayRotations;
	public final byte[][][] renderRuleFlags;
	static boolean lowMem = false;
	public static final int anIntArray152[] = { 1, 2, 4, 8 };
}
