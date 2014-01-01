final class SceneGraph {

	private static int visibleAreaWidth = 53;
	private static int visibleAreaHeight = 53;

	public SceneGraph(int height, int width, int length, int heightmap[][][], int heightmapUL[][][]) {
		interactiveObjectCache = new InteractiveObject[5000];
		anIntArray486 = new int[10000];
		anIntArray487 = new int[10000];
		zMapSize = height;
		xMapSize = width;
		yMapSize = length;
		tileArray = new Tile[height][width][length];
		anIntArrayArrayArray445 = new int[height][width + 1][length + 1];
		this.heightmap = heightmap;
		initToNull();
	}

	public static void resetCache() {
		interactableObjects = null;
		cullingClusterPointer = null;
		cullingClusters = null;
		aClass19_477 = null;
		TILE_VISIBILITY_MAPS = null;
		TILE_VISIBILITY_MAP = null;
	}

	public void initToNull() {
		for (int j = 0; j < zMapSize; j++) {
			for (int k = 0; k < xMapSize; k++) {
				for (int i1 = 0; i1 < yMapSize; i1++)
					tileArray[j][k][i1] = null;
			}
		}
		for (int l = 0; l < anInt472; l++) {
			for (int j1 = 0; j1 < cullingClusterPointer[l]; j1++)
				cullingClusters[l][j1] = null;
			cullingClusterPointer[l] = 0;
		}
		for (int k1 = 0; k1 < interactableObjectCacheCurrPos; k1++)
			interactiveObjectCache[k1] = null;
		interactableObjectCacheCurrPos = 0;
		for (int l1 = 0; l1 < interactableObjects.length; l1++)
			interactableObjects[l1] = null;
	}

	/**
	 * 
	 * @param z
	 */
	public void setHeightLevel(int z) {
		currentHeight = z;
		for (int k = 0; k < xMapSize; k++) {
			for (int l = 0; l < yMapSize; l++)
				if (tileArray[z][k][l] == null)
					tileArray[z][k][l] = new Tile(z, k, l);
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void applyBridgeMode(int x, int y) {
		Tile groundLevelTile = tileArray[0][y][x];
		for (int l = 0; l < 3; l++) {
			Tile tile = tileArray[l][y][x] = tileArray[l + 1][y][x];
			if (tile != null) {
				tile.tileZ--;
				for (int j1 = 0; j1 < tile.entityCount; j1++) {
					InteractiveObject worldEntity = tile.interactableObjects[j1];
					if ((worldEntity.uid >> 29 & 3) == 2 && worldEntity.tileLeft == y && worldEntity.tileTop == x)
						worldEntity.zPos--;
				}
			}
		}
		if (tileArray[0][y][x] == null)
			tileArray[0][y][x] = new Tile(0, y, x);
		tileArray[0][y][x].tile = groundLevelTile;
		tileArray[3][y][x] = null;
	}

	/**
	 * 
	 * @param z
	 * @param lowest_x
	 * @param lowest_y
	 * @param lowest_z
	 * @param highest_x
	 * @param highest_y
	 * @param highest_z
	 * @param searchMask
	 */
	public static void createCullingCluster(int z, int lowest_x, int lowest_y, int lowest_z, int highest_x, int highest_y, int highest_z,
			int search_mask) {
		CullingCluster culling_cluster = new CullingCluster();
		culling_cluster.tileStartX = lowest_x / 128;
		culling_cluster.tileEndX = highest_x / 128;
		culling_cluster.tileStartY = lowest_y / 128;
		culling_cluster.tileEndY = highest_y / 128;
		culling_cluster.searchMask = search_mask;
		culling_cluster.worldStartX = lowest_x;
		culling_cluster.worldEndX = highest_x;
		culling_cluster.worldStartY = lowest_y;
		culling_cluster.worldEndY = highest_y;
		culling_cluster.worldStartZ = highest_z;
		culling_cluster.worldEndZ = lowest_z;
		cullingClusters[z][cullingClusterPointer[z]++] = culling_cluster;
	}

	/**
	 * 
	 * @param z
	 * @param x
	 * @param y
	 * @param lowest_z
	 */
	public void setTileLogicHeight(int z, int x, int y, int lowest_z) {
		Tile tile = tileArray[z][x][y];
		if (tile != null) {
			tileArray[z][x][y].logicHeight = lowest_z;
		}
	}

	/**
	 * 
	 * @param zz
	 * @param x
	 * @param y
	 * @param shape
	 * @param rotation
	 * @param j1
	 * @param k1
	 * @param l1
	 * @param i2
	 * @param j2
	 * @param k2
	 * @param l2
	 * @param i3
	 * @param j3
	 * @param k3
	 * @param l3
	 * @param i4
	 * @param j4
	 * @param k4
	 * @param l4
	 */
	public void addTile(int zz, int x, int y, int shape, int rotation, int j1, int k1, int l1, int i2, int j2, int k2, int l2, int i3,
			int j3, int k3, int l3, int i4, int j4, int k4, int l4) {
		if (shape == 0) {
			PlainTile plainTile = new PlainTile(k2, l2, i3, j3, -1, k4, false);
			for (int heightLevel = zz; heightLevel >= 0; heightLevel--)
				if (tileArray[heightLevel][x][y] == null)
					tileArray[heightLevel][x][y] = new Tile(heightLevel, x, y);
			tileArray[zz][x][y].plainTile = plainTile;
			return;
		}
		if (shape == 1) {
			PlainTile plainTile_1 = new PlainTile(k3, l3, i4, j4, j1, l4, k1 == l1 && k1 == i2 && k1 == j2);
			for (int z = zz; z >= 0; z--)
				if (tileArray[z][x][y] == null)
					tileArray[z][x][y] = new Tile(z, x, y);
			tileArray[zz][x][y].plainTile = plainTile_1;
			return;
		}
		ShapedTile shapedTile = new ShapedTile(y, k3, j3, i2, j1, i4, rotation, k2, k4, i3, j2, l1, k1, shape, j4, l3, l2, x, l4);
		for (int k5 = zz; k5 >= 0; k5--)
			if (tileArray[k5][x][y] == null)
				tileArray[k5][x][y] = new Tile(k5, x, y);
		tileArray[zz][x][y].shapedTile = shapedTile;
	}

	/**
	 * 
	 * @param z
	 * @param z3d
	 * @param y
	 * @param cacheNode
	 * @param byte0
	 * @param uid
	 * @param x
	 */
	public void addGroundDecoration(int z, int z3d, int y, Renderable cacheNode, byte byte0, int uid, int x) {
		if (cacheNode == null)
			return;
		GroundDecoration groundDecoration = new GroundDecoration();
		groundDecoration.entity = cacheNode;
		groundDecoration.xPos = x * 128 + 64;
		groundDecoration.yPos = y * 128 + 64;
		groundDecoration.zPos = z3d;
		groundDecoration.uid = uid;
		groundDecoration.objectConfig = byte0;
		if (tileArray[z][x][y] == null)
			tileArray[z][x][y] = new Tile(z, x, y);
		tileArray[z][x][y].groundDecoration = groundDecoration;
	}

	/**
	 * 
	 * @param x
	 * @param uid
	 * @param secondGroundItem
	 * @param drawHeight
	 * @param thirdGroundItem
	 * @param firstGroundItem
	 * @param z
	 * @param y
	 */
	public void addGroundItemTile(int x, int uid, Renderable secondGroundItem, int drawHeight, Renderable thirdGroundItem,
			Renderable firstGroundItem, int z, int y) {
		GroundItemTile itemTile = new GroundItemTile();
		itemTile.firstGroundItem = firstGroundItem;
		itemTile.xPos = x * 128 + 64;
		itemTile.yPos = y * 128 + 64;
		itemTile.zPos = drawHeight;
		itemTile.uid = uid;
		itemTile.secondGroundItem = secondGroundItem;
		itemTile.thirdGroundItem = thirdGroundItem;
		int j1 = 0;
		Tile tile = tileArray[z][x][y];
		if (tile != null) {
			for (int k1 = 0; k1 < tile.entityCount; k1++)
				if (tile.interactableObjects[k1].jagexNode instanceof Model) {
					int l1 = ((Model) tile.interactableObjects[k1].jagexNode).anInt1654;
					if (l1 > j1)
						j1 = l1;
				}
		}
		itemTile.anInt52 = j1;
		if (tileArray[z][x][y] == null)
			tileArray[z][x][y] = new Tile(z, x, y);
		tileArray[z][x][y].groundItemTile = itemTile;
	}

	public void addWallObject(int i, Renderable cacheNode, int j, int y, byte byte0, int x, Renderable secondCacheNode, int z3d, int j1,
			int k1) {
		if (cacheNode == null && secondCacheNode == null)
			return;
		WallObject wallObj = new WallObject();
		wallObj.uid = j;
		wallObj.objectConfig = byte0;
		wallObj.xPos = x * 128 + 64;
		wallObj.yPos = y * 128 + 64;
		wallObj.zPos = z3d;
		wallObj.cacheNode = cacheNode;
		wallObj.node2 = secondCacheNode;
		wallObj.orientation = i;
		wallObj.orientation1 = j1;
		for (int l1 = k1; l1 >= 0; l1--)
			if (tileArray[l1][x][y] == null)
				tileArray[l1][x][y] = new Tile(l1, x, y);
		tileArray[k1][x][y].wallObject = wallObj;
	}

	public void addWallDecoration(int i, int tileY, int face, int tileZ, int x3dOffset, int z3d, Renderable cacheNode, int tileX,
			byte objectConfig, int y3dOffset, int facebits) {
		if (cacheNode == null)
			return;
		WallDecoration decoration = new WallDecoration();
		decoration.uid = i;
		decoration.objConfig = objectConfig;
		decoration.xPos = tileX * 128 + 64 + x3dOffset;
		decoration.yPos = tileY * 128 + 64 + y3dOffset;
		decoration.zPos = z3d;
		decoration.decorationNode = cacheNode;
		decoration.configBits = facebits;
		decoration.face = face;
		for (int k2 = tileZ; k2 >= 0; k2--)
			if (tileArray[k2][tileX][tileY] == null)
				tileArray[k2][tileX][tileY] = new Tile(k2, tileX, tileY);
		tileArray[tileZ][tileX][tileY].wallDecoration = decoration;
	}

	public boolean addEntityB(int i, byte byte0, int j, int k, Renderable entity, int l, int i1, int j1, int y, int x) {
		if (entity == null) {
			return true;
		} else {
			int xPos = x * 128 + 64 * l;
			int yPos = y * 128 + 64 * k;
			return addEntityC(i1, x, y, l, k, xPos, yPos, j, entity, j1, false, i, byte0);
		}
	}

	public boolean addRenderableA(int plane, int rotation, int drawheight, int l, int boundExtentY, int j1, int boundExtentX,
			Renderable npc, boolean flag) {
		if (npc == null)
			return true;
		int x = boundExtentX - j1;
		int y = boundExtentY - j1;
		int j2 = boundExtentX + j1;
		int k2 = boundExtentY + j1;
		if (flag) {
			if (rotation > 640 && rotation < 1408)
				k2 += 128;
			if (rotation > 1152 && rotation < 1920)
				j2 += 128;
			if (rotation > 1664 || rotation < 384)
				y -= 128;
			if (rotation > 128 && rotation < 896)
				x -= 128;
		}
		x /= 128;
		y /= 128;
		j2 /= 128;
		k2 /= 128;
		return addEntityC(plane, x, y, (j2 - x) + 1, (k2 - y) + 1, boundExtentX, boundExtentY, drawheight, npc, rotation, true, l, (byte) 0);
	}

	public boolean addEntity(int j, int k, Renderable entity, int l, int i1, int j1, int k1, int l1, int i2, int j2, int k2) {
		return entity == null || addEntityC(j, l1, k2, (i2 - l1) + 1, (i1 - k2) + 1, j1, k, k1, entity, l, true, j2, (byte) 0);
	}

	/**
	 * 
	 * @param z
	 * @param x
	 * @param y
	 * @param tileHeight
	 * @param tileWidth
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @param cacheNode
	 * @param rotation
	 * @param flag
	 * @param uid
	 * @param objConfig
	 * @return
	 */
	private boolean addEntityC(int z, int x, int y, int tileHeight, int tileWidth, int worldX, int worldY, int worldZ,
			Renderable cacheNode, int rotation, boolean flag, int uid, byte objConfig) {
		for (int k2 = x; k2 < x + tileHeight; k2++) {
			for (int l2 = y; l2 < y + tileWidth; l2++) {
				if (k2 < 0 || l2 < 0 || k2 >= xMapSize || l2 >= yMapSize)
					return false;
				Tile tile = tileArray[z][k2][l2];
				if (tile != null && tile.entityCount >= 5)
					return false;
			}
		}
		InteractiveObject interactableObj = new InteractiveObject();
		interactableObj.uid = uid;
		interactableObj.objectConfig = objConfig;
		interactableObj.zPos = z;
		interactableObj.worldX = worldX;
		interactableObj.worldY = worldY;
		interactableObj.worldZ = worldZ;
		interactableObj.jagexNode = cacheNode;
		interactableObj.rotation = rotation;
		interactableObj.tileLeft = x;
		interactableObj.tileTop = y;
		interactableObj.tileRight = (x + tileHeight) - 1;
		interactableObj.tileBottom = (y + tileWidth) - 1;
		for (int i3 = x; i3 < x + tileHeight; i3++) {
			for (int j3 = y; j3 < y + tileWidth; j3++) {
				int k3 = 0;
				if (i3 > x)
					k3++;
				if (i3 < (x + tileHeight) - 1)
					k3 += 4;
				if (j3 > y)
					k3 += 8;
				if (j3 < (y + tileWidth) - 1)
					k3 += 2;
				for (int l3 = z; l3 >= 0; l3--)
					if (tileArray[l3][i3][j3] == null)
						tileArray[l3][i3][j3] = new Tile(l3, i3, j3);
				Tile tile = tileArray[z][i3][j3];
				tile.interactableObjects[tile.entityCount] = interactableObj;
				tile.anIntArray1319[tile.entityCount] = k3;
				tile.anInt1320 |= k3;
				tile.entityCount++;
			}
		}
		if (flag)
			interactiveObjectCache[interactableObjectCacheCurrPos++] = interactableObj;
		return true;
	}

	public void clearInteractableObjectCache() {
		for (int i = 0; i < interactableObjectCacheCurrPos; i++) {
			InteractiveObject object = interactiveObjectCache[i];
			remove(object);
			interactiveObjectCache[i] = null;
		}
		interactableObjectCacheCurrPos = 0;
	}

	/**
	 * 
	 * @param interactiveObject
	 */
	private void remove(InteractiveObject interactiveObject) {
		for (int j = interactiveObject.tileLeft; j <= interactiveObject.tileRight; j++) {
			for (int k = interactiveObject.tileTop; k <= interactiveObject.tileBottom; k++) {
				Tile tile = tileArray[interactiveObject.zPos][j][k];
				if (tile != null) {
					for (int l = 0; l < tile.entityCount; l++) {
						if (tile.interactableObjects[l] != interactiveObject)
							continue;
						tile.entityCount--;
						for (int i1 = l; i1 < tile.entityCount; i1++) {
							tile.interactableObjects[i1] = tile.interactableObjects[i1 + 1];
							tile.anIntArray1319[i1] = tile.anIntArray1319[i1 + 1];
						}
						tile.interactableObjects[tile.entityCount] = null;
						break;
					}
					tile.anInt1320 = 0;
					for (int j1 = 0; j1 < tile.entityCount; j1++)
						tile.anInt1320 |= tile.anIntArray1319[j1];
				}
			}
		}
	}

	public void method290(int y, int k, int x, int z) {
		Tile tile = tileArray[z][x][y];
		if (tile == null)
			return;
		WallDecoration wallDecoration = tile.wallDecoration;
		if (wallDecoration != null) {
			int j1 = x * 128 + 64;
			int k1 = y * 128 + 64;
			wallDecoration.xPos = j1 + ((wallDecoration.xPos - j1) * k) / 16;
			wallDecoration.yPos = k1 + ((wallDecoration.yPos - k1) * k) / 16;
		}
	}

	public void removeWallObject(int x, int z, int y, byte byte0) {
		Tile tile = tileArray[z][x][y];
		if (byte0 != -119)
			aBoolean434 = !aBoolean434;
		if (tile != null) {
			tile.wallObject = null;
		}
	}

	public void removeWallDecoration(int y, int z, int x) {
		Tile tile = tileArray[z][x][y];
		if (tile != null) {
			tile.wallDecoration = null;
		}
	}

	public void method293(int i, int k, int l) {
		Tile tile = tileArray[i][k][l];
		if (tile == null)
			return;
		for (int j1 = 0; j1 < tile.entityCount; j1++) {
			InteractiveObject class28 = tile.interactableObjects[j1];
			if ((class28.uid >> 29 & 3) == 2 && class28.tileLeft == k && class28.tileTop == l) {
				remove(class28);
				return;
			}
		}
	}

	public void removeGroundDecoration(int z, int y, int x) {
		Tile tile = tileArray[z][x][y];
		if (tile == null)
			return;
		tile.groundDecoration = null;
	}

	public void removeGroundItemTile(int z, int x, int y) {
		Tile tile = tileArray[z][x][y];
		if (tile != null) {
			tile.groundItemTile = null;
		}
	}

	public WallObject getWallObject(int z, int x, int y) {
		Tile tile = tileArray[z][x][y];
		if (tile == null)
			return null;
		else
			return tile.wallObject;
	}

	public WallDecoration getWallDecoration(int x, int y, int z) {
		Tile tile = tileArray[z][x][y];
		if (tile == null)
			return null;
		else
			return tile.wallDecoration;
	}

	public InteractiveObject getInteractableObject(int x, int y, int z) {
		Tile tile = tileArray[z][x][y];
		if (tile == null)
			return null;
		for (int l = 0; l < tile.entityCount; l++) {
			InteractiveObject interactiveObject = tile.interactableObjects[l];
			if ((interactiveObject.uid >> 29 & 3) == 2 && interactiveObject.tileLeft == x && interactiveObject.tileTop == y)
				return interactiveObject;
		}
		return null;
	}

	public GroundDecoration getGroundDecoration(int i, int j, int k) {
		Tile tile = tileArray[k][j][i];
		if (tile == null || tile.groundDecoration == null)
			return null;
		else
			return tile.groundDecoration;
	}

	public int getWallObjectUID(int i, int j, int k) {
		Tile tile = tileArray[i][j][k];
		if (tile == null || tile.wallObject == null)
			return 0;
		else
			return tile.wallObject.uid;
	}

	public int getWallDecorationUID(int i, int j, int l) {
		Tile tile = tileArray[i][j][l];
		if (tile == null || tile.wallDecoration == null)
			return 0;
		else
			return tile.wallDecoration.uid;
	}

	public int getInteractiveObjectUID(int i, int j, int k) {
		Tile tile = tileArray[i][j][k];
		if (tile == null)
			return 0;
		for (int l = 0; l < tile.entityCount; l++) {
			InteractiveObject interactiveObj = tile.interactableObjects[l];
			if (interactiveObj.tileLeft == j && interactiveObj.tileTop == k)
				return interactiveObj.uid;
		}
		return 0;
	}

	public int getGroundDecortionUID(int i, int j, int k) {
		Tile tile = tileArray[i][j][k];
		if (tile == null || tile.groundDecoration == null)
			return 0;
		else
			return tile.groundDecoration.uid;
	}

	/**
	 * 
	 * @param z
	 * @param x
	 * @param y
	 * @param interactableObjectUID
	 * @return
	 */
	public int getTileArrayIdForPosition(int z, int x, int y, int interactableObjectUID) {
		Tile tile = tileArray[z][x][y];
		if (tile == null)
			return -1;
		if (tile.wallObject != null && tile.wallObject.uid == interactableObjectUID)
			return tile.wallObject.objectConfig & 0xff;
		if (tile.wallDecoration != null && tile.wallDecoration.uid == interactableObjectUID)
			return tile.wallDecoration.objConfig & 0xff;
		if (tile.groundDecoration != null && tile.groundDecoration.uid == interactableObjectUID)
			return tile.groundDecoration.objectConfig & 0xff;
		for (int i1 = 0; i1 < tile.entityCount; i1++)
			if (tile.interactableObjects[i1].uid == interactableObjectUID)
				return tile.interactableObjects[i1].objectConfig & 0xff;
		return -1;
	}

	/**
	 * TODO: Make better
	 * 
	 * @param l_x
	 * @param l_y
	 * @param l_z
	 * @param mag_multiplier
	 * @param lightness
	 */
	public void shadeModels(int lightness, int mag_multiplier, int l_x, int l_y, int l_z) {
		for (int _z = 0; _z < zMapSize; _z++) {
			for (int _x = 0; _x < xMapSize; _x++) {
				for (int _y = 0; _y < yMapSize; _y++) {
					Tile tile = tileArray[_z][_x][_y];
					if (tile != null) {
						WallObject wallObject = tile.wallObject;
						if (wallObject != null && wallObject.cacheNode != null && wallObject.cacheNode.vertexNormals != null) {
							method307(_z, 1, 1, _x, _y, (Model) wallObject.cacheNode);
							if (wallObject.node2 != null && wallObject.node2.vertexNormals != null) {
								method307(_z, 1, 1, _x, _y, (Model) wallObject.node2);
								method308((Model) wallObject.cacheNode, (Model) wallObject.node2, 0, 0, 0, false);
								((Model) wallObject.node2).light(lightness, mag_multiplier, l_x, l_y, l_z, true);
							}
							((Model) wallObject.cacheNode).light(lightness, mag_multiplier, l_x, l_y, l_z, true);
						}
						for (int k2 = 0; k2 < tile.entityCount; k2++) {
							InteractiveObject class28 = tile.interactableObjects[k2];
							if (class28 != null && class28.jagexNode != null && class28.jagexNode.vertexNormals != null) {
								method307(_z, (class28.tileRight - class28.tileLeft) + 1, (class28.tileBottom - class28.tileTop) + 1, _x,
										_y, (Model) class28.jagexNode);
								((Model) class28.jagexNode).light(lightness, mag_multiplier, l_x, l_y, l_z, true);
							}
						}
						GroundDecoration groundDecoration = tile.groundDecoration;
						if (groundDecoration != null && groundDecoration.entity.vertexNormals != null) {
							method306(_x, _z, (Model) groundDecoration.entity, _y);
							((Model) groundDecoration.entity).light(lightness, mag_multiplier, l_x, l_y, l_z, true);
						}
					}
				}
			}
		}
	}

	private void method306(int i, int j, Model model, int k) {
		if (i < xMapSize) {
			Tile tile = tileArray[j][i + 1][k];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.entity.vertexNormals != null)
				method308(model, (Model) tile.groundDecoration.entity, 128, 0, 0, true);
		}
		if (k < xMapSize) {
			Tile tile_1 = tileArray[j][i][k + 1];
			if (tile_1 != null && tile_1.groundDecoration != null && tile_1.groundDecoration.entity.vertexNormals != null)
				method308(model, (Model) tile_1.groundDecoration.entity, 0, 0, 128, true);
		}
		if (i < xMapSize && k < yMapSize) {
			Tile tile_2 = tileArray[j][i + 1][k + 1];
			if (tile_2 != null && tile_2.groundDecoration != null && tile_2.groundDecoration.entity.vertexNormals != null)
				method308(model, (Model) tile_2.groundDecoration.entity, 128, 0, 128, true);
		}
		if (i < xMapSize && k > 0) {
			Tile tile_3 = tileArray[j][i + 1][k - 1];
			if (tile_3 != null && tile_3.groundDecoration != null && tile_3.groundDecoration.entity.vertexNormals != null)
				method308(model, (Model) tile_3.groundDecoration.entity, 128, 0, -128, true);
		}
	}

	private void method307(int z, int j, int k, int x, int y, Model model) {
		// seems to 'snap' models togeather such as fences and walls nso they
		// dont look retarded

		boolean flag = true;
		int j1 = x;
		int k1 = x + j;
		int l1 = y - 1;
		int i2 = y + k;
		for (int j2 = z; j2 <= z + 1; j2++)
			if (j2 != zMapSize) {
				for (int k2 = j1; k2 <= k1; k2++)
					if (k2 >= 0 && k2 < xMapSize) {
						for (int l2 = l1; l2 <= i2; l2++)
							if (l2 >= 0 && l2 < yMapSize && (!flag || k2 >= k1 || l2 >= i2 || l2 < y && k2 != x)) {
								Tile tile = tileArray[j2][k2][l2];
								if (tile != null) {
									int i3 = (heightmap[j2][k2][l2] + heightmap[j2][k2 + 1][l2] + heightmap[j2][k2][l2 + 1] + heightmap[j2][k2 + 1][l2 + 1])
											/ 4
											- (heightmap[z][x][y] + heightmap[z][x + 1][y] + heightmap[z][x][y + 1] + heightmap[z][x + 1][y + 1])
											/ 4;
									WallObject wallObject = tile.wallObject;
									if (wallObject != null && wallObject.cacheNode != null && wallObject.cacheNode.vertexNormals != null)
										method308(model, (Model) wallObject.cacheNode, (k2 - x) * 128 + (1 - j) * 64, i3, (l2 - y) * 128
												+ (1 - k) * 64, flag);
									if (wallObject != null && wallObject.node2 != null && wallObject.node2.vertexNormals != null)
										method308(model, (Model) wallObject.node2, (k2 - x) * 128 + (1 - j) * 64, i3, (l2 - y) * 128
												+ (1 - k) * 64, flag);
									for (int j3 = 0; j3 < tile.entityCount; j3++) {
										InteractiveObject class28 = tile.interactableObjects[j3];
										if (class28 != null && class28.jagexNode != null && class28.jagexNode.vertexNormals != null) {
											int k3 = (class28.tileRight - class28.tileLeft) + 1;
											int l3 = (class28.tileBottom - class28.tileTop) + 1;
											method308(model, (Model) class28.jagexNode, (class28.tileLeft - x) * 128 + (k3 - j) * 64, i3,
													(class28.tileTop - y) * 128 + (l3 - k) * 64, flag);
										}
									}
								}
							}
					}
				j1--;
				flag = false;
			}
	}

	private void method308(Model model, Model model_1, int i, int j, int k, boolean flag) {
		anInt488++;
		int l = 0;
		short ai[] = model_1.vertexX;
		int i1 = model_1.vertexCount;
		for (int j1 = 0; j1 < model.vertexCount; j1++) {
			VertexNormal class33 = model.vertexNormals[j1];
			VertexNormal class33_1 = model.vertexNormalOffset[j1];
			if (class33_1.magnitude != 0) {
				int i2 = model.vertexY[j1] - j;
				if (i2 <= model_1.maxY) {
					int j2 = model.vertexX[j1] - i;
					if (j2 >= model_1.minX && j2 <= model_1.maxX) {
						int k2 = model.vertexZ[j1] - k;
						if (k2 >= model_1.minZ && k2 <= model_1.maxZ) {
							for (int l2 = 0; l2 < i1; l2++) {
								VertexNormal class33_2 = model_1.vertexNormals[l2];
								VertexNormal class33_3 = model_1.vertexNormalOffset[l2];
								if (j2 == ai[l2] && k2 == model_1.vertexZ[l2] && i2 == model_1.vertexY[l2] && class33_3.magnitude != 0) {
									class33.x += class33_3.x;
									class33.y += class33_3.y;
									class33.z += class33_3.z;
									class33.magnitude += class33_3.magnitude;
									class33_2.x += class33_1.x;
									class33_2.y += class33_1.y;
									class33_2.z += class33_1.z;
									class33_2.magnitude += class33_1.magnitude;
									l++;
									anIntArray486[j1] = anInt488;
									anIntArray487[l2] = anInt488;
								}
							}
						}
					}
				}
			}
		}
		if (l < 3 || !flag)
			return;
		for (int k1 = 0; k1 < model.triangleCount; k1++)
			if (anIntArray486[model.triangleA[k1]] == anInt488 && anIntArray486[model.triangleB[k1]] == anInt488
					&& anIntArray486[model.triangleC[k1]] == anInt488)
				model.triangleDrawType[k1] = -1;
		for (int l1 = 0; l1 < model_1.triangleCount; l1++)
			if (anIntArray487[model_1.triangleA[l1]] == anInt488 && anIntArray487[model_1.triangleB[l1]] == anInt488
					&& anIntArray487[model_1.triangleC[l1]] == anInt488)
				model_1.triangleDrawType[l1] = -1;
	}

	/**
	 * 
	 * @param pixels
	 * @param ptr
	 * @param z
	 * @param x
	 * @param y
	 */
	public void drawMinimapTile(int pixels[], int ptr, int z, int x, int y) {
		int scanLength = 512;
		Tile tile = tileArray[z][x][y];
		if (tile == null)
			return;
		PlainTile plainTile = tile.plainTile;
		if (plainTile != null) {
			int j1 = plainTile.anInt722;
			if (j1 == 0)
				return;
			for (int k1 = 0; k1 < 4; k1++) {
				pixels[ptr] = j1;
				pixels[ptr + 1] = j1;
				pixels[ptr + 2] = j1;
				pixels[ptr + 3] = j1;
				ptr += scanLength;
			}
			return;
		}
		ShapedTile shapedTile = tile.shapedTile;
		if (shapedTile == null)
			return;
		int l1 = shapedTile.anInt684;
		int i2 = shapedTile.anInt685;
		int j2 = shapedTile.anInt686;
		int k2 = shapedTile.anInt687;
		int ai1[] = tileShapePoints[l1];
		int ai2[] = tileShapeIndices[i2];
		int l2 = 0;
		if (j2 != 0) {
			for (int i3 = 0; i3 < 4; i3++) {
				pixels[ptr] = ai1[ai2[l2++]] != 0 ? k2 : j2;
				pixels[ptr + 1] = ai1[ai2[l2++]] != 0 ? k2 : j2;
				pixels[ptr + 2] = ai1[ai2[l2++]] != 0 ? k2 : j2;
				pixels[ptr + 3] = ai1[ai2[l2++]] != 0 ? k2 : j2;
				ptr += scanLength;
			}
			return;
		}
		for (int j3 = 0; j3 < 4; j3++) {
			if (ai1[ai2[l2++]] != 0)
				pixels[ptr] = k2;
			if (ai1[ai2[l2++]] != 0)
				pixels[ptr + 1] = k2;
			if (ai1[ai2[l2++]] != 0)
				pixels[ptr + 2] = k2;
			if (ai1[ai2[l2++]] != 0)
				pixels[ptr + 3] = k2;
			ptr += scanLength;
		}
	}

	/**
	 * 
	 * @param min_z
	 * @param max_z
	 * @param viewportWidth
	 * @param viewportHeight
	 * @param ai
	 */
	public static void initViewport(int min_z, int max_z, int viewportWidth, int viewportHeight, int[] ai) {
		left = 0;
		top = 0;
		right = viewportWidth;
		bottom = viewportHeight;
		midX = viewportWidth / 2;
		midY = viewportHeight / 2;

		boolean aflag[][][][] = new boolean[9][32][53][53];
		for (int i1 = 128; i1 <= 384; i1 += 32) {
			for (int j1 = 0; j1 < 2048; j1 += 64) {
				yCurveSine = Model.SINE[i1];
				yCurveCosine = Model.COSINE[i1];
				xCurveSine = Model.SINE[j1];
				xCurveCosine = Model.COSINE[j1];
				int l1 = (i1 - 128) / 32;
				int j2 = j1 / 64;
				for (int l2 = -26; l2 <= 26; l2++) {
					for (int j3 = -26; j3 <= 26; j3++) {
						int k3 = l2 * 128;
						int i4 = j3 * 128;
						boolean flag2 = false;
						for (int k4 = -min_z; k4 <= max_z; k4 += 128) {
							if (!isInViewingAngle(ai[l1] + k4, i4, k3))
								continue;
							flag2 = true;
							break;
						}
						aflag[l1][j2][l2 + 25 + 1][j3 + 25 + 1] = flag2;
					}
				}
			}
		}
		for (int k1 = 0; k1 < 8; k1++) {
			for (int i2 = 0; i2 < 32; i2++) {
				for (int k2 = -25; k2 < 25; k2++) {
					for (int i3 = -25; i3 < 25; i3++) {
						boolean flag1 = false;
						label0: for (int l3 = -1; l3 <= 1; l3++) {
							for (int j4 = -1; j4 <= 1; j4++) {
								if (aflag[k1][i2][k2 + l3 + 25 + 1][i3 + j4 + 25 + 1])
									flag1 = true;
								else if (aflag[k1][(i2 + 1) % 31][k2 + l3 + 25 + 1][i3 + j4 + 25 + 1])
									flag1 = true;
								else if (aflag[k1 + 1][i2][k2 + l3 + 25 + 1][i3 + j4 + 25 + 1]) {
									flag1 = true;
								} else {
									if (!aflag[k1 + 1][(i2 + 1) % 31][k2 + l3 + 25 + 1][i3 + j4 + 25 + 1])
										continue;
									flag1 = true;
								}
								break label0;
							}
						}
						TILE_VISIBILITY_MAPS[k1][i2][k2 + 25][i3 + 25] = flag1;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param z
	 * @param y
	 * @param x
	 * @return
	 */
	private static boolean isInViewingAngle(int z, int y, int x) {
		int camera_x = y * xCurveSine + x * xCurveCosine >> 16;
		int i1 = y * xCurveCosine - x * xCurveSine >> 16;
		int camera_z = z * yCurveSine + i1 * yCurveCosine >> 16;
		int camera_y = z * yCurveCosine - i1 * yCurveSine >> 16;
		if (camera_z < 50 || camera_z > 3500)
			return false;
		int log_view_dist = 9;
		int viewX = midX + (camera_x << log_view_dist) / camera_z;
		int viewY = midY + (camera_y << log_view_dist) / camera_z;
		return viewX >= left && viewX <= right && viewY >= top && viewY <= bottom;
	}

	public void request2DTrace(int i, int j) {
		isClicked = true;
		clickX = j;
		clickY = i;
		clickedTileX = -1;
		clickedTileY = -1;
	}

	public void render(int xCampos, int yCampos, int xCurve, int zCampos, int plane, int yCurve) {
		if (xCampos < 0)
			xCampos = 0;
		else if (xCampos >= xMapSize * 128)
			xCampos = xMapSize * 128 - 1;
		if (yCampos < 0)
			yCampos = 0;
		else if (yCampos >= yMapSize * 128)
			yCampos = yMapSize * 128 - 1;
		anInt448++;
		yCurveSine = Model.SINE[yCurve];
		yCurveCosine = Model.COSINE[yCurve];
		xCurveSine = Model.SINE[xCurve];
		xCurveCosine = Model.COSINE[xCurve];
		TILE_VISIBILITY_MAP = TILE_VISIBILITY_MAPS[(yCurve - 128) / 32][xCurve / 64];
		xCameraPosition = xCampos;
		zCameraPosition = zCampos;
		yCameraPosition = yCampos;
		xCameraPosTile = xCampos / 128;
		yCameraPosTile = yCampos / 128;
		SceneGraph.plane = plane;
		anInt449 = xCameraPosTile - 25;
		if (anInt449 < 0)
			anInt449 = 0;
		anInt451 = yCameraPosTile - 25;
		if (anInt451 < 0)
			anInt451 = 0;
		anInt450 = xCameraPosTile + 25;
		if (anInt450 > xMapSize)
			anInt450 = xMapSize;
		anInt452 = yCameraPosTile + 25;
		if (anInt452 > yMapSize)
			anInt452 = yMapSize;
		process_culling();
		anInt446 = 0;
		for (int z = currentHeight; z < zMapSize; z++) {
			Tile floorTiles[][] = tileArray[z];
			for (int x = anInt449; x < anInt450; x++) {
				for (int y = anInt451; y < anInt452; y++) {
					Tile singleTile = floorTiles[x][y];
					if (singleTile != null) {
						if (singleTile.logicHeight > plane || !TILE_VISIBILITY_MAP[(x - xCameraPosTile) + 25][(y - yCameraPosTile) + 25]
								&& heightmap[z][x][y] - zCampos < 2000) {
							singleTile.aBoolean1322 = false;
							singleTile.aBoolean1323 = false;
							singleTile.anInt1325 = 0;
						} else {
							singleTile.aBoolean1322 = true;
							singleTile.aBoolean1323 = true;
							singleTile.aBoolean1324 = singleTile.entityCount > 0;
							anInt446++;
						}
					}
				}
			}
		}
		for (int z = currentHeight; z < zMapSize; z++) {
			Tile floorTiles[][] = tileArray[z];
			for (int l2 = -25; l2 <= 0; l2++) {
				int x = xCameraPosTile + l2;
				int x2 = xCameraPosTile - l2;
				if (x >= anInt449 || x2 < anInt450) {
					for (int i4 = -25; i4 <= 0; i4++) {
						int y = yCameraPosTile + i4;
						int y2 = yCameraPosTile - i4;
						if (x >= anInt449) {
							if (y >= anInt451) {
								Tile class30_sub3_1 = floorTiles[x][y];
								if (class30_sub3_1 != null && class30_sub3_1.aBoolean1322)
									renderTile(class30_sub3_1, true);
							}
							if (y2 < anInt452) {
								Tile class30_sub3_2 = floorTiles[x][y2];
								if (class30_sub3_2 != null && class30_sub3_2.aBoolean1322)
									renderTile(class30_sub3_2, true);
							}
						}
						if (x2 < anInt450) {
							if (y >= anInt451) {
								Tile class30_sub3_3 = floorTiles[x2][y];
								if (class30_sub3_3 != null && class30_sub3_3.aBoolean1322)
									renderTile(class30_sub3_3, true);
							}
							if (y2 < anInt452) {
								Tile class30_sub3_4 = floorTiles[x2][y2];
								if (class30_sub3_4 != null && class30_sub3_4.aBoolean1322)
									renderTile(class30_sub3_4, true);
							}
						}
						if (anInt446 == 0) {
							isClicked = false;
							return;
						}
					}
				}
			}
		}
		for (int j2 = currentHeight; j2 < zMapSize; j2++) {
			Tile aclass30_sub3_2[][] = tileArray[j2];
			for (int j3 = -25; j3 <= 0; j3++) {
				int l3 = xCameraPosTile + j3;
				int j4 = xCameraPosTile - j3;
				if (l3 >= anInt449 || j4 < anInt450) {
					for (int l4 = -25; l4 <= 0; l4++) {
						int j5 = yCameraPosTile + l4;
						int k5 = yCameraPosTile - l4;
						if (l3 >= anInt449) {
							if (j5 >= anInt451) {
								Tile class30_sub3_5 = aclass30_sub3_2[l3][j5];
								if (class30_sub3_5 != null && class30_sub3_5.aBoolean1322)
									renderTile(class30_sub3_5, false);
							}
							if (k5 < anInt452) {
								Tile class30_sub3_6 = aclass30_sub3_2[l3][k5];
								if (class30_sub3_6 != null && class30_sub3_6.aBoolean1322)
									renderTile(class30_sub3_6, false);
							}
						}
						if (j4 < anInt450) {
							if (j5 >= anInt451) {
								Tile class30_sub3_7 = aclass30_sub3_2[j4][j5];
								if (class30_sub3_7 != null && class30_sub3_7.aBoolean1322)
									renderTile(class30_sub3_7, false);
							}
							if (k5 < anInt452) {
								Tile class30_sub3_8 = aclass30_sub3_2[j4][k5];
								if (class30_sub3_8 != null && class30_sub3_8.aBoolean1322)
									renderTile(class30_sub3_8, false);
							}
						}
						if (anInt446 == 0) {
							isClicked = false;
							return;
						}
					}
				}
			}
		}
		isClicked = false;
	}

	private void renderTile(Tile tile, boolean flag) {
		aClass19_477.insertHead(tile);
		do {
			Tile tile_1;
			do {
				tile_1 = (Tile) aClass19_477.popHead();
				if (tile_1 == null)
					return;
			} while (!tile_1.aBoolean1323);
			int i = tile_1.anInt1308;
			int j = tile_1.anInt1309;
			int k = tile_1.tileZ;
			int l = tile_1.anInt1310;
			Tile atile[][] = tileArray[k];
			if (tile_1.aBoolean1322) {
				if (flag) {
					if (k > 0) {
						Tile tile_2 = tileArray[k - 1][i][j];
						if (tile_2 != null && tile_2.aBoolean1323)
							continue;
					}
					if (i <= xCameraPosTile && i > anInt449) {
						Tile tile_3 = atile[i - 1][j];
						if (tile_3 != null && tile_3.aBoolean1323 && (tile_3.aBoolean1322 || (tile_1.anInt1320 & 1) == 0))
							continue;
					}
					if (i >= xCameraPosTile && i < anInt450 - 1) {
						Tile tile_4 = atile[i + 1][j];
						if (tile_4 != null && tile_4.aBoolean1323 && (tile_4.aBoolean1322 || (tile_1.anInt1320 & 4) == 0))
							continue;
					}
					if (j <= yCameraPosTile && j > anInt451) {
						Tile tile_5 = atile[i][j - 1];
						if (tile_5 != null && tile_5.aBoolean1323 && (tile_5.aBoolean1322 || (tile_1.anInt1320 & 8) == 0))
							continue;
					}
					if (j >= yCameraPosTile && j < anInt452 - 1) {
						Tile tile_6 = atile[i][j + 1];
						if (tile_6 != null && tile_6.aBoolean1323 && (tile_6.aBoolean1322 || (tile_1.anInt1320 & 2) == 0))
							continue;
					}
				} else {
					flag = true;
				}
				tile_1.aBoolean1322 = false;
				if (tile_1.tile != null) {
					Tile tile_7 = tile_1.tile;
					if (tile_7.plainTile != null) {
						if (!method320(0, i, j))
							render_plain_tile(tile_7.plainTile, 0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, i, j);
					} else if (tile_7.shapedTile != null && !method320(0, i, j))
						render_shaped_tile(i, yCurveSine, xCurveSine, tile_7.shapedTile, yCurveCosine, j, xCurveCosine);
					WallObject class10 = tile_7.wallObject;
					if (class10 != null)
						class10.cacheNode.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, class10.xPos - xCameraPosition,
								class10.zPos - zCameraPosition, class10.yPos - yCameraPosition, class10.uid);
					for (int i2 = 0; i2 < tile_7.entityCount; i2++) {
						InteractiveObject class28 = tile_7.interactableObjects[i2];
						if (class28 != null)
							class28.jagexNode.method443(class28.rotation, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine,
									class28.worldX - xCameraPosition, class28.worldZ - zCameraPosition, class28.worldY - yCameraPosition,
									class28.uid);
					}
				}
				boolean flag1 = false;
				if (tile_1.plainTile != null) {
					if (!method320(l, i, j)) {
						flag1 = true;
						render_plain_tile(tile_1.plainTile, l, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, i, j);
					}
				} else if (tile_1.shapedTile != null && !method320(l, i, j)) {
					flag1 = true;
					render_shaped_tile(i, yCurveSine, xCurveSine, tile_1.shapedTile, yCurveCosine, j, xCurveCosine);
				}
				int j1 = 0;
				int j2 = 0;
				WallObject class10_3 = tile_1.wallObject;
				WallDecoration wallDecoration_1 = tile_1.wallDecoration;
				if (class10_3 != null || wallDecoration_1 != null) {
					if (xCameraPosTile == i)
						j1++;
					else if (xCameraPosTile < i)
						j1 += 2;
					if (yCameraPosTile == j)
						j1 += 3;
					else if (yCameraPosTile > j)
						j1 += 6;
					j2 = anIntArray478[j1];
					tile_1.anInt1328 = (short) anIntArray480[j1];
				}
				if (class10_3 != null) {
					if ((class10_3.orientation & anIntArray479[j1]) != 0) {
						if (class10_3.orientation == 16) {
							tile_1.anInt1325 = 3;
							tile_1.anInt1326 = (short) anIntArray481[j1];
							tile_1.anInt1327 = (short) (3 - tile_1.anInt1326);
						} else if (class10_3.orientation == 32) {
							tile_1.anInt1325 = 6;
							tile_1.anInt1326 = (short) anIntArray482[j1];
							tile_1.anInt1327 = (short) (6 - tile_1.anInt1326);
						} else if (class10_3.orientation == 64) {
							tile_1.anInt1325 = 12;
							tile_1.anInt1326 = (short) anIntArray483[j1];
							tile_1.anInt1327 = (short) (12 - tile_1.anInt1326);
						} else {
							tile_1.anInt1325 = 9;
							tile_1.anInt1326 = (short) anIntArray484[j1];
							tile_1.anInt1327 = (short) (9 - tile_1.anInt1326);
						}
					} else {
						tile_1.anInt1325 = 0;
					}
					if ((class10_3.orientation & j2) != 0 && !method321(l, i, j, class10_3.orientation))
						class10_3.cacheNode.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, class10_3.xPos
								- xCameraPosition, class10_3.zPos - zCameraPosition, class10_3.yPos - yCameraPosition, class10_3.uid);
					if ((class10_3.orientation1 & j2) != 0 && !method321(l, i, j, class10_3.orientation1))
						class10_3.node2.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, class10_3.xPos - xCameraPosition,
								class10_3.zPos - zCameraPosition, class10_3.yPos - yCameraPosition, class10_3.uid);
				}
				if (wallDecoration_1 != null && !method322(l, i, j, wallDecoration_1.decorationNode.modelHeight))
					if ((wallDecoration_1.configBits & j2) != 0)
						wallDecoration_1.decorationNode.method443(wallDecoration_1.face, yCurveSine, yCurveCosine, xCurveSine,
								xCurveCosine, wallDecoration_1.xPos - xCameraPosition, wallDecoration_1.zPos - zCameraPosition,
								wallDecoration_1.yPos - yCameraPosition, wallDecoration_1.uid);
					else if ((wallDecoration_1.configBits & 0x300) != 0) {
						int j4 = wallDecoration_1.xPos - xCameraPosition;
						int l5 = wallDecoration_1.zPos - zCameraPosition;
						int k6 = wallDecoration_1.yPos - yCameraPosition;
						int i8 = wallDecoration_1.face;
						int k9;
						if (i8 == 1 || i8 == 2)
							k9 = -j4;
						else
							k9 = j4;
						int k10;
						if (i8 == 2 || i8 == 3)
							k10 = -k6;
						else
							k10 = k6;
						if ((wallDecoration_1.configBits & 0x100) != 0 && k10 < k9) {
							int i11 = j4 + anIntArray463[i8];
							int k11 = k6 + anIntArray464[i8];
							wallDecoration_1.decorationNode.method443(i8 * 512 + 256, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine,
									i11, l5, k11, wallDecoration_1.uid);
						}
						if ((wallDecoration_1.configBits & 0x200) != 0 && k10 > k9) {
							int j11 = j4 + anIntArray465[i8];
							int l11 = k6 + anIntArray466[i8];
							wallDecoration_1.decorationNode.method443(i8 * 512 + 1280 & 0x7ff, yCurveSine, yCurveCosine, xCurveSine,
									xCurveCosine, j11, l5, l11, wallDecoration_1.uid);
						}
					}
				if (flag1) {
					GroundDecoration groundDecoration = tile_1.groundDecoration;
					if (groundDecoration != null)
						groundDecoration.entity.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, groundDecoration.xPos
								- xCameraPosition, groundDecoration.zPos - zCameraPosition, groundDecoration.yPos - yCameraPosition,
								groundDecoration.uid);
					GroundItemTile object4_1 = tile_1.groundItemTile;
					if (object4_1 != null && object4_1.anInt52 == 0) {
						if (object4_1.secondGroundItem != null)
							object4_1.secondGroundItem.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, object4_1.xPos
									- xCameraPosition, object4_1.zPos - zCameraPosition, object4_1.yPos - yCameraPosition, object4_1.uid);
						if (object4_1.thirdGroundItem != null)
							object4_1.thirdGroundItem.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, object4_1.xPos
									- xCameraPosition, object4_1.zPos - zCameraPosition, object4_1.yPos - yCameraPosition, object4_1.uid);
						if (object4_1.firstGroundItem != null)
							object4_1.firstGroundItem.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, object4_1.xPos
									- xCameraPosition, object4_1.zPos - zCameraPosition, object4_1.yPos - yCameraPosition, object4_1.uid);
					}
				}
				int k4 = tile_1.anInt1320;
				if (k4 != 0) {
					if (i < xCameraPosTile && (k4 & 4) != 0) {
						Tile tile_17 = atile[i + 1][j];
						if (tile_17 != null && tile_17.aBoolean1323)
							aClass19_477.insertHead(tile_17);
					}
					if (j < yCameraPosTile && (k4 & 2) != 0) {
						Tile tile_18 = atile[i][j + 1];
						if (tile_18 != null && tile_18.aBoolean1323)
							aClass19_477.insertHead(tile_18);
					}
					if (i > xCameraPosTile && (k4 & 1) != 0) {
						Tile tile_19 = atile[i - 1][j];
						if (tile_19 != null && tile_19.aBoolean1323)
							aClass19_477.insertHead(tile_19);
					}
					if (j > yCameraPosTile && (k4 & 8) != 0) {
						Tile tile_20 = atile[i][j - 1];
						if (tile_20 != null && tile_20.aBoolean1323)
							aClass19_477.insertHead(tile_20);
					}
				}
			}
			if (tile_1.anInt1325 != 0) {
				boolean flag2 = true;
				for (int k1 = 0; k1 < tile_1.entityCount; k1++) {
					if (tile_1.interactableObjects[k1].anInt528 == anInt448
							|| (tile_1.anIntArray1319[k1] & tile_1.anInt1325) != tile_1.anInt1326)
						continue;
					flag2 = false;
					break;
				}
				if (flag2) {
					WallObject class10_1 = tile_1.wallObject;
					if (!method321(l, i, j, class10_1.orientation))
						class10_1.cacheNode.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, class10_1.xPos
								- xCameraPosition, class10_1.zPos - zCameraPosition, class10_1.yPos - yCameraPosition, class10_1.uid);
					tile_1.anInt1325 = 0;
				}
			}
			if (tile_1.aBoolean1324)
				try {
					int i1 = tile_1.entityCount;
					tile_1.aBoolean1324 = false;
					int l1 = 0;
					label0: for (int k2 = 0; k2 < i1; k2++) {
						InteractiveObject class28_1 = tile_1.interactableObjects[k2];
						if (class28_1.anInt528 == anInt448)
							continue;
						for (int k3 = class28_1.tileLeft; k3 <= class28_1.tileRight; k3++) {
							for (int l4 = class28_1.tileTop; l4 <= class28_1.tileBottom; l4++) {
								Tile tile_21 = atile[k3][l4];
								if (tile_21.aBoolean1322) {
									tile_1.aBoolean1324 = true;
								} else {
									if (tile_21.anInt1325 == 0)
										continue;
									int l6 = 0;
									if (k3 > class28_1.tileLeft)
										l6++;
									if (k3 < class28_1.tileRight)
										l6 += 4;
									if (l4 > class28_1.tileTop)
										l6 += 8;
									if (l4 < class28_1.tileBottom)
										l6 += 2;
									if ((l6 & tile_21.anInt1325) != tile_1.anInt1327)
										continue;
									tile_1.aBoolean1324 = true;
								}
								continue label0;
							}
						}
						interactableObjects[l1++] = class28_1;
						int i5 = xCameraPosTile - class28_1.tileLeft;
						int i6 = class28_1.tileRight - xCameraPosTile;
						if (i6 > i5)
							i5 = i6;
						int i7 = yCameraPosTile - class28_1.tileTop;
						int j8 = class28_1.tileBottom - yCameraPosTile;
						if (j8 > i7)
							class28_1.anInt527 = (i5 + j8);
						else
							class28_1.anInt527 = (i5 + i7);
					}
					while (l1 > 0) {
						int i3 = -50;
						int l3 = -1;
						for (int j5 = 0; j5 < l1; j5++) {
							InteractiveObject class28_2 = interactableObjects[j5];
							if (class28_2.anInt528 != anInt448)
								if (class28_2.anInt527 > i3) {
									i3 = class28_2.anInt527;
									l3 = j5;
								} else if (class28_2.anInt527 == i3) {
									int j7 = class28_2.worldX - xCameraPosition;
									int k8 = class28_2.worldY - yCameraPosition;
									int l9 = interactableObjects[l3].worldX - xCameraPosition;
									int l10 = interactableObjects[l3].worldY - yCameraPosition;
									if (j7 * j7 + k8 * k8 > l9 * l9 + l10 * l10)
										l3 = j5;
								}
						}
						if (l3 == -1)
							break;
						InteractiveObject class28_3 = interactableObjects[l3];
						class28_3.anInt528 = anInt448;
						if (!method323(l, class28_3.tileLeft, class28_3.tileRight, class28_3.tileTop, class28_3.tileBottom,
								class28_3.jagexNode.modelHeight))
							class28_3.jagexNode.method443(class28_3.rotation, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine,
									class28_3.worldX - xCameraPosition, class28_3.worldZ - zCameraPosition, class28_3.worldY
											- yCameraPosition, class28_3.uid);
						for (int k7 = class28_3.tileLeft; k7 <= class28_3.tileRight; k7++) {
							for (int l8 = class28_3.tileTop; l8 <= class28_3.tileBottom; l8++) {
								Tile tile_22 = atile[k7][l8];
								if (tile_22.anInt1325 != 0)
									aClass19_477.insertHead(tile_22);
								else if ((k7 != i || l8 != j) && tile_22.aBoolean1323)
									aClass19_477.insertHead(tile_22);
							}
						}
					}
					if (tile_1.aBoolean1324)
						continue;
				} catch (Exception _ex) {
					tile_1.aBoolean1324 = false;
				}
			if (!tile_1.aBoolean1323 || tile_1.anInt1325 != 0)
				continue;
			if (i <= xCameraPosTile && i > anInt449) {
				Tile tile_8 = atile[i - 1][j];
				if (tile_8 != null && tile_8.aBoolean1323)
					continue;
			}
			if (i >= xCameraPosTile && i < anInt450 - 1) {
				Tile tile_9 = atile[i + 1][j];
				if (tile_9 != null && tile_9.aBoolean1323)
					continue;
			}
			if (j <= yCameraPosTile && j > anInt451) {
				Tile tile_10 = atile[i][j - 1];
				if (tile_10 != null && tile_10.aBoolean1323)
					continue;
			}
			if (j >= yCameraPosTile && j < anInt452 - 1) {
				Tile tile_11 = atile[i][j + 1];
				if (tile_11 != null && tile_11.aBoolean1323)
					continue;
			}
			tile_1.aBoolean1323 = false;
			anInt446--;
			GroundItemTile object4 = tile_1.groundItemTile;
			if (object4 != null && object4.anInt52 != 0) {
				if (object4.secondGroundItem != null)
					object4.secondGroundItem.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, object4.xPos
							- xCameraPosition, object4.zPos - zCameraPosition - object4.anInt52, object4.yPos - yCameraPosition,
							object4.uid);
				if (object4.thirdGroundItem != null)
					object4.thirdGroundItem.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine,
							object4.xPos - xCameraPosition, object4.zPos - zCameraPosition - object4.anInt52, object4.yPos
									- yCameraPosition, object4.uid);
				if (object4.firstGroundItem != null)
					object4.firstGroundItem.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine,
							object4.xPos - xCameraPosition, object4.zPos - zCameraPosition - object4.anInt52, object4.yPos
									- yCameraPosition, object4.uid);
			}
			if (tile_1.anInt1328 != 0) {
				WallDecoration wallDecoration = tile_1.wallDecoration;
				if (wallDecoration != null && !method322(l, i, j, wallDecoration.decorationNode.modelHeight))
					if ((wallDecoration.configBits & tile_1.anInt1328) != 0)
						wallDecoration.decorationNode.method443(wallDecoration.face, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine,
								wallDecoration.xPos - xCameraPosition, wallDecoration.zPos - zCameraPosition, wallDecoration.yPos
										- yCameraPosition, wallDecoration.uid);
					else if ((wallDecoration.configBits & 0x300) != 0) {
						int l2 = wallDecoration.xPos - xCameraPosition;
						int j3 = wallDecoration.zPos - zCameraPosition;
						int i4 = wallDecoration.yPos - yCameraPosition;
						int k5 = wallDecoration.face;
						int j6;
						if (k5 == 1 || k5 == 2)
							j6 = -l2;
						else
							j6 = l2;
						int l7;
						if (k5 == 2 || k5 == 3)
							l7 = -i4;
						else
							l7 = i4;
						if ((wallDecoration.configBits & 0x100) != 0 && l7 >= j6) {
							int i9 = l2 + anIntArray463[k5];
							int i10 = i4 + anIntArray464[k5];
							wallDecoration.decorationNode.method443(k5 * 512 + 256, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, i9,
									j3, i10, wallDecoration.uid);
						}
						if ((wallDecoration.configBits & 0x200) != 0 && l7 <= j6) {
							int j9 = l2 + anIntArray465[k5];
							int j10 = i4 + anIntArray466[k5];
							wallDecoration.decorationNode.method443(k5 * 512 + 1280 & 0x7ff, yCurveSine, yCurveCosine, xCurveSine,
									xCurveCosine, j9, j3, j10, wallDecoration.uid);
						}
					}
				WallObject class10_2 = tile_1.wallObject;
				if (class10_2 != null) {
					if ((class10_2.orientation1 & tile_1.anInt1328) != 0 && !method321(l, i, j, class10_2.orientation1))
						class10_2.node2.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, class10_2.xPos - xCameraPosition,
								class10_2.zPos - zCameraPosition, class10_2.yPos - yCameraPosition, class10_2.uid);
					if ((class10_2.orientation & tile_1.anInt1328) != 0 && !method321(l, i, j, class10_2.orientation))
						class10_2.cacheNode.method443(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, class10_2.xPos
								- xCameraPosition, class10_2.zPos - zCameraPosition, class10_2.yPos - yCameraPosition, class10_2.uid);
				}
			}
			if (k < zMapSize - 1) {
				Tile tile_12 = tileArray[k + 1][i][j];
				if (tile_12 != null && tile_12.aBoolean1323)
					aClass19_477.insertHead(tile_12);
			}
			if (i < xCameraPosTile) {
				Tile tile_13 = atile[i + 1][j];
				if (tile_13 != null && tile_13.aBoolean1323)
					aClass19_477.insertHead(tile_13);
			}
			if (j < yCameraPosTile) {
				Tile tile_14 = atile[i][j + 1];
				if (tile_14 != null && tile_14.aBoolean1323)
					aClass19_477.insertHead(tile_14);
			}
			if (i > xCameraPosTile) {
				Tile tile_15 = atile[i - 1][j];
				if (tile_15 != null && tile_15.aBoolean1323)
					aClass19_477.insertHead(tile_15);
			}
			if (j > yCameraPosTile) {
				Tile tile_16 = atile[i][j - 1];
				if (tile_16 != null && tile_16.aBoolean1323)
					aClass19_477.insertHead(tile_16);
			}
		} while (true);
	}

	private void render_plain_tile(PlainTile plainTile, int i, int j, int k, int l, int i1, int j1, int k1) {
		try {
			int l1;
			int i2 = l1 = (j1 << 7) - xCameraPosition;
			int j2;
			int k2 = j2 = (k1 << 7) - yCameraPosition;
			int l2;
			int i3 = l2 = i2 + 128;
			int j3;
			int k3 = j3 = k2 + 128;
			int l3 = heightmap[i][j1][k1] - zCameraPosition;
			int i4 = heightmap[i][j1 + 1][k1] - zCameraPosition;
			int j4 = heightmap[i][j1 + 1][k1 + 1] - zCameraPosition;
			int k4 = heightmap[i][j1][k1 + 1] - zCameraPosition;
			int l4 = k2 * l + i2 * i1 >> 16;
			k2 = k2 * i1 - i2 * l >> 16;
			i2 = l4;
			l4 = l3 * k - k2 * j >> 16;
			k2 = l3 * j + k2 * k >> 16;
			l3 = l4;
			if (k2 < 50)
				return;
			l4 = j2 * l + i3 * i1 >> 16;
			j2 = j2 * i1 - i3 * l >> 16;
			i3 = l4;
			l4 = i4 * k - j2 * j >> 16;
			j2 = i4 * j + j2 * k >> 16;
			i4 = l4;
			if (j2 < 50)
				return;
			l4 = k3 * l + l2 * i1 >> 16;
			k3 = k3 * i1 - l2 * l >> 16;
			l2 = l4;
			l4 = j4 * k - k3 * j >> 16;
			k3 = j4 * j + k3 * k >> 16;
			j4 = l4;
			if (k3 < 50)
				return;
			l4 = j3 * l + l1 * i1 >> 16;
			j3 = j3 * i1 - l1 * l >> 16;
			l1 = l4;
			l4 = k4 * k - j3 * j >> 16;
			j3 = k4 * j + j3 * k >> 16;
			k4 = l4;
			if (j3 < 50)
				return;
			int i5 = Rasterizer.textureInt1 + (i2 << 9) / k2;
			int j5 = Rasterizer.textureInt2 + (l3 << 9) / k2;
			int k5 = Rasterizer.textureInt1 + (i3 << 9) / j2;
			int l5 = Rasterizer.textureInt2 + (i4 << 9) / j2;
			int i6 = Rasterizer.textureInt1 + (l2 << 9) / k3;
			int j6 = Rasterizer.textureInt2 + (j4 << 9) / k3;
			int k6 = Rasterizer.textureInt1 + (l1 << 9) / j3;
			int l6 = Rasterizer.textureInt2 + (k4 << 9) / j3;
			Rasterizer.alpha = 0;
			if ((i6 - k6) * (l5 - l6) - (j6 - l6) * (k5 - k6) > 0) {
				Rasterizer.restrict_edges = i6 < 0 || k6 < 0 || k5 < 0 || i6 > RSRaster.centerX || k6 > RSRaster.centerX
						|| k5 > RSRaster.centerX;
				if (isClicked && method318(clickX, clickY, j6, l6, l5, i6, k6, k5)) {
					clickedTileX = j1;
					clickedTileY = k1;
				}
				if (plainTile.anInt720 == -1) {
					if (plainTile.anInt718 != 0xbc614e)
						Rasterizer.method374(j6, l6, l5, i6, k6, k5, plainTile.anInt718, plainTile.anInt719, plainTile.anInt717, false);
				} else if (!lowMem) {
					if (plainTile.aBoolean721)
						Rasterizer.method378(j6, l6, l5, i6, k6, k5, plainTile.anInt718, plainTile.anInt719, plainTile.anInt717, i2, i3,
								l1, l3, i4, k4, k2, j2, j3, plainTile.anInt720);
					else
						Rasterizer.method378(j6, l6, l5, i6, k6, k5, plainTile.anInt718, plainTile.anInt719, plainTile.anInt717, l2, l1,
								i3, j4, k4, i4, k3, j3, j2, plainTile.anInt720);
				} else {
					int i7 = textureRGBColor[plainTile.anInt720];
					Rasterizer.method374(j6, l6, l5, i6, k6, k5, method317(i7, plainTile.anInt718), method317(i7, plainTile.anInt719),
							method317(i7, plainTile.anInt717), false);
				}
			}
			if ((i5 - k5) * (l6 - l5) - (j5 - l5) * (k6 - k5) > 0) {
				Rasterizer.restrict_edges = i5 < 0 || k5 < 0 || k6 < 0 || i5 > RSRaster.centerX || k5 > RSRaster.centerX
						|| k6 > RSRaster.centerX;
				if (isClicked && method318(clickX, clickY, j5, l5, l6, i5, k5, k6)) {
					clickedTileX = j1;
					clickedTileY = k1;
				}
				if (plainTile.anInt720 == -1) {
					if (plainTile.anInt716 != 0xbc614e) {
						Rasterizer.method374(j5, l5, l6, i5, k5, k6, plainTile.anInt716, plainTile.anInt717, plainTile.anInt719, false);
					}
				} else {
					if (!lowMem) {
						Rasterizer.method378(j5, l5, l6, i5, k5, k6, plainTile.anInt716, plainTile.anInt717, plainTile.anInt719, i2, i3,
								l1, l3, i4, k4, k2, j2, j3, plainTile.anInt720);
						return;
					}
					int j7 = textureRGBColor[plainTile.anInt720];
					Rasterizer.method374(j5, l5, l6, i5, k5, k6, method317(j7, plainTile.anInt716), method317(j7, plainTile.anInt717),
							method317(j7, plainTile.anInt719), false);
				}
			}
		} catch (Exception e) {
		}
	}

	private void render_shaped_tile(int i, int j, int k, ShapedTile shapedTile, int l, int i1, int j1) {
		int k1 = shapedTile.anIntArray673.length;
		for (int l1 = 0; l1 < k1; l1++) {
			int i2 = shapedTile.anIntArray673[l1] - xCameraPosition;
			int k2 = shapedTile.anIntArray674[l1] - zCameraPosition;
			int i3 = shapedTile.anIntArray675[l1] - yCameraPosition;
			int k3 = i3 * k + i2 * j1 >> 16;
			i3 = i3 * j1 - i2 * k >> 16;
			i2 = k3;
			k3 = k2 * l - i3 * j >> 16;
			i3 = k2 * j + i3 * l >> 16;
			k2 = k3;
			if (i3 < 50)
				return;
			if (shapedTile.anIntArray682 != null) {
				ShapedTile.anIntArray690[l1] = i2;
				ShapedTile.anIntArray691[l1] = k2;
				ShapedTile.anIntArray692[l1] = i3;
			}
			ShapedTile.anIntArray688[l1] = Rasterizer.textureInt1 + (i2 << 9) / i3;
			ShapedTile.anIntArray689[l1] = Rasterizer.textureInt2 + (k2 << 9) / i3;
		}
		Rasterizer.alpha = 0;
		k1 = shapedTile.anIntArray679.length;
		for (int j2 = 0; j2 < k1; j2++) {
			int l2 = shapedTile.anIntArray679[j2];
			int j3 = shapedTile.anIntArray680[j2];
			int l3 = shapedTile.anIntArray681[j2];
			int i4 = ShapedTile.anIntArray688[l2];
			int j4 = ShapedTile.anIntArray688[j3];
			int k4 = ShapedTile.anIntArray688[l3];
			int l4 = ShapedTile.anIntArray689[l2];
			int i5 = ShapedTile.anIntArray689[j3];
			int j5 = ShapedTile.anIntArray689[l3];
			if ((i4 - j4) * (j5 - i5) - (l4 - i5) * (k4 - j4) > 0) {
				Rasterizer.restrict_edges = i4 < 0 || j4 < 0 || k4 < 0 || i4 > RSRaster.centerX || j4 > RSRaster.centerX
						|| k4 > RSRaster.centerX;
				if (isClicked && method318(clickX, clickY, l4, i5, j5, i4, j4, k4)) {
					clickedTileX = i;
					clickedTileY = i1;
				}
				if (shapedTile.anIntArray682 == null || shapedTile.anIntArray682[j2] == -1) {
					if (shapedTile.anIntArray676[j2] != 0xbc614e)
						Rasterizer.method374(l4, i5, j5, i4, j4, k4, shapedTile.anIntArray676[j2], shapedTile.anIntArray677[j2],
								shapedTile.anIntArray678[j2], false);
				} else if (!lowMem) {
					if (shapedTile.aBoolean683)
						Rasterizer.method378(l4, i5, j5, i4, j4, k4, shapedTile.anIntArray676[j2], shapedTile.anIntArray677[j2],
								shapedTile.anIntArray678[j2], ShapedTile.anIntArray690[0], ShapedTile.anIntArray690[1],
								ShapedTile.anIntArray690[3], ShapedTile.anIntArray691[0], ShapedTile.anIntArray691[1],
								ShapedTile.anIntArray691[3], ShapedTile.anIntArray692[0], ShapedTile.anIntArray692[1],
								ShapedTile.anIntArray692[3], shapedTile.anIntArray682[j2]);
					else
						Rasterizer.method378(l4, i5, j5, i4, j4, k4, shapedTile.anIntArray676[j2], shapedTile.anIntArray677[j2],
								shapedTile.anIntArray678[j2], ShapedTile.anIntArray690[l2], ShapedTile.anIntArray690[j3],
								ShapedTile.anIntArray690[l3], ShapedTile.anIntArray691[l2], ShapedTile.anIntArray691[j3],
								ShapedTile.anIntArray691[l3], ShapedTile.anIntArray692[l2], ShapedTile.anIntArray692[j3],
								ShapedTile.anIntArray692[l3], shapedTile.anIntArray682[j2]);
				} else {
					int k5 = textureRGBColor[shapedTile.anIntArray682[j2]];
					Rasterizer.method374(l4, i5, j5, i4, j4, k4, method317(k5, shapedTile.anIntArray676[j2]),
							method317(k5, shapedTile.anIntArray677[j2]), method317(k5, shapedTile.anIntArray678[j2]), false);
				}
			}
		}
	}

	private int method317(int j, int k) {
		k = 127 - k;
		k = (k * (j & 0x7f)) / 160;
		if (k < 2)
			k = 2;
		else if (k > 126)
			k = 126;
		return (j & 0xff80) + k;
	}

	private boolean method318(int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
		if (j < k && j < l && j < i1)
			return false;
		if (j > k && j > l && j > i1)
			return false;
		if (i < j1 && i < k1 && i < l1)
			return false;
		if (i > j1 && i > k1 && i > l1)
			return false;
		int i2 = (j - k) * (k1 - j1) - (i - j1) * (l - k);
		int j2 = (j - i1) * (j1 - l1) - (i - l1) * (k - i1);
		int k2 = (j - l) * (l1 - k1) - (i - k1) * (i1 - l);
		return i2 * k2 > 0 && k2 * j2 > 0;
	}

	private void process_culling() {
		int cluster_count = cullingClusterPointer[plane];
		CullingCluster clusters[] = cullingClusters[plane];
		processed_culling_clusters_ptr = 0;
		for (int ptr = 0; ptr < cluster_count; ptr++) {
			CullingCluster cluster = clusters[ptr];
			if (cluster.searchMask == 1) {
				int x_dist_from_camera_start = (cluster.tileStartX - xCameraPosTile) + (((visibleAreaWidth - 1) / 2) - 1);
				if (x_dist_from_camera_start < 0 || x_dist_from_camera_start > visibleAreaWidth - 3)
					continue;
				int y_dist_from_camera_start = (cluster.tileStartY - yCameraPosTile) + (((visibleAreaHeight - 1) / 2) - 1);
				if (y_dist_from_camera_start < 0)
					y_dist_from_camera_start = 0;
				int y_dist_from_camera_end = (cluster.tileEndY - yCameraPosTile) + (((visibleAreaHeight - 1) / 2) - 1);
				if (y_dist_from_camera_end > visibleAreaHeight - 3)
					y_dist_from_camera_end = visibleAreaHeight - 3;
				boolean is_visible = false;
				while (y_dist_from_camera_start <= y_dist_from_camera_end)
					if (TILE_VISIBILITY_MAP[x_dist_from_camera_start][y_dist_from_camera_start++]) {
						is_visible = true;
						break;
					}
				if (!is_visible)
					continue;
				int x_dist_from_camera_start_real = xCameraPosition - cluster.worldStartX;
				if (x_dist_from_camera_start_real > 32) {
					cluster.tileDistanceEnum = 1;
				} else {
					if (x_dist_from_camera_start_real >= -32)
						continue;
					cluster.tileDistanceEnum = 2;
					x_dist_from_camera_start_real = -x_dist_from_camera_start_real;
				}
				cluster.worldDistanceFromCameraStartY = (cluster.worldStartY - yCameraPosition << 8) / x_dist_from_camera_start_real;
				cluster.worldDistanceFromCameraEndY = (cluster.worldEndY - yCameraPosition << 8) / x_dist_from_camera_start_real;
				cluster.worldDistanceFromCameraStartZ = (cluster.worldStartZ - zCameraPosition << 8) / x_dist_from_camera_start_real;
				cluster.worldDistanceFromCameraEndZ = (cluster.worldEndZ - zCameraPosition << 8) / x_dist_from_camera_start_real;
				processed_culling_clusters[processed_culling_clusters_ptr++] = cluster;
				continue;
			}
			if (cluster.searchMask == 2) {
				int y_dist_from_camera_start = (cluster.tileStartY - yCameraPosTile) + (((visibleAreaHeight - 1) / 2) - 1);
				if (y_dist_from_camera_start < 0 || y_dist_from_camera_start > visibleAreaHeight - 3)
					continue;
				int x_dist_from_camera_start = (cluster.tileStartX - xCameraPosTile) + (((visibleAreaWidth - 1) / 2) - 1);
				if (x_dist_from_camera_start < 0)
					x_dist_from_camera_start = 0;
				int x_dist_from_camera_end = (cluster.tileEndX - xCameraPosTile) + (((visibleAreaWidth - 1) / 2) - 1);
				if (x_dist_from_camera_end > visibleAreaWidth - 3)
					x_dist_from_camera_end = visibleAreaWidth - 3;
				boolean is_visible = false;
				while (x_dist_from_camera_start <= x_dist_from_camera_end)
					if (TILE_VISIBILITY_MAP[x_dist_from_camera_start++][y_dist_from_camera_start]) {
						is_visible = true;
						break;
					}
				if (!is_visible)
					continue;
				int y_dist_from_camera_start_real = yCameraPosition - cluster.worldStartY;
				if (y_dist_from_camera_start_real > 32) {
					cluster.tileDistanceEnum = 3;
				} else {
					if (y_dist_from_camera_start_real >= -32)
						continue;
					cluster.tileDistanceEnum = 4;
					y_dist_from_camera_start_real = -y_dist_from_camera_start_real;
				}
				cluster.worldDistanceFromCameraStartX = (cluster.worldStartX - xCameraPosition << 8) / y_dist_from_camera_start_real;
				cluster.worldDistanceFromCameraEndX = (cluster.worldEndX - xCameraPosition << 8) / y_dist_from_camera_start_real;
				cluster.worldDistanceFromCameraStartZ = (cluster.worldStartZ - zCameraPosition << 8) / y_dist_from_camera_start_real;
				cluster.worldDistanceFromCameraEndZ = (cluster.worldEndZ - zCameraPosition << 8) / y_dist_from_camera_start_real;
				processed_culling_clusters[processed_culling_clusters_ptr++] = cluster;
			} else if (cluster.searchMask == 4) {
				int z_dist_from_camera_start_real = cluster.worldStartZ - zCameraPosition;
				if (z_dist_from_camera_start_real > 128) {
					int y_dist_from_camera_start = (cluster.tileStartY - yCameraPosTile) + (((visibleAreaHeight - 1) / 2) - 1);
					if (y_dist_from_camera_start < 0)
						y_dist_from_camera_start = 0;
					int y_dist_from_camera_end = (cluster.tileEndY - yCameraPosTile) + (((visibleAreaHeight - 1) / 2) - 1);
					if (y_dist_from_camera_end > visibleAreaHeight - 3)
						y_dist_from_camera_end = visibleAreaHeight - 3;
					if (y_dist_from_camera_start <= y_dist_from_camera_end) {
						int x_dist_from_camera_start = (cluster.tileStartX - xCameraPosTile) + (((visibleAreaWidth - 1) / 2) - 1);
						if (x_dist_from_camera_start < 0)
							x_dist_from_camera_start = 0;
						int x_dist_from_camera_end = (cluster.tileEndX - xCameraPosTile) + (((visibleAreaWidth - 1) / 2) - 1);
						if (x_dist_from_camera_end > visibleAreaWidth - 3)
							x_dist_from_camera_end = visibleAreaWidth - 3;
						boolean is_visible = false;
						for_outer: for (int __x = x_dist_from_camera_start; __x <= x_dist_from_camera_end; __x++) {
							for (int __y = y_dist_from_camera_start; __y <= y_dist_from_camera_end; __y++) {
								if (!TILE_VISIBILITY_MAP[__x][__y])
									continue;
								is_visible = true;
								break for_outer;
							}
						}
						if (is_visible) {
							cluster.tileDistanceEnum = 5;
							cluster.worldDistanceFromCameraStartX = (cluster.worldStartX - xCameraPosition << 8)
									/ z_dist_from_camera_start_real;
							cluster.worldDistanceFromCameraEndX = (cluster.worldEndX - xCameraPosition << 8)
									/ z_dist_from_camera_start_real;
							cluster.worldDistanceFromCameraStartY = (cluster.worldStartY - yCameraPosition << 8)
									/ z_dist_from_camera_start_real;
							cluster.worldDistanceFromCameraEndY = (cluster.worldEndY - yCameraPosition << 8)
									/ z_dist_from_camera_start_real;
							processed_culling_clusters[processed_culling_clusters_ptr++] = cluster;
						}
					}
				}
			}
		}
	}

	private boolean method320(int i, int j, int k) {
		int l = anIntArrayArrayArray445[i][j][k];
		if (l == -anInt448)
			return false;
		if (l == anInt448)
			return true;
		int i1 = j << 7;
		int j1 = k << 7;
		if (method324(i1 + 1, heightmap[i][j][k], j1 + 1) && method324((i1 + 128) - 1, heightmap[i][j + 1][k], j1 + 1)
				&& method324((i1 + 128) - 1, heightmap[i][j + 1][k + 1], (j1 + 128) - 1)
				&& method324(i1 + 1, heightmap[i][j][k + 1], (j1 + 128) - 1)) {
			anIntArrayArrayArray445[i][j][k] = anInt448;
			return true;
		} else {
			anIntArrayArrayArray445[i][j][k] = -anInt448;
			return false;
		}
	}

	private boolean method321(int i, int j, int k, int l) {
		if (!method320(i, j, k))
			return false;
		int i1 = j << 7;
		int j1 = k << 7;
		int k1 = heightmap[i][j][k] - 1;
		int l1 = k1 - 120;
		int i2 = k1 - 230;
		int j2 = k1 - 238;
		if (l < 16) {
			if (l == 1) {
				if (i1 > xCameraPosition) {
					if (!method324(i1, k1, j1))
						return false;
					if (!method324(i1, k1, j1 + 128))
						return false;
				}
				if (i > 0) {
					if (!method324(i1, l1, j1))
						return false;
					if (!method324(i1, l1, j1 + 128))
						return false;
				}
				return method324(i1, i2, j1) && method324(i1, i2, j1 + 128);
			}
			if (l == 2) {
				if (j1 < yCameraPosition) {
					if (!method324(i1, k1, j1 + 128))
						return false;
					if (!method324(i1 + 128, k1, j1 + 128))
						return false;
				}
				if (i > 0) {
					if (!method324(i1, l1, j1 + 128))
						return false;
					if (!method324(i1 + 128, l1, j1 + 128))
						return false;
				}
				return method324(i1, i2, j1 + 128) && method324(i1 + 128, i2, j1 + 128);
			}
			if (l == 4) {
				if (i1 < xCameraPosition) {
					if (!method324(i1 + 128, k1, j1))
						return false;
					if (!method324(i1 + 128, k1, j1 + 128))
						return false;
				}
				if (i > 0) {
					if (!method324(i1 + 128, l1, j1))
						return false;
					if (!method324(i1 + 128, l1, j1 + 128))
						return false;
				}
				return method324(i1 + 128, i2, j1) && method324(i1 + 128, i2, j1 + 128);
			}
			if (l == 8) {
				if (j1 > yCameraPosition) {
					if (!method324(i1, k1, j1))
						return false;
					if (!method324(i1 + 128, k1, j1))
						return false;
				}
				if (i > 0) {
					if (!method324(i1, l1, j1))
						return false;
					if (!method324(i1 + 128, l1, j1))
						return false;
				}
				return method324(i1, i2, j1) && method324(i1 + 128, i2, j1);
			}
		}
		if (!method324(i1 + 64, j2, j1 + 64))
			return false;
		if (l == 16)
			return method324(i1, i2, j1 + 128);
		if (l == 32)
			return method324(i1 + 128, i2, j1 + 128);
		if (l == 64)
			return method324(i1 + 128, i2, j1);
		if (l == 128) {
			return method324(i1, i2, j1);
		} else {
			System.out.println("Warning unsupported wall type");
			return true;
		}
	}

	private boolean method322(int i, int j, int k, int l) {
		if (!method320(i, j, k))
			return false;
		int i1 = j << 7;
		int j1 = k << 7;
		return method324(i1 + 1, heightmap[i][j][k] - l, j1 + 1) && method324((i1 + 128) - 1, heightmap[i][j + 1][k] - l, j1 + 1)
				&& method324((i1 + 128) - 1, heightmap[i][j + 1][k + 1] - l, (j1 + 128) - 1)
				&& method324(i1 + 1, heightmap[i][j][k + 1] - l, (j1 + 128) - 1);
	}

	private boolean method323(int i, int j, int k, int l, int i1, int j1) {
		if (j == k && l == i1) {
			if (!method320(i, j, l))
				return false;
			int k1 = j << 7;
			int i2 = l << 7;
			return method324(k1 + 1, heightmap[i][j][l] - j1, i2 + 1) && method324((k1 + 128) - 1, heightmap[i][j + 1][l] - j1, i2 + 1)
					&& method324((k1 + 128) - 1, heightmap[i][j + 1][l + 1] - j1, (i2 + 128) - 1)
					&& method324(k1 + 1, heightmap[i][j][l + 1] - j1, (i2 + 128) - 1);
		}
		for (int l1 = j; l1 <= k; l1++) {
			for (int j2 = l; j2 <= i1; j2++)
				if (anIntArrayArrayArray445[i][l1][j2] == -anInt448)
					return false;
		}
		int k2 = (j << 7) + 1;
		int l2 = (l << 7) + 2;
		int i3 = heightmap[i][j][l] - j1;
		if (!method324(k2, i3, l2))
			return false;
		int j3 = (k << 7) - 1;
		if (!method324(j3, i3, l2))
			return false;
		int k3 = (i1 << 7) - 1;
		return method324(k2, i3, k3) && method324(j3, i3, k3);
	}

	// True for culled tiles?
	private boolean method324(int i, int j, int k) {
		for (int l = 0; l < processed_culling_clusters_ptr; l++) {
			CullingCluster class47 = processed_culling_clusters[l];
			if (class47.tileDistanceEnum == 1) {
				int i1 = class47.worldStartX - i;
				if (i1 > 0) {
					int j2 = class47.worldEndZ + (class47.worldDistanceFromCameraStartY * i1 >> 8);
					int k3 = class47.worldEndX + (class47.worldDistanceFromCameraEndY * i1 >> 8);
					int l4 = class47.worldEndY + (class47.worldDistanceFromCameraStartZ * i1 >> 8);
					int i6 = class47.worldStartY + (class47.worldDistanceFromCameraEndZ * i1 >> 8);
					if (k >= j2 && k <= k3 && j >= l4 && j <= i6)
						return true;
				}
			} else if (class47.tileDistanceEnum == 2) {
				int j1 = i - class47.worldStartX;
				if (j1 > 0) {
					int k2 = class47.worldEndZ + (class47.worldDistanceFromCameraStartY * j1 >> 8);
					int l3 = class47.worldEndX + (class47.worldDistanceFromCameraEndY * j1 >> 8);
					int i5 = class47.worldEndY + (class47.worldDistanceFromCameraStartZ * j1 >> 8);
					int j6 = class47.worldStartY + (class47.worldDistanceFromCameraEndZ * j1 >> 8);
					if (k >= k2 && k <= l3 && j >= i5 && j <= j6)
						return true;
				}
			} else if (class47.tileDistanceEnum == 3) {
				int k1 = class47.worldEndZ - k;
				if (k1 > 0) {
					int l2 = class47.worldStartX + (class47.worldDistanceFromCameraStartX * k1 >> 8);
					int i4 = class47.worldStartZ + (class47.worldDistanceFromCameraEndX * k1 >> 8);
					int j5 = class47.worldEndY + (class47.worldDistanceFromCameraStartZ * k1 >> 8);
					int k6 = class47.worldStartY + (class47.worldDistanceFromCameraEndZ * k1 >> 8);
					if (i >= l2 && i <= i4 && j >= j5 && j <= k6)
						return true;
				}
			} else if (class47.tileDistanceEnum == 4) {
				int l1 = k - class47.worldEndZ;
				if (l1 > 0) {
					int i3 = class47.worldStartX + (class47.worldDistanceFromCameraStartX * l1 >> 8);
					int j4 = class47.worldStartZ + (class47.worldDistanceFromCameraEndX * l1 >> 8);
					int k5 = class47.worldEndY + (class47.worldDistanceFromCameraStartZ * l1 >> 8);
					int l6 = class47.worldStartY + (class47.worldDistanceFromCameraEndZ * l1 >> 8);
					if (i >= i3 && i <= j4 && j >= k5 && j <= l6)
						return true;
				}
			} else if (class47.tileDistanceEnum == 5) {
				int i2 = j - class47.worldEndY;
				if (i2 > 0) {
					int j3 = class47.worldStartX + (class47.worldDistanceFromCameraStartX * i2 >> 8);
					int k4 = class47.worldStartZ + (class47.worldDistanceFromCameraEndX * i2 >> 8);
					int l5 = class47.worldEndZ + (class47.worldDistanceFromCameraStartY * i2 >> 8);
					int i7 = class47.worldEndX + (class47.worldDistanceFromCameraEndY * i2 >> 8);
					if (i >= j3 && i <= k4 && k >= l5 && k <= i7)
						return true;
				}
			}
		}
		return false;
	}

	private boolean aBoolean434;
	public static boolean lowMem = false;
	private final int zMapSize;
	private final int xMapSize;
	private final int yMapSize;
	private final int[][][] heightmap;
	private final Tile[][][] tileArray;
	private int currentHeight;
	private int interactableObjectCacheCurrPos;
	private final InteractiveObject[] interactiveObjectCache;
	private final int[][][] anIntArrayArrayArray445;
	private static int anInt446;
	private static int plane;
	private static int anInt448;
	private static int anInt449;
	private static int anInt450;
	private static int anInt451;
	private static int anInt452;
	private static int xCameraPosTile;
	private static int yCameraPosTile;
	private static int xCameraPosition;
	private static int zCameraPosition;
	private static int yCameraPosition;
	private static int yCurveSine;
	private static int yCurveCosine;
	private static int xCurveSine;
	private static int xCurveCosine;
	private static InteractiveObject[] interactableObjects = new InteractiveObject[100];
	private static final int[] anIntArray463 = { 53, -53, -53, 53 };
	private static final int[] anIntArray464 = { -53, -53, 53, 53 };
	private static final int[] anIntArray465 = { -45, 45, 45, -45 };
	private static final int[] anIntArray466 = { 45, 45, -45, -45 };
	private static boolean isClicked;
	private static int clickX;
	private static int clickY;
	public static int clickedTileX = -1;
	public static int clickedTileY = -1;
	private static final int anInt472;
	private static int[] cullingClusterPointer;
	private static CullingCluster[][] cullingClusters;
	private static int processed_culling_clusters_ptr;
	private static final CullingCluster[] processed_culling_clusters = new CullingCluster[500];
	private static Deque aClass19_477 = new Deque();
	private static final int[] anIntArray478 = { 19, 55, 38, 155, 255, 110, 137, 205, 76 };
	private static final int[] anIntArray479 = { 160, 192, 80, 96, 0, 144, 80, 48, 160 };
	private static final int[] anIntArray480 = { 76, 8, 137, 4, 0, 1, 38, 2, 19 };
	private static final int[] anIntArray481 = { 0, 0, 2, 0, 0, 2, 1, 1, 0 };
	private static final int[] anIntArray482 = { 2, 0, 0, 2, 0, 0, 0, 4, 4 };
	private static final int[] anIntArray483 = { 0, 4, 4, 8, 0, 0, 8, 0, 0 };
	private static final int[] anIntArray484 = { 1, 1, 0, 0, 0, 8, 0, 0, 8 };
	private static final int[] textureRGBColor = { 41, 39248, 41, 4643, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 43086, 41, 41, 41, 41,
			41, 41, 41, 8602, 41, 28992, 41, 41, 41, 41, 41, 5056, 41, 41, 41, 7079, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 3131, 41, 41,
			41 };
	private final int[] anIntArray486;
	private final int[] anIntArray487;
	private int anInt488;
	private final int[][] tileShapePoints = { new int[16], { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1 }, { 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 },
			{ 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0 }, { 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1 } };
	private final int[][] tileShapeIndices = { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
			{ 12, 8, 4, 0, 13, 9, 5, 1, 14, 10, 6, 2, 15, 11, 7, 3 }, { 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 },
			{ 3, 7, 11, 15, 2, 6, 10, 14, 1, 5, 9, 13, 0, 4, 8, 12 } };
	private static boolean[][][][] TILE_VISIBILITY_MAPS = new boolean[8][32][51][51];
	private static boolean[][] TILE_VISIBILITY_MAP;
	private static int midX;
	private static int midY;
	private static int left;
	private static int top;
	private static int right;
	private static int bottom;
	static {
		anInt472 = 4;
		cullingClusterPointer = new int[anInt472];
		cullingClusters = new CullingCluster[anInt472][500];
	}
}
