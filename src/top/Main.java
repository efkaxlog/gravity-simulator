package top;
import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class Main extends Application{
	
	Stage stage;
	Scene scene;
	Pane root;
	
  	ArrayList<SpaceObject> spaceObjects = new ArrayList<SpaceObject>();
  	
  	double mouseStartX, mouseEndX, mouseStartY, mouseEndY;
  	boolean dragging = false;
  	Line dragLine = new Line();
  	Line pixelLine;
  	Label gravityLabel = new Label();
  	Label particlesNumberLabel = new Label();
  	boolean bordersEnabled = true;
  	boolean collisionsOn = true;
  	boolean running = false;
  	boolean isOnPlanetGenerator = false;
  	double smallBodyRadius = 0.4;
  	double smallBodyMass = 1;
  	double starRadius = 25;
  	double starMass = 10000;
  	double defaultGravity = 2;
  	double gravityStep = defaultGravity / 10;
  	double lastShot = System.currentTimeMillis();
  	double planetShotInterval = 0;
  	
  	Label stepsPerSecLabel = new Label();
  	double stepsPerSec = 0;
  	// for checking how many steps per sec
  	double stepCounter = 0;
  	double lastStepMillis = System.currentTimeMillis();
  	
  	Kernel kernel;
  	
  	Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
  	
  	Color labelColor = Color.WHITE;
  	
  	final double[] currentDx = new double[1000];
  	final double[] currentDy = new double[1000];
  	final double[] currentX = new double[1000];
  	final double[] currentY = new double[1000];
  	final double[] currentMasses = new double[1000];
  	final int[] currentBody = new int[] {0};
  	final double G = 0.03;
  	
  	
  	
	AnimationTimer atMain = new AnimationTimer() {
		

		@Override
		public void handle(long arg0) {
			// TODO Auto-generated method stub
			update();
			if ((dragging && isOnPlanetGenerator) && canShoot()) {
				createSpeedyObject();
				lastShot = System.currentTimeMillis();
			}

			
			if (System.currentTimeMillis() - lastStepMillis >=  1000) {
				lastStepMillis = System.currentTimeMillis();
				stepsPerSec = stepCounter;
				stepCounter = 0;
			}
			
			stepCounter ++;
			
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}};
		
		EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

			public void handle(MouseEvent e) {
				// TODO Auto-generated method stub
//				double mass = Math.random() *100;
				if (e.getButton() == MouseButton.SECONDARY) {
					SpaceObject so = new SpaceObject(starMass, 0, 0);
					so.setCenterX(e.getX());
					so.setCenterY(e.getY());
					so.setRadius(starRadius);
					so.canMove = false;
					so.setFill(Color.YELLOW);
					so.canAbsorb = false;
					spaceObjects.add(so);
					root.getChildren().add(so);
				}
				updateLabels();
			}
		};
		
		EventHandler<KeyEvent> keyPressHandler = new EventHandler<KeyEvent>() {
			
			public void handle(KeyEvent e) {
				KeyCode key = e.getCode();
				if(key != null) switch (key) {
				case DOWN:
					Physics.G -= gravityStep;
					break;
				case UP:
					Physics.G += gravityStep;
					break;
				case SPACE:
					if (running) {
						atMain.stop();
						running = false;
					} 
					else {
						atMain.start();
						running = true;
					}
					break;
				case C:
					clearObjects();
					break;
				case R: 
					resetToDefaultSettings();
					break;
				case Z:
					isOnPlanetGenerator = true;
					break;
				case ESCAPE:
					System.exit(0);
					break;
				case Q:
					createChaos();
					break;
				case F:
					if (stage.isFullScreen()) {
						stage.setFullScreen(false);
					}
					else {
						stage.setFullScreen(true);
						stage.sizeToScene();
					}
					break;
				case RIGHT:
					update();
					break;
				default:
					break;
				}
				updateLabels();
			}
		};
		
		EventHandler<KeyEvent> keyReleaseHandler = new EventHandler<KeyEvent>() {
			
			public void handle(KeyEvent e) {
				KeyCode key = e.getCode();
				if(key != null) switch (key) {
				case Z:
					isOnPlanetGenerator = false;
				default:
					break;
				}
				updateLabels();
			}
		};
	
	public static void main(String[] args) {
		launch(args);
	    /*  System.out.println("got here");

		double aparapiStart = System.currentTimeMillis();
		
		Aparapi.addArray();
		double aparapiEnd = System.currentTimeMillis();
		double localStart = System.currentTimeMillis();
		addArray();
		double localEnd = System.currentTimeMillis();
		System.out.println("Aparapi: " + (aparapiEnd - aparapiStart));
		System.out.println("Local: " + (localEnd - localStart));*/
	
	}
	
	private static  void addArray() {
		final int size = 99999;
		
	    final float[] a = new float[size];
	    final float[] b = new float[size];
	
	    for (int i = 0; i < size; i++) {
	       a[i] = (float) ((float) (Math.random() * 100) * Math.pow(5632, 3));
	       b[i] = (float) ((float) (Math.random() * 100) * Math.pow(5632, 3));
	    }

	      final float[] sum = new float[size];

	      for (int i = 0; i < size; i++) {
	    	  sum[i] = a[i] * b[i];
	      }
	
	    
	      
	    /*  for (int i = 0; i < size; i++) {
	          System.out.printf("%6.2f + %6.2f = %8.2f\n", a[i], b[i], sum[i]);
	       }*/

	}
	
	private void createChaos() {
		for (int i=0; i < 1000; i ++) {
			SpaceObject body = new SpaceObject(
					smallBodyMass, 
					(Math.random() * -5) + (Math.random() * 5),
					(Math.random() * -5) + (Math.random() * 5));
			body.setCenterX(Math.random() * scene.getWidth());
			body.setCenterY(Math.random() * scene.getHeight());
			body.setFill(getRandomColor());
			body.setRadius(smallBodyRadius);
			spaceObjects.add(body);
			root.getChildren().add(body);
		}
	}
	
	private void resetToDefaultSettings() {
		Physics.G = defaultGravity;
	}
	
	/**
	 * clears any "space" objects from the screen
	 * leaves any labels, etc.
	 */
	private void clearObjects() {
		root.getChildren().clear();
		spaceObjects.clear();
		addLabels();
	}
	
	private void addLabels() {
		root.getChildren().addAll(gravityLabel, particlesNumberLabel, stepsPerSecLabel);
	}
	
	private void updateLabels() {
		particlesNumberLabel.setText("Particles: " + spaceObjects.size());
		gravityLabel.setText("Gravity: " + Physics.G);
		stepsPerSecLabel.setText("Steps per second: " + stepsPerSec);
	}
	
	public void start(Stage stage) {
		this.stage = stage;
		Physics.G = defaultGravity;	
	  	root = new Pane();
	    scene = new Scene(root, 1920, 1080);
	    createChaos();

	  	root.setStyle("-fx-background-color: #000000");
	  	dragLine.setStroke(Color.WHITE);
	  	
	  	gravityLabel.setText("Gravity: " + Physics.G);
	  	gravityLabel.setTextFill(labelColor);
	  	gravityLabel.setLayoutX(5);
	  	gravityLabel.setLayoutY(5);
	  	root.getChildren().add(gravityLabel);  	
	  	
	  	particlesNumberLabel = new Label("Particles: " + spaceObjects.size());
	  	particlesNumberLabel.setTextFill(labelColor);
	  	particlesNumberLabel.setLayoutX(5);
	  	particlesNumberLabel.setLayoutY(15);
	  	root.getChildren().add(particlesNumberLabel);
	  	
	  	stepsPerSecLabel = new Label("Steps per second: " + stepsPerSec);
	  	stepsPerSecLabel.setTextFill(labelColor);
	  	stepsPerSecLabel.setLayoutX(5);
	  	stepsPerSecLabel.setLayoutY(25);
	  	root.getChildren().add(stepsPerSecLabel);
	  	
	  	stage.setScene(scene);
	  	stage.show();
	  	
	    //root.getChildren().addAll(sun, earth, sunDX, sunDY, sunX, sunY, earthDX, earthDY, earthX, earthY, xForce, yForce, c);
	  	scene.setOnKeyPressed(keyPressHandler);
	  	scene.setOnMouseClicked(mouseHandler);

	  	scene.setOnMouseDragged(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent e) {
				if (e.isPrimaryButtonDown()) {
					if (!dragging) {
						mouseStartX = e.getX();
						mouseStartY = e.getY();
						dragging = true;
						root.getChildren().add(dragLine);
						dragLine.setStartX(mouseStartX);
						dragLine.setStartY(mouseStartY);
					}
					dragLine.setEndX(e.getX());
					dragLine.setEndY(e.getY());
					mouseEndX = e.getX();
					mouseEndY = e.getY();
				}
			}
		});
	  	
	  	
		scene.setOnMouseReleased(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent e) {
				if (dragging) {
					dragging = false;
					root.getChildren().remove(dragLine);
					createSpeedyObject();
				}
				else if (e.getButton() == MouseButton.PRIMARY){
					createBody(e.getX(), e.getY(), 0, 0);
				}
			}
		});
		updateLabels();
		
//		scene.setOnMouseMoved(mouseMoveHandler);
		scene.setOnKeyReleased(keyReleaseHandler);
		stage.setFullScreen(true);
		
		int i = 0;
		for (SpaceObject body : spaceObjects) {
			currentMasses[i] = body.mass;
			currentX[i] = body.getCenterX();
			currentY[i] = body.getCenterY();
			currentDx[i] = body.vx;
			currentDy[i] = body.vy;
			i++;
		}

		
		kernel = new Kernel(){
			@Override
			public void run() {
				int i = getGlobalId();
				double bodyMass = currentMasses[currentBody[0]];
				double otherMass = currentMasses[0];
				double bodyX = currentX[currentBody[0]];
				double bodyY = currentY[currentBody[0]];
				double otherX = currentX[i];
				double otherY = currentY[i];
				double d = Math.sqrt(
						(Math.pow(bodyX - otherX, 2)) +
						(Math.pow(bodyY - otherY, 2))
						);
				double dx = G * bodyMass / (d*d) * (bodyX - otherX) / d;
				double dy = G * bodyMass / (d*d) * (bodyY - otherY) / d;
				currentDx[i] += dx;
				currentDy[i] += dy;
				currentBody[0] ++;
			}};
			
			
		
		

		/*for (SpaceObject so : spaceObjects) {
 			for (SpaceObject so2 : spaceObjects) {
 				if (so == so2 || !so.canMove) continue;		
 				double d = Math.sqrt(
 						(Math.pow(so.getCenterX() - so2.getCenterX(), 2)) +  
 						(Math.pow(so.getCenterY() - so2.getCenterY(), 2)));
 				so.vy += defaultGravity * so.mass / (d*d) * (so2.getCenterY() - so.getCenterY()) / d;
 				so.vx += defaultGravity * so.mass / (d*d) * (so2.getCenterX() - so.getCenterX()) / d;
 			}
  	  
		}*/
		
	  	
	}
	
	private boolean canShoot() {
		return System.currentTimeMillis() - lastShot >= planetShotInterval;
	}
	
	private void createBody(double x, double y, double vx, double vy) {
		double mass = smallBodyMass;
		SpaceObject so = new SpaceObject(mass, 0, 0);
		so.setCenterX(x);
		so.setCenterY(y);
		so.setRadius(smallBodyRadius);
		so.setFill(getRandomColor());
		spaceObjects.add(so);
		root.getChildren().add(so);
	}
	
	private Color getRandomColor() {
		double r = Math.random();
		double g = Math.random();
		double b = Math.random();
		return new Color(r, g, b, 1);
	}
	
	private void createSpeedyObject() {
		double vx = (mouseStartX - mouseEndX) / 50;
		double vy = (mouseStartY - mouseEndY) / 50;
		SpaceObject so = new SpaceObject(smallBodyMass, vx, vy);
		so.setCenterX(mouseEndX);
		so.setCenterY(mouseEndY);
		spaceObjects.add(so);
		so.setRadius(smallBodyRadius);
		so.setFill(getRandomColor());
		root.getChildren().add(so);
	}
	 
	public void drawTrace(SpaceObject so) {
		pixelLine = new Line(so.getCenterX(), so.getCenterY(), so.getCenterX(), so.getCenterY());
		pixelLine.setStroke(so.getFill());
		pixelLine.setStrokeWidth(1.0f);
		root.getChildren().add(pixelLine);
	}
	
	private void handleCollision() {
		ArrayList<SpaceObject> bodiesToRemove = new ArrayList<SpaceObject>();
		// got to use Iterators to remove objects while iterating
		for (SpaceObject so1 : spaceObjects) {
			for (SpaceObject so2 : spaceObjects) {
				if (so1.equals(so2)) {
					continue;
				}
				
				if (Math.abs(so1.getCenterX() - so2.getCenterX()) - so1.getRadius() <= 0 &&
						Math.abs(so1.getCenterY() - so2.getCenterY()) - so1.getRadius() <= 0) {
					if (so1.mass > so2.mass) {
						so1.collide(so2);
						bodiesToRemove.add(so2);
					}
					else if (so1.mass < so2.mass) {
						so2.collide(so1);
						bodiesToRemove.add(so1);
					}
					// mass is equal;
					else if (so1.vx + so1.vy <= so2.vx + so2.vy) {
						so1.collide(so2);
						bodiesToRemove.add(so2);
					}
					else {
						so2.collide(so1);
						bodiesToRemove.add(so1);
					}
					updateLabels();
				}
			} // end for so2
		} // end for so1
		spaceObjects.removeAll(bodiesToRemove);
		root.getChildren().removeAll(bodiesToRemove);
		bodiesToRemove.clear();
		
	}
	
	public void update() {
		// check collisions BEFORE UPDATING VX AND VY
		if (collisionsOn) {
			handleCollision();
		}	
		kernel.execute(spaceObjects.size());
		 if (!kernel.getExecutionMode().equals(Kernel.EXECUTION_MODE.GPU)) {
	    	   System.out.println("Kernel did not execute on the GPU!");
	    } else {
	    	   System.out.println("Kernel DID execute on the GPU!");
	    }
	    kernel.dispose();
		
/*		for (SpaceObject so : spaceObjects) {
			for (SpaceObject so2 : spaceObjects) {
				if (so2 == so || !so.canMove) {
					continue;
				}
				
//				so.vx = cuda.getAcc(so, so2, (float)defaultGravity)[0];
//				so.vy = cuda.getAcc(so, so2, (float)defaultGravity)[1];
//				so.vx += Physics.getDX(so2, so);
//				so.vy += Physics.getDY(so2, so);
			} // end for so2
		} // end for so
*/		
		for (SpaceObject so : spaceObjects) {
			so.setCenterX(so.getCenterX() + so.vx);
			so.setCenterY(so.getCenterY() + so.vy);
			if (bordersEnabled) {
				if (so.getCenterX() > root.getWidth() || so.getCenterX() < 0) {
					so.vx = -so.vx;
				}
				if (so.getCenterY() > root.getHeight() || so.getCenterY() < 0) {
					so.vy = -so.vy;
				}
			}
			//drawTrace(so);
		} // end for

	}
}

