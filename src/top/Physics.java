package top;

public class Physics {

	//static double G = 6.67E-11;
	
	/*
	 * Gravitational constant
	 * The real one is: G = 6.67E-11
	 */
	static double G = 0.03;
	
	/**
	 * 
	 * @param SpaceObject1
	 * @param SpaceObject2
	 * @return calculated distance between two Space Objects
	 */
	final public static double getDistance(SpaceObject so1, SpaceObject so2) {
		// square root of: x distance^2 + y distance^2
		return Math.sqrt(
				(Math.pow(so1.getCenterX() - so2.getCenterX(), 2)) +  
				(Math.pow(so1.getCenterY() - so2.getCenterY(), 2))); 
	}
	
	/**
	 * 
	 * @param SpaceObject1
	 * @param SpaceObject2
	 * @return acceleration
	 */
	final public static double getDX(SpaceObject so1, SpaceObject so2) {
		final double d = getDistance(so1, so2);
		return G * so1.mass / (d*d) * (so1.getCenterX() - so2.getCenterX()) / d;
	}
	
	/**
	 * 
	 * @param SpaceObject1
	 * @param SpaceObject2
	 * @return acceleration
	 */
	final public static double getDY(SpaceObject so1, SpaceObject so2) {
		final double d = getDistance(so1, so2);
		return G * so1.mass / (d*d) * (so1.getCenterY() - so2.getCenterY()) / d;
	}
	
	public static double getForceY(SpaceObject so) {
		// f = ma
		return so.mass * so.vy;
	}
	
	public static double getForceX(SpaceObject so) {
		// f = ma
		return so.mass * so.vx;
	}
	
	public static double getAccelerationFromForce(double force, double mass) {
		// a = f/m
		return force/mass;
	}
}