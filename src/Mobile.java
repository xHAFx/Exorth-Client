/**
 * A mobile entity
 * 
 * @author Joshua Barry
 * 
 */
public class Mobile extends Renderable {

	public final void setPos(int x, int y, boolean flag) {
		if (anim != -1 && Sequence.anims[anim].walkable == 1)
			anim = -1;
		if (!flag) {
			int k = x - smallX[0];
			int l = y - smallY[0];
			if (k >= -8 && k <= 8 && l >= -8 && l <= 8) {
				if (smallXYIndex < 9)
					smallXYIndex++;
				for (int i1 = smallXYIndex; i1 > 0; i1--) {
					smallX[i1] = smallX[i1 - 1];
					smallY[i1] = smallY[i1 - 1];
					aBooleanArray1553[i1] = aBooleanArray1553[i1 - 1];
				}
				smallX[0] = (short) x;
				smallY[0] = (short) y;
				aBooleanArray1553[0] = false;
				return;
			}
		}
		smallXYIndex = 0;
		positionBasedInt = 0;
		anInt1503 = 0;
		smallX[0] = (short) x;
		smallY[0] = (short) y;
		this.x = (short) (smallX[0] * 128 + anInt1540 * 64);
		this.y = (short) (smallY[0] * 128 + anInt1540 * 64);
	}

	public final void resetLocationIndex() {
		smallXYIndex = 0;
		positionBasedInt = 0;
	}

	public final void updateHitData(int type, int damage, int time) {
		for (int i1 = 0; i1 < 4; i1++)
			if (hitsLoopCycle[i1] <= time) {
				hitArray[i1] = damage;
				hitMarkTypes[i1] = type;
				hitsLoopCycle[i1] = time + 70;
				return;
			}
	}

	public final void move(boolean flag, int direction) {
		int j = smallX[0];
		int k = smallY[0];
		if (direction == 0) {
			j--;
			k++;
		}
		if (direction == 1)
			k++;
		if (direction == 2) {
			j++;
			k++;
		}
		if (direction == 3)
			j--;
		if (direction == 4)
			j++;
		if (direction == 5) {
			j--;
			k--;
		}
		if (direction == 6)
			k--;
		if (direction == 7) {
			j++;
			k--;
		}
		if (anim != -1 && Sequence.anims[anim].walkable == 1)
			anim = -1;
		if (smallXYIndex < 9)
			smallXYIndex++;
		for (int l = smallXYIndex; l > 0; l--) {
			smallX[l] = smallX[l - 1];
			smallY[l] = smallY[l - 1];
			aBooleanArray1553[l] = aBooleanArray1553[l - 1];
		}
		smallX[0] = (short) j;
		smallY[0] = (short) k;
		aBooleanArray1553[0] = flag;
	}

	public int entScreenX;
	public int entScreenY;
	public final int index = -1;

	public boolean isVisible() {
		return false;
	}

	Mobile() {
		smallX = new short[10];
		smallY = new short[10];
		interactingEntity = -1;
		anInt1504 = 32;
		anInt1505 = -1;
		height = 200;
		anInt1511 = -1;
		anInt1512 = -1;
		hitArray = new int[4];
		hitMarkTypes = new int[4];
		hitsLoopCycle = new int[4];
		anInt1517 = -1;
		gfxId = -1;
		anim = -1;
		loopCycleStatus = -1000;
		textCycle = 100;
		anInt1540 = 1;
		aBoolean1541 = false;
		aBooleanArray1553 = new boolean[10];
		anInt1554 = -1;
		anInt1555 = -1;
		anInt1556 = -1;
		anInt1557 = -1;
	}

	public final short[] smallX;
	public final short[] smallY;
	public int interactingEntity;
	int anInt1503;
	int anInt1504;
	int anInt1505;
	public String textSpoken;
	public int height;
	public int turnDirection;
	int anInt1511;
	int anInt1512;
	int anInt1513;
	final int[] hitArray;
	final int[] hitMarkTypes;
	final int[] hitsLoopCycle;
	int anInt1517;
	int anInt1518;
	int anInt1519;
	int gfxId;
	int currentAnim;
	int anInt1522;
	int anInt1523;
	int anInt1524;
	int smallXYIndex;
	public int anim;
	int anInt1527;
	int anInt1528;
	int anInt1529;
	int anInt1530;
	int anInt1531;
	public int loopCycleStatus;
	public int currentHealth;
	public int maxHealth;
	int textCycle;
	int anInt1537;
	int anInt1538;
	int anInt1539;
	int anInt1540;
	boolean aBoolean1541;
	int positionBasedInt;
	int anInt1543;
	int anInt1544;
	int anInt1545;
	int anInt1546;
	int anInt1547;
	int anInt1548;
	int anInt1549;
	public short x;
	public short y;
	int anInt1552;
	final boolean[] aBooleanArray1553;
	int anInt1554;
	int anInt1555;
	int anInt1556;
	int anInt1557;
}
