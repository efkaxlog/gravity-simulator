import java.util.ArrayList;
import java.util.Iterator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application{
	
	Scene scene;
	Pane root;
	
  	ArrayList<SpaceObject> spaceObjects;
  	
  	double mouseStartX, mouseEndX, mouseStartY, mouseEndY;
  	boolean dragging = false;
  	Line dragLine = new Line();
  	Line pixelLine;
  	Label gravityLabel = new Label();
  	Label particlesNumberLabel = new Label();
  	boolean bordersEnabled = true;
  	boolean collisionsOn = true;
  	
  	double smallBodyRadius = 5;
  	double starRadius = 50;
  	
  	double defaultGravity = 0.03;
  	
	AnimationTimer atMain = new AnimationTimer() {
		

		@Override
		public void handle(long arg0) {
			// TODO Auto-generated method stub
			update();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}};
		
		EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				// TODO Auto-generated method stub
//				double mass = Math.random() *100;
				if (e.getButton() == MouseButton.SECONDARY) {
					double mass = 10000;
					SpaceObject so = new SpaceObject(mass, 0, 0);
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
			
			@Override
			public void handle(KeyEvent e) {
				KeyCode key = e.getCode();
				if(key != null) switch (key) {
				case DOWN:
					atMain.stop();
					break;
				case UP:
					atMain.start();
					break;
				case SPACE:
					Physics.G += 0.1;
					break;
				case Q:
					Physics.G -= 0.1;
					break;
				case C:
					clearObjects();
					break;
				case R: 
					resetToDefaultSettings();
					break;
				default:
					break;
				}
				updateLabels();
			}
		};
	
	public static void main(String[] args) {
		launch(args);
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
		root.getChildren().addAll(gravityLabel, particlesNumberLabel);
	}
	
	private void updateLabels() {
		particlesNumberLabel.setText("Particles: " + spaceObjects.size());
		gravityLabel.setText("Gravity: " + Physics.G);
	}
	
	public void start(Stage stage) {
		Physics.G = defaultGravity;
		spaceObjects = new ArrayList<SpaceObject>();
	  	root = new Pane();
	    scene = new Scene(root, 800, 800);

	  	root.setStyle("-fx-background-color: #000000");
	  	dragLine.setStroke(Color.WHITE);
	  	
	  	gravityLabel.setTextFill(Color.WHITE);
	  	gravityLabel.setLayoutX(5);
	  	gravityLabel.setLayoutY(5);
	  	gravityLabel.setText("Gravity: " + Physics.G);
	  	root.getChildren().add(gravityLabel);  	
	  	
	  	particlesNumberLabel = new Label("Particles: " + spaceObjects.size());
	  	particlesNumberLabel.setTextFill(Color.WHITE);
	  	particlesNumberLabel.setLayoutX(5);
	  	particlesNumberLabel.setLayoutY(15);
	  	root.getChildren().add(particlesNumberLabel);
	  	
	  	stage.setScene(scene);
	  	stage.show();
	  	
	    //root.getChildren().addAll(sun, earth, sunDX, sunDY, sunX, sunY, earthDX, earthDY, earthX, earthY, xForce, yForce, c);
	  	scene.setOnKeyPressed(keyPressHandler);
	  	scene.setOnMouseClicked(mouseHandler);
	  	scene.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
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
				}
			}
		});
	  	
		scene.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				if (dragging) {
					mouseEndX = e.getX();
					mouseEndY = e.getY();
					dragging = false;
					root.getChildren().remove(dragLine);
					createSpeedyObject(mouseStartX, mouseStartY, mouseEndX, mouseEndY);
				} 
				else if (e.getButton() == MouseButton.PRIMARY){
					double mass = 1;
					SpaceObject so = new SpaceObject(mass, 0, 0);
					so.setCenterX(e.getX());
					so.setCenterY(e.getY());
					so.setRadius(smallBodyRadius);
					so.setFill(getRandomColor());
					spaceObjects.add(so);
					root.getChildren().add(so);
				
				}
			}
		});
		updateLabels();
	  	
	}
	
	private Color getRandomColor() {
		double r = Math.random();
		double g = Math.random();
		double b = Math.random();
		return new Color(r, g, b, 1);
	}
	
	private void createSpeedyObject(double startX, double startY, double endX, double endY) {
		double vx = (startX - endX) / 50;
		double vy = (startY - endY) / 50;
		SpaceObject so = new SpaceObject(10, vx, vy);
		so.setCenterX(endX);
		so.setCenterY(endY);
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
						// so2 'abosrbs so2'
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
		for (SpaceObject so : spaceObjects) {
			for (SpaceObject so2 : spaceObjects) {
				if (so2 == so || !so.canMove) {
					continue;
				}
				so.vx += Physics.getDX(so2, so);
				so.vy += Physics.getDY(so2, so);
			} // end for so2
		} // end for so
		
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

