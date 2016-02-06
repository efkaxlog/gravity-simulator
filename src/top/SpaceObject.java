package top;
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
	
	public void collide(SpaceObject co) {
		if (canAbsorb) {
			mass += co.mass;
			double area = Math.PI * Math.pow(getRadius(), 2);
			double coArea = Math.PI * Math.pow(co.getRadius(), 2);
			double mergedArea = area + coArea;
			setRadius(Math.sqrt(mergedArea / Math.PI));
		}		
		if (canMove) {
			double totalMass = mass + co.mass;
			double totalForceX = Physics.getForceX(this) + Physics.getForceX(co);
			double totalForceY = Physics.getForceY(this) + Physics.getForceY(co);
			vx = Physics.getAccelerationFromForce(totalForceX, totalMass);
			vy = Physics.getAccelerationFromForce(totalForceY, totalMass);
		}
	}
	
	
}

