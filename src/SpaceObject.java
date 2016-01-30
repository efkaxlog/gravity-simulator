import javafx.scene.shape.Circle;

public class SpaceObject extends Circle {

	double mass, vx, vy;
	boolean canMove;
	
	public SpaceObject(double mass, double vx, double vy) {
		this.mass = mass;
		this.vx = vx;
		this.vy = vy;
		canMove = true;
	}
	
	
}

