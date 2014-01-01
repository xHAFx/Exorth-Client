final class CollisionMap {

	public CollisionMap() {
		insetX = 0;
		insetY = 0;
		length = 104;
		width = 104;
		clips = new int[length][width];
		reset();
	}

	public void reset() {
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < width; j++)
				if (i == 0 || j == 0 || i == length - 1 || j == width - 1)
					clips[i][j] = 0xffffff; // fully closed
				else
					clips[i][j] = 0x1000000; // uninitialized
		}
	}

	public void markWall(int i, int direction, int k, int type, boolean flag) {
		k -= insetX;
		i -= insetY;
		if (type == 0) {
			if (direction == 0) {
				addClip(k, i, 128);
				addClip(k - 1, i, 8);
			}
			if (direction == 1) {
				addClip(k, i, 2);
				addClip(k, i + 1, 32);
			}
			if (direction == 2) {
				addClip(k, i, 8);
				addClip(k + 1, i, 128);
			}
			if (direction == 3) {
				addClip(k, i, 32);
				addClip(k, i - 1, 2);
			}
		}
		if (type == 1 || type == 3) {
			if (direction == 0) {
				addClip(k, i, 1);
				addClip(k - 1, i + 1, 16);
			}
			if (direction == 1) {
				addClip(k, i, 4);
				addClip(k + 1, i + 1, 64);
			}
			if (direction == 2) {
				addClip(k, i, 16);
				addClip(k + 1, i - 1, 1);
			}
			if (direction == 3) {
				addClip(k, i, 64);
				addClip(k - 1, i - 1, 4);
			}
		}
		if (type == 2) {
			if (direction == 0) {
				addClip(k, i, 130);
				addClip(k - 1, i, 8);
				addClip(k, i + 1, 32);
			}
			if (direction == 1) {
				addClip(k, i, 10);
				addClip(k, i + 1, 32);
				addClip(k + 1, i, 128);
			}
			if (direction == 2) {
				addClip(k, i, 40);
				addClip(k + 1, i, 128);
				addClip(k, i - 1, 2);
			}
			if (direction == 3) {
				addClip(k, i, 160);
				addClip(k, i - 1, 2);
				addClip(k - 1, i, 8);
			}
		}
		if (flag) {
			if (type == 0) {
				if (direction == 0) {
					addClip(k, i, 0x10000);
					addClip(k - 1, i, 4096);
				}
				if (direction == 1) {
					addClip(k, i, 1024);
					addClip(k, i + 1, 16384);
				}
				if (direction == 2) {
					addClip(k, i, 4096);
					addClip(k + 1, i, 0x10000);
				}
				if (direction == 3) {
					addClip(k, i, 16384);
					addClip(k, i - 1, 1024);
				}
			}
			if (type == 1 || type == 3) {
				if (direction == 0) {
					addClip(k, i, 512);
					addClip(k - 1, i + 1, 8192);
				}
				if (direction == 1) {
					addClip(k, i, 2048);
					addClip(k + 1, i + 1, 32768);
				}
				if (direction == 2) {
					addClip(k, i, 8192);
					addClip(k + 1, i - 1, 512);
				}
				if (direction == 3) {
					addClip(k, i, 32768);
					addClip(k - 1, i - 1, 2048);
				}
			}
			if (type == 2) {
				if (direction == 0) {
					addClip(k, i, 0x10400);
					addClip(k - 1, i, 4096);
					addClip(k, i + 1, 16384);
				}
				if (direction == 1) {
					addClip(k, i, 5120);
					addClip(k, i + 1, 16384);
					addClip(k + 1, i, 0x10000);
				}
				if (direction == 2) {
					addClip(k, i, 20480);
					addClip(k + 1, i, 0x10000);
					addClip(k, i - 1, 1024);
				}
				if (direction == 3) {
					addClip(k, i, 0x14000);
					addClip(k, i - 1, 1024);
					addClip(k - 1, i, 4096);
				}
			}
		}
	}

	public void method212(boolean flag, int j, int k, int l, int i1, int j1) {
		int k1 = 256;
		if (flag)
			k1 += 0x20000;
		l -= insetX;
		i1 -= insetY;
		if (j1 == 1 || j1 == 3) {
			int l1 = j;
			j = k;
			k = l1;
		}
		for (int i2 = l; i2 < l + j; i2++)
			if (i2 >= 0 && i2 < length) {
				for (int j2 = i1; j2 < i1 + k; j2++)
					if (j2 >= 0 && j2 < width)
						addClip(i2, j2, k1);
			}
	}

	public void orClipTableSET(int i, int k) {
		k -= insetX;
		i -= insetY;
		clips[k][i] |= 0x200000;
	}

	private void addClip(int i, int j, int k) {
		clips[i][j] |= k;
	}

	public void addClippingForVariableObject(int direction, int type, boolean flag, int k, int l) {
		k -= insetX;
		l -= insetY;
		if (type == 0) {
			if (direction == 0) {
				method217(128, k, l);
				method217(8, k - 1, l);
			}
			if (direction == 1) {
				method217(2, k, l);
				method217(32, k, l + 1);
			}
			if (direction == 2) {
				method217(8, k, l);
				method217(128, k + 1, l);
			}
			if (direction == 3) {
				method217(32, k, l);
				method217(2, k, l - 1);
			}
		}
		if (type == 1 || type == 3) {
			if (direction == 0) {
				method217(1, k, l);
				method217(16, k - 1, l + 1);
			}
			if (direction == 1) {
				method217(4, k, l);
				method217(64, k + 1, l + 1);
			}
			if (direction == 2) {
				method217(16, k, l);
				method217(1, k + 1, l - 1);
			}
			if (direction == 3) {
				method217(64, k, l);
				method217(4, k - 1, l - 1);
			}
		}
		if (type == 2) {
			if (direction == 0) {
				method217(130, k, l);
				method217(8, k - 1, l);
				method217(32, k, l + 1);
			}
			if (direction == 1) {
				method217(10, k, l);
				method217(32, k, l + 1);
				method217(128, k + 1, l);
			}
			if (direction == 2) {
				method217(40, k, l);
				method217(128, k + 1, l);
				method217(2, k, l - 1);
			}
			if (direction == 3) {
				method217(160, k, l);
				method217(2, k, l - 1);
				method217(8, k - 1, l);
			}
		}
		if (flag) {
			if (type == 0) {
				if (direction == 0) {
					method217(0x10000, k, l);
					method217(4096, k - 1, l);
				}
				if (direction == 1) {
					method217(1024, k, l);
					method217(16384, k, l + 1);
				}
				if (direction == 2) {
					method217(4096, k, l);
					method217(0x10000, k + 1, l);
				}
				if (direction == 3) {
					method217(16384, k, l);
					method217(1024, k, l - 1);
				}
			}
			if (type == 1 || type == 3) {
				if (direction == 0) {
					method217(512, k, l);
					method217(8192, k - 1, l + 1);
				}
				if (direction == 1) {
					method217(2048, k, l);
					method217(32768, k + 1, l + 1);
				}
				if (direction == 2) {
					method217(8192, k, l);
					method217(512, k + 1, l - 1);
				}
				if (direction == 3) {
					method217(32768, k, l);
					method217(2048, k - 1, l - 1);
				}
			}
			if (type == 2) {
				if (direction == 0) {
					method217(0x10400, k, l);
					method217(4096, k - 1, l);
					method217(16384, k, l + 1);
				}
				if (direction == 1) {
					method217(5120, k, l);
					method217(16384, k, l + 1);
					method217(0x10000, k + 1, l);
				}
				if (direction == 2) {
					method217(20480, k, l);
					method217(0x10000, k + 1, l);
					method217(1024, k, l - 1);
				}
				if (direction == 3) {
					method217(0x14000, k, l);
					method217(1024, k, l - 1);
					method217(4096, k - 1, l);
				}
			}
		}
	}

	public void addClippingForSolidObject(int i, int j, int k, int l, int i1, boolean flag) {
		int j1 = 256;
		if (flag)
			j1 += 0x20000;
		k -= insetX;
		l -= insetY;
		if (i == 1 || i == 3) {
			int k1 = j;
			j = i1;
			i1 = k1;
		}
		for (int l1 = k; l1 < k + j; l1++)
			if (l1 >= 0 && l1 < length) {
				for (int i2 = l; i2 < l + i1; i2++)
					if (i2 >= 0 && i2 < width)
						method217(j1, l1, i2);
			}
	}

	private void method217(int i, int j, int k) {
		clips[j][k] &= 0xffffff - i;
	}

	public void method218(int j, int k) {
		k -= insetX;
		j -= insetY;
		clips[k][j] &= 0xdfffff;
	}

	public boolean blockedTile(int i, int j, int k, int direction, int type, int k1) {
		if (j == i && k == k1)
			return true;
		j -= insetX;
		k -= insetY;
		i -= insetX;
		k1 -= insetY;
		if (type == 0)
			if (direction == 0) {
				if (j == i - 1 && k == k1)
					return true;
				if (j == i && k == k1 + 1 && (clips[j][k] & 0x1280120) == 0)
					return true;
				if (j == i && k == k1 - 1 && (clips[j][k] & 0x1280102) == 0)
					return true;
			} else if (direction == 1) {
				if (j == i && k == k1 + 1)
					return true;
				if (j == i - 1 && k == k1 && (clips[j][k] & 0x1280108) == 0)
					return true;
				if (j == i + 1 && k == k1 && (clips[j][k] & 0x1280180) == 0)
					return true;
			} else if (direction == 2) {
				if (j == i + 1 && k == k1)
					return true;
				if (j == i && k == k1 + 1 && (clips[j][k] & 0x1280120) == 0)
					return true;
				if (j == i && k == k1 - 1 && (clips[j][k] & 0x1280102) == 0)
					return true;
			} else if (direction == 3) {
				if (j == i && k == k1 - 1)
					return true;
				if (j == i - 1 && k == k1 && (clips[j][k] & 0x1280108) == 0)
					return true;
				if (j == i + 1 && k == k1 && (clips[j][k] & 0x1280180) == 0)
					return true;
			}
		if (type == 2)
			if (direction == 0) {
				if (j == i - 1 && k == k1)
					return true;
				if (j == i && k == k1 + 1)
					return true;
				if (j == i + 1 && k == k1 && (clips[j][k] & 0x1280180) == 0)
					return true;
				if (j == i && k == k1 - 1 && (clips[j][k] & 0x1280102) == 0)
					return true;
			} else if (direction == 1) {
				if (j == i - 1 && k == k1 && (clips[j][k] & 0x1280108) == 0)
					return true;
				if (j == i && k == k1 + 1)
					return true;
				if (j == i + 1 && k == k1)
					return true;
				if (j == i && k == k1 - 1 && (clips[j][k] & 0x1280102) == 0)
					return true;
			} else if (direction == 2) {
				if (j == i - 1 && k == k1 && (clips[j][k] & 0x1280108) == 0)
					return true;
				if (j == i && k == k1 + 1 && (clips[j][k] & 0x1280120) == 0)
					return true;
				if (j == i + 1 && k == k1)
					return true;
				if (j == i && k == k1 - 1)
					return true;
			} else if (direction == 3) {
				if (j == i - 1 && k == k1)
					return true;
				if (j == i && k == k1 + 1 && (clips[j][k] & 0x1280120) == 0)
					return true;
				if (j == i + 1 && k == k1 && (clips[j][k] & 0x1280180) == 0)
					return true;
				if (j == i && k == k1 - 1)
					return true;
			}
		if (type == 9) {
			if (j == i && k == k1 + 1 && (clips[j][k] & 0x20) == 0)
				return true;
			if (j == i && k == k1 - 1 && (clips[j][k] & 2) == 0)
				return true;
			if (j == i - 1 && k == k1 && (clips[j][k] & 8) == 0)
				return true;
			if (j == i + 1 && k == k1 && (clips[j][k] & 0x80) == 0)
				return true;
		}
		return false;
	}

	public boolean method220(int i, int j, int k, int l, int i1, int j1) {
		if (j1 == i && k == j)
			return true;
		j1 -= insetX;
		k -= insetY;
		i -= insetX;
		j -= insetY;
		if (l == 6 || l == 7) {
			if (l == 7)
				i1 = i1 + 2 & 3;
			if (i1 == 0) {
				if (j1 == i + 1 && k == j && (clips[j1][k] & 0x80) == 0)
					return true;
				if (j1 == i && k == j - 1 && (clips[j1][k] & 2) == 0)
					return true;
			} else if (i1 == 1) {
				if (j1 == i - 1 && k == j && (clips[j1][k] & 8) == 0)
					return true;
				if (j1 == i && k == j - 1 && (clips[j1][k] & 2) == 0)
					return true;
			} else if (i1 == 2) {
				if (j1 == i - 1 && k == j && (clips[j1][k] & 8) == 0)
					return true;
				if (j1 == i && k == j + 1 && (clips[j1][k] & 0x20) == 0)
					return true;
			} else if (i1 == 3) {
				if (j1 == i + 1 && k == j && (clips[j1][k] & 0x80) == 0)
					return true;
				if (j1 == i && k == j + 1 && (clips[j1][k] & 0x20) == 0)
					return true;
			}
		}
		if (l == 8) {
			if (j1 == i && k == j + 1 && (clips[j1][k] & 0x20) == 0)
				return true;
			if (j1 == i && k == j - 1 && (clips[j1][k] & 2) == 0)
				return true;
			if (j1 == i - 1 && k == j && (clips[j1][k] & 8) == 0)
				return true;
			if (j1 == i + 1 && k == j && (clips[j1][k] & 0x80) == 0)
				return true;
		}
		return false;
	}

	public boolean method221(int i, int j, int k, int l, int i1, int j1, int k1) {
		int l1 = (j + j1) - 1;
		int i2 = (i + l) - 1;
		if (k >= j && k <= l1 && k1 >= i && k1 <= i2)
			return true;
		if (k == j - 1 && k1 >= i && k1 <= i2 && (clips[k - insetX][k1 - insetY] & 8) == 0 && (i1 & 8) == 0)
			return true;
		if (k == l1 + 1 && k1 >= i && k1 <= i2 && (clips[k - insetX][k1 - insetY] & 0x80) == 0 && (i1 & 2) == 0)
			return true;
		return k1 == i - 1 && k >= j && k <= l1 && (clips[k - insetX][k1 - insetY] & 2) == 0 && (i1 & 4) == 0 || k1 == i2 + 1 && k >= j
				&& k <= l1 && (clips[k - insetX][k1 - insetY] & 0x20) == 0 && (i1 & 1) == 0;
	}

	private final int insetX;
	private final int insetY;
	private final int length;
	private final int width;
	public final int[][] clips;
}
