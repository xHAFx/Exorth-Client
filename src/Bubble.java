import java.awt.Color;


public class Bubble {

	private int x;
	private int y;
	private byte radius;
	private int speed;
	private int xChange;
	public static final byte BUBBLES = 1;
	public static final byte BOUNCING_BALLS = 0;
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public byte getRadius() {
		return radius;
	}
	
	public Bubble() {
		radius = (byte) (Math.random() * 60.0d);
		if (radius < 30) {
			radius += 15;
		}
		xChange = ((Math.random()) == 1 ? -1 : 1) * (int) Math.round(Math.random());
		x = radius + (int)(Math.random() * (RSClient.clientWidth - radius * 2)); 
		y = RSClient.clientHeight + radius + (int) (Math.random() * 50.0d);
		speed = (int) (Math.random() * 3.0d);
		if (speed == 0) {
			speed = 1;
		}
		xSpeed = speed;
		ySpeed = speed;
	}
	
	private boolean setToRandomY = false;
	private int xSpeed;
	private int ySpeed;
	
	public void draw(byte state) {
		if (this != null) {
			switch (state) {
			
			case BUBBLES:
				this.y -= speed;
				this.x += xChange;
				if (this.y < 0 - radius) {
					radius = (byte) (Math.random() * 60.0d);
					if (radius < 30) {
						radius += 15;
					}
					xChange = (Math.round(Math.random()) == 1 ? -1 : 1) * (int) Math.round(Math.random());
					x = radius + (int)(Math.random() * (RSClient.clientWidth - radius));
					y = RSClient.clientHeight + radius + (int) (Math.random() * (Math.random() * 50.0d));
					speed = (int) (Math.random() * 3.0d);
					if (speed == 0) {
						speed = 1;
					}
				}
				RSRaster.drawBubble(this.x, this.y, (int) this.radius, 0xFFFFFF, 20);
				break;
			}
		}
	}

}