import javafx.scene.shape.Circle;

public class SpaceObject extends Circle {

	double mass, vx, vy;
	boolean canMove, canAbsorb;
	
	public SpaceObject(double mass, double vx, double vy) {
		this.mass = mass;
		this.vx = vx;
		this.vy = vy;
		canMove = true;
		canAbsorb = true;
	}
	
	public void collide(SpaceObject collisionObject) {
		if (canAbsorb) {
			mass += collisionObject.mass;
			setRadius(getRadius() + collisionObject.getRadius());
		}		
		if (canMove) {
			vx += collisionObject.vx;
			vy += collisionObject.vy;
		}
	}
	
	
}

