final class Projectile extends Renderable {

	public void method455(int i, int j, int k, int l) {
		if (!aBoolean1579) {
			double d = l - anInt1580;
			double d2 = j - anInt1581;
			double d3 = Math.sqrt(d * d + d2 * d2);
			aDouble1585 = (double) anInt1580 + (d * (double) anInt1589) / d3;
			aDouble1586 = (double) anInt1581 + (d2 * (double) anInt1589) / d3;
			aDouble1587 = anInt1582;
		}
		double d1 = (speedtime + 1) - i;
		aDouble1574 = ((double) l - aDouble1585) / d1;
		aDouble1575 = ((double) j - aDouble1586) / d1;
		aDouble1576 = Math.sqrt(aDouble1574 * aDouble1574 + aDouble1575 * aDouble1575);
		if (!aBoolean1579)
			aDouble1577 = -aDouble1576 * Math.tan((double) slope * 0.02454369D);
		aDouble1578 = (2D * ((double) k - aDouble1587 - aDouble1577 * d1)) / (d1 * d1);
	}

	public Model getRotatedModel() {
		Model model = graphic.getModel();
		if (model == null)
			return null;
		int j = -1;
		if (graphic.aAnimation_407 != null)
			j = graphic.aAnimation_407.anIntArray353[elapsedFramed];
		Model model_1 = new Model(true, Animation.method532(j), false, model);
		if (j != -1) {
			model_1.method469();
			model_1.method470(j);
			model_1.triangleSkin = null;
			model_1.vertexSkin = null;
		}
		if (graphic.anInt410 != 128 || graphic.anInt411 != 128)
			model_1.resize(graphic.anInt410, graphic.anInt410, graphic.anInt411);
		model_1.method474(anInt1596);
		model_1.light(64 + graphic.anInt413, 850 + graphic.anInt414, -30, -50, -30, true);
		return model_1;
	}

	public Projectile(int i, int j, int l, int i1, int j1, int k1, int l1, int i2, int j2, int k2, int l2) {
		aBoolean1579 = false;
		graphic = Graphic.cache[l2];
		anInt1597 = k1;
		anInt1580 = j2;
		anInt1581 = i2;
		anInt1582 = l1;
		delayTime = l;
		speedtime = i1;
		slope = i;
		anInt1589 = j1;
		anInt1590 = k2;
		anInt1583 = j;
		aBoolean1579 = false;
	}

	public void method456(int timePassed) {
		aBoolean1579 = true;
		aDouble1585 += aDouble1574 * (double) timePassed;
		aDouble1586 += aDouble1575 * (double) timePassed;
		aDouble1587 += aDouble1577 * (double) timePassed + 0.5D * aDouble1578 * (double) timePassed * (double) timePassed;
		aDouble1577 += aDouble1578 * (double) timePassed;
		anInt1595 = (int) (Math.atan2(aDouble1574, aDouble1575) * 325.94900000000001D) + 1024 & 0x7ff;
		anInt1596 = (int) (Math.atan2(aDouble1577, aDouble1576) * 325.94900000000001D) & 0x7ff;
		if (graphic.aAnimation_407 != null)
			for (duration += timePassed; duration > graphic.aAnimation_407.getFrameLength(elapsedFramed);) {
				duration -= graphic.aAnimation_407.getFrameLength(elapsedFramed) + 1;
				elapsedFramed++;
				if (elapsedFramed >= graphic.aAnimation_407.anInt352)
					elapsedFramed = 0;
			}
	}

	public final int delayTime;
	public final int speedtime;
	private double aDouble1574;
	private double aDouble1575;
	private double aDouble1576;
	private double aDouble1577;
	private double aDouble1578;
	private boolean aBoolean1579;
	private final int anInt1580;
	private final int anInt1581;
	private final int anInt1582;
	public final int anInt1583;
	public double aDouble1585;
	public double aDouble1586;
	public double aDouble1587;
	private final int slope;
	private final int anInt1589;
	public final int anInt1590;
	private final Graphic graphic;
	private int elapsedFramed;
	private int duration;
	public int anInt1595;
	private int anInt1596;
	public final int anInt1597;
}
