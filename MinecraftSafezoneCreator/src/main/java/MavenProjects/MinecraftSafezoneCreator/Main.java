package MavenProjects.MinecraftSafezoneCreator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application{

	//VARIABLES
	
	//dir
	private static String dir = "";
	
	//scale
	private static final int screenX = 1000;
	private static final int screenY = 800;	
	private static final double width = 5000;
	private static final double halfWidth = width / 2;
	private static final double conversionFactor = 2;
	
	//new lists
	final static List<Residence> allRes = new ArrayList<Residence>();
	
	//listViews
	final static ListView<String> listViewRes = new ListView<String>(); //show residences
	final static ListView<String> listViewAreas = new ListView<String>(); //show areas
	final static ListView<String> listViewNew = new ListView<String>();
	
	final static List<int[]> newSafeZoneCoord = new ArrayList<int[]>();
	final static List<String> newSafeZoneCoordString = new ArrayList<String>(); 
	final static List<int[]> newSafeZoneCoordConv = new ArrayList<int[]>(); 
	final static List<Line> allLinesNew = new ArrayList<Line>();
			
	final static List<String> cmds = new ArrayList<String>(); //saves all commands
	
	//layout containers
	
	
	private static Stage newSZScreen = new Stage();
	
	private static HBox root = new HBox();
	private static HBox setRemove = new HBox(10);
	private static HBox createHBox = new HBox(10);
	private static HBox removeResArea = new HBox(10);
	
	private static ScrollPane scrollPane = new ScrollPane();
	
	private static VBox contextMenuNewSZ = new VBox();
	private static VBox contextMenuLeft = new VBox(20);
	
	private static GridPane gridPane = new GridPane();
	
	private static Group holder = new Group();
	private static Group newSZholder = new Group();
	private static Group curSZholder = new Group();
	
	private static TextField y1Field = new TextField("0");
	private static TextField y2Field = new TextField("255");
	private static TextField newNameField = new TextField("");
	
	private static Label newNameLabel = new Label("Name: ");
	private static Label x1Field = new Label("");
	private static Label z1Field = new Label("");
	private static Label x2Field = new Label("");
	private static Label z2Field = new Label("");

	private static Label y1Label = new Label("Y1: ");
	private static Label y2Label = new Label("Y2: ");
	private static Label x1Label = new Label("X1: ");
	private static Label z1Label = new Label("Z1: ");
	private static Label x2Label = new Label("X2: ");
	private static Label z2Label = new Label("Z2: ");
	private static Label coordinateLabel = new Label();
	
	private static Button importConfig = new Button("Import Residences");
	private static Button setXZ = new Button("Set Area");
	private static Button createSZ = new Button("Create Residence/Area");
	private static Button removeButton = new Button("Remove Area");
	private static Button create = new Button("Create");
	private static Button removeResidence = new Button("Remove Residence");
	private static Button removeResidenceArea = new Button("Remove Area");
	private static Button printCmds = new Button("Print Commands");
	
	//shapes
	
	private static Line line1;
	private static Line line2;
	private static Line line3;
	private static Line line4;
	
	//styles
	
	private final static String redCSS = "-fx-stroke: red; -fx-stroke-width: 2";
	private final static String yellowCSS = "-fx-stroke: yellow; -fx-stroke-width: 2";
	private final static String orangeRedCSS = "-fx-stroke: orangered; -fx-stroke-width: 2";
	private final static String blueCSS = "-fx-stroke: indigo; -fx-stroke-width: 2";
	
	//others
	
	private static Point2D savedPoint = null;
	private static int i = 0;
	
	private static ResCmds allCmds = new ResCmds();
	
	//START
	
	@Override
	public void start(Stage primaryStage) throws Exception {

	//Get directory of .jar	
	dir	= System.getProperty("user.dir");
	
	//Set Screen Scales
	
	contextMenuLeft.setPadding(new Insets(10,10,10,10));
	contextMenuLeft.setMinWidth(250);
	createSZ.setDisable(true);
	removeResidence.setDisable(true);
	removeResidenceArea.setDisable(true);
	printCmds.setDisable(true);
	
	//Bindings

	removeResidence.disableProperty().bind(Bindings.isEmpty(listViewRes.getSelectionModel().getSelectedItems())); //activates the button if item(s) in listViewCur are selected
	removeResidenceArea.disableProperty().bind(Bindings.isEmpty(listViewAreas.getSelectionModel().getSelectedItems()));
	
	//Scene
		
	Scene scene = new Scene(root, screenX, screenY);
	
	//Eventhandling
	
	//Import Safezones - DONE
	importConfig.setOnMouseClicked(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			
			createSZ.setDisable(false);
			updateYAML();
			importResidences();			
			updateListViewRes();
			importConfig.setDisable(true);			
		}
	});
	
	//Remove current residence - DONE
	removeResidence.setOnMouseClicked(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			
			int index = listViewRes.getSelectionModel().getSelectedIndex();
			
			allCmds.removeResidence(allRes.get(index).getResName());;
			updatePrintCmdButton();
			
			int h = getFirstLineIndex(index, allRes);
			
			for(int k = 0; k < allRes.get(index).getAreaLinesSize();k++) {
				
				curSZholder.getChildren().remove(h);
			}
						
			allRes.remove(index);
			updateListViewRes();
			listViewAreas.getItems().clear();
		}
	});
		
	//Remove current area - DONE
	removeResidenceArea.setOnMouseClicked(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			
			int indexRes = listViewRes.getSelectionModel().getSelectedIndex();
			int indexArea = listViewAreas.getSelectionModel().getSelectedIndex();		
			int h = getFirstLineIndex(indexRes, allRes) + indexArea*4;
			
			if(allRes.get(indexRes).getAreaCoordinatesSize() > 1) {
				
				allCmds.removeArea(allRes.get(indexRes).getResName(), allRes.get(indexRes).getAreaNames().get(indexArea));
				updatePrintCmdButton();
				
				for(int k = 0; k < 4;k++) {
					
					curSZholder.getChildren().remove(h);
					allRes.get(indexRes).getAreaLines().remove(indexArea*4);
				}
							
				allRes.get(indexRes).getAreaCoordinates().remove(indexArea);
				allRes.get(indexRes).getAreaCoordinatesConv().remove(indexArea);
				allRes.get(indexRes).getAreaNames().remove(indexArea);
				
				updateListViewRes();
				updateListViewAreas(indexRes);
				
			} else {
				
				Alert alert = new Alert(AlertType.ERROR, "You cannot remove the last area of a residence. Please click on the residence and remove it completely.", ButtonType.OK);
				alert.initOwner(primaryStage);
				alert.showAndWait();
			}
		}
	});
	
	//Set Coordinates - DONE
	setXZ.setOnMouseClicked(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			
			setXZ.setDisable(true);
			holder.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent e) {
								
					switch(e.getButton()) {
					case PRIMARY:
						
						handlePrimaryClick(e);
						i++;
						if (i > 1) {
							resetNewSZMouseEvent();
						}
						break;
					default:
						break;
					}				
				}
			});
		}
	});
	
	//open create new residence window - DONE
	createSZ.setOnMouseClicked(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			
			showHideStage(newSZScreen);
		}
	});

	//move on holder (ScrollPane) - DONE
	holder.setOnMouseMoved(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			
			double x = (event.getX()-halfWidth) * conversionFactor;
			double z = (event.getY()-halfWidth) * conversionFactor;
			coordinateLabel.setText("X: " + x + " Z: " + z);
			List<Line> allResLines;
			checkOnLine(event.getX(),event.getY(),allLinesNew, redCSS, yellowCSS);
			checkOnLine(event.getX(),event.getY(),allResLines = getAllResLines(), blueCSS, orangeRedCSS);
		}}
	);
		
	//create residence - DONE
	create.setOnMouseClicked(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent e) {
					
			//check validity
			boolean emptyFields = hasEmptyFields();
			boolean illegalIntersect = hasIllegalIntersection();
			
			if((emptyFields) || (illegalIntersect)) {
						
				//new residence or area cannot be created
				String errorMsg = new String();
				
				if(emptyFields && illegalIntersect) {
					
					errorMsg = "There are both illegal intersections and empty fields. Please redraw the areas and fill out all the fields.";
					
				} else if(emptyFields) {
					
					errorMsg = "There are empty fields. Please fill out all the fields.";
					
				} else if(illegalIntersect) {
					
					errorMsg = "There are illegal intersections. Please redraw the areas.";		
				}
				
				Alert alert = new Alert(AlertType.ERROR, errorMsg, ButtonType.OK);
				alert.initOwner(newSZScreen);
				alert.showAndWait();

			} else {
				
				//check if area or new residence
				List<String> allResNames = getAllResNames();
								
				boolean isArea = allResNames.contains(newNameField.getText());
				int countAreas = Collections.frequency(allResNames, newNameField.getText());
				int resIndex = getIndexofResName(newNameField.getText());
				
				if(isArea) {
									
					for(int[] array : newSafeZoneCoord) {
						
						countAreas++;
						allRes.get(resIndex).addAreaCoordinates(array);
						allRes.get(resIndex).addAreaCoordinatesConv(array,conversionFactor,halfWidth);
						allRes.get(resIndex).addAreaName("area" + countAreas);
						allCmds.addArea(newNameField.getText(), countAreas, array[0], array[1], array[2], array[3], array[4], array[5]);
					}
					
					allRes.get(resIndex).addAreaLines(newSafeZoneCoordConv);					
					updateMap();
					
				} else {
					
					Residence newRes = new Residence();
					allRes.add(newRes);
					newRes.setResName(newNameField.getText());
					
					for(int[] array : newSafeZoneCoord) { //mc
						
						countAreas++;
						newRes.addAreaCoordinates(array); //mc
						newRes.addAreaCoordinatesConv(array,conversionFactor,halfWidth); //program
						newRes.addAreaName("area" + countAreas);
						allCmds.createResidence(newNameField.getText(), array[0], array[1], array[2], array[3], array[4], array[5]);
					}
															
					newRes.addAreaLines(newSafeZoneCoordConv);
					updateMap();
				}
					
				//update listView				
				updateListViewRes();
				updateListViewAreas(resIndex);
								
				//update Print Commands button
				updatePrintCmdButton();
				
				//kill create new residence process
				killNewSZ();
				
				//close window
				newSZScreen.close();	
			}
		}
	});
	
	//remove new safezone - DONE
	removeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			
			int index = listViewNew.getSelectionModel().getSelectedIndex();
			
			//remove double[] from safeZoneCoords
			
			newSafeZoneCoord.remove(index);
			newSafeZoneCoordString.remove(index);
			newSafeZoneCoordConv.remove(index);
			
			//remove lines from allLinesNew & newSZholder
			
			for(int i = 1; i<= 4;i++) {
				allLinesNew.remove(index*4);
				newSZholder.getChildren().remove(index*4);
			}
							
			ObservableList<String> obsSafeZoneCoord = FXCollections.observableArrayList(newSafeZoneCoordString);
			listViewNew.setItems(obsSafeZoneCoord);
			
		}
	});
	
	//print all commands
	printCmds.setOnMouseClicked(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			
			allCmds.printCmds();
		}
	});
	
	//New safezones ListView
	listViewNew.setOnMouseClicked(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent arg0) {
			
			for (Line line : allLinesNew) {
				line.setStyle(yellowCSS);
			}
			int index = listViewNew.getSelectionModel().getSelectedIndex(); //get selected item			
			int j = 0;
			
			if(index >= 0) { //to avoid an error in case an empty listview is clicked
				for(int i = 1; i<= 4;i++) {
					allLinesNew.get(index*4+j).setStyle(redCSS);
					j++;
				}
			}
		}
	});
	
	//Current safezones ListView
	listViewRes.setOnMouseClicked(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent arg0) {
			
			for(Residence r : allRes) {
				
				for(Line line : r.getAreaLines()) {
					line.setStyle(orangeRedCSS);
				}	
			}
						
			int index = listViewRes.getSelectionModel().getSelectedIndex(); //get selected item
									
			if(index >= 0) { //to avoid an error in case an empty listview is clicked
				
				for(Line line : allRes.get(index).getAreaLines()) {
					
					line.setStyle(blueCSS);
				}
			}
			
			updateListViewAreas(index);
		}
	});
	
	//Current areas ListView
	listViewAreas.setOnMouseClicked(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent arg0) {
			
			int indexRes = listViewRes.getSelectionModel().getSelectedIndex(); //get index of area
			int indexArea = listViewAreas.getSelectionModel().getSelectedIndex();
			
			for(Line line : allRes.get(indexRes).getAreaLines()) {
				
				line.setStyle(blueCSS);
			}	
			
			for(int l = 0; l < 4;l++) {

				allRes.get(indexRes).getAreaLines().get(indexArea*4+l).setStyle(yellowCSS);;				
			}
		}
	});
	
	//Get map
	ImageView mapView = updateImageView();
	//ImageView mapView = new ImageView(new Image("Resources/imageView.jpg"));

	//Add elements
	removeResArea.getChildren().addAll(removeResidence, removeResidenceArea);
	
	contextMenuLeft.getChildren().addAll(importConfig, listViewRes, listViewAreas, removeResArea, createSZ, printCmds, coordinateLabel);
	
	holder.getChildren().addAll(mapView,curSZholder, newSZholder);
	
	scrollPane.setContent(holder);
	scrollPane.setPannable(true);
	
	root.getChildren().addAll(contextMenuLeft, scrollPane);

	//Stage
	
	primaryStage.setTitle("Minecraft Safezone Creator");
	primaryStage.getIcons().add(new Image("Resources/icon.png"));
	primaryStage.setScene(scene);
	primaryStage.show();
	primaryStage.setMaximized(true);
	primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

		@Override
		public void handle(WindowEvent arg0) {
			
			//close other windows with the primary window
			newSZScreen.close();
		}
	});	
	
	//Secondary Stages
	
	createNewSZScreen();
	
	}
	
	//METHODS
	
	//create new safezone
	public static void handlePrimaryClick(MouseEvent mouseEvent) {
	
		Point2D clickPoint = new Point2D(mouseEvent.getX(),mouseEvent.getY());
		if(savedPoint == null) {
			
			savedPoint = clickPoint;
			updateFirstPoint(savedPoint);
			
		} else {
			
			updateSecondPoint(clickPoint);
			drawSafeZone(savedPoint,clickPoint);				//add lines and add them to Line ArrayList
			addSafeZone(newSafeZoneCoord, savedPoint, clickPoint, newSafeZoneCoordString, newSafeZoneCoordConv); //fill Double[] array with each safezones' corner points
			
			savedPoint = null;	
		}
	}
	
	//update first point labels
	public static void updateFirstPoint (Point2D p1) {
		
		double x1 = (p1.getX() - halfWidth) * conversionFactor;
		double z1 = (p1.getY() - halfWidth) * conversionFactor;
		
		int x1Int = (int) x1;
		int z1Int = (int) z1;
		
		x1Field.setText(x1Int+"");
		z1Field.setText(z1Int+"");
	}
	
	//update second point labels
	public static void updateSecondPoint (Point2D p2) {
		
		double x2 = (p2.getX() - halfWidth) * conversionFactor;
		double z2 = (p2.getY() - halfWidth) * conversionFactor;
		
		int x2Int = (int) x2;
		int z2Int = (int) z2;
		
		x2Field.setText(x2Int+"");
		z2Field.setText(z2Int+"");
	}
	
	//draw new safezone in newSZholder and save the lines in allLinesNew
	public static void drawSafeZone (Point2D p1, Point2D p2) {
				
		line1 = new Line(p1.getX(),p1.getY(),p1.getX(),p2.getY());
		line2 = new Line(p1.getX(),p1.getY(),p2.getX(),p1.getY());
		line3 = new Line(p2.getX(),p2.getY(),p1.getX(),p2.getY());
		line4 = new Line(p2.getX(),p2.getY(),p2.getX(),p1.getY());
		
		allLinesNew.add(line1); //add lines to array
		allLinesNew.add(line2);
		allLinesNew.add(line3);
		allLinesNew.add(line4);
		
		for(Line line : allLinesNew) {
			
			line.setStyle(yellowCSS);
		}
		
		newSZholder.getChildren().addAll(line1,line2,line3,line4); //show them on the scene
	}
	
	//add new safezone to ArrayList<Double[]> and ArrayList<String> (converted to Minecraft coordinates)
	public static void addSafeZone(List<int[]> intArrayCoord, Point2D p1, Point2D p2, List<String> stringArray, List<int[]> intArrayCoordConv) {
		
		//add them to converted array first and then calculate!
		double x1 = p1.getX();
		double z1 = p1.getY();
		double y1 = Double.parseDouble(y1Field.getText());
		double x2 = p2.getX();
		double z2 = p2.getY();
		double y2 = Double.parseDouble(y2Field.getText());
		
		int[] newSZconverted = new int[] {(int) x1,(int) y1,(int) z1,(int) x2,(int) y2,(int) z2};
				
		intArrayCoordConv.add(newSZconverted);
		
		x1 = (p1.getX() - halfWidth) * conversionFactor;
		z1 = (p1.getY() - halfWidth) * conversionFactor;
		x2 = (p2.getX() - halfWidth) * conversionFactor;
		z2 = (p2.getY() - halfWidth) * conversionFactor;
		
		int[] sz = new int[] {(int) x1,(int) y1,(int) z1,(int) x2,(int) y2,(int) z2};
		String szString = Arrays.toString(sz);
		stringArray.add(szString);
		intArrayCoord.add(sz);		
	}
	
	//reset create new safezone mouse event
	public static void resetNewSZMouseEvent() {
		i = 0;
		holder.setOnMouseClicked(null);
		setXZ.setDisable(false);
		ObservableList<String> obsSafeZoneCoord = FXCollections.observableArrayList(newSafeZoneCoordString);
		listViewNew.setItems(obsSafeZoneCoord);
		savedPoint = null;
	}
		
	//check if the mouse is on a border of a safezone
	public static void checkOnLine(double x, double y, List<Line> lineArrayList, String style, String originStyle) {
		
		for (Line line : lineArrayList) {
			line.setStyle(originStyle);
		}
		
		for(Line line : lineArrayList) {
			
			boolean flag = false;
			
			if((x == line.getStartX() && y >= Math.min(line.getStartY(), line.getEndY()) && y <= Math.max(line.getStartY(), line.getEndY()))) {
				flag = true;
				
			} else if((y == line.getStartY() && x >= Math.min(line.getStartX(), line.getEndX()) && x <= Math.max(line.getStartX(), line.getEndX()))) {
				flag = true;
			}
			
			if(flag) {
				int mod = (lineArrayList.indexOf(line)+1) % 4;
				switch(mod) {
				case 1:
					
					line.setStyle(style);
					lineArrayList.get(lineArrayList.indexOf(line)+1).setStyle(style);
					lineArrayList.get(lineArrayList.indexOf(line)+2).setStyle(style);
					lineArrayList.get(lineArrayList.indexOf(line)+3).setStyle(style);				
					break;
				case 2:
					
					line.setStyle(style);
					lineArrayList.get(lineArrayList.indexOf(line)-1).setStyle(style);
					lineArrayList.get(lineArrayList.indexOf(line)+1).setStyle(style);
					lineArrayList.get(lineArrayList.indexOf(line)+2).setStyle(style);
					break;
				case 3:
					
					line.setStyle(style);
					lineArrayList.get(lineArrayList.indexOf(line)-2).setStyle(style);
					lineArrayList.get(lineArrayList.indexOf(line)-1).setStyle(style);
					lineArrayList.get(lineArrayList.indexOf(line)+1).setStyle(style);
					break;
				case 0:
					
					line.setStyle(style);
					lineArrayList.get(lineArrayList.indexOf(line)-3).setStyle(style);
					lineArrayList.get(lineArrayList.indexOf(line)-2).setStyle(style);
					lineArrayList.get(lineArrayList.indexOf(line)-1).setStyle(style);
					break;
				}
			}
		}
	}
			
	//create new safezone screen
	public static void createNewSZScreen() {
		
		Scene newSZScreenScene = new Scene(contextMenuNewSZ, 300, 500);
		
		//Build context menu
		contextMenuNewSZ.setPadding(new Insets(10,10,10,10));
		setRemove.setPadding(new Insets(10,10,10,10));
		gridPane.setPadding(new Insets(10,10,10,10));
		listViewNew.setPadding(new Insets(10,10,10,10));
		createHBox.setPadding(new Insets(10,10,10,10));
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		
		int h = 25;
		
		newNameLabel.setMinHeight(h);
		y1Label.setMinHeight(h);
		y2Label.setMinHeight(h);
		x1Label.setMinHeight(h);
		x2Label.setMinHeight(h);
		z1Label.setMinHeight(h);
		z2Label.setMinHeight(h);
		
		int w = 50;
		
		x1Field.setMaxWidth(w);
		z1Field.setMaxWidth(w);
		y1Field.setMaxWidth(w);
		x2Field.setMaxWidth(w);
		z2Field.setMaxWidth(w);
		y2Field.setMaxWidth(w);
		
		gridPane.add(x1Label, 0, 0);
		gridPane.add(z1Label, 0, 1);
		gridPane.add(y1Label, 0, 2);
		gridPane.add(x2Label, 2, 0);
		gridPane.add(z2Label, 2, 1);
		gridPane.add(y2Label, 2, 2);
		
		gridPane.add(x1Field, 1, 0);
		gridPane.add(z1Field, 1, 1);
		gridPane.add(y1Field, 1, 2);
		gridPane.add(x2Field, 3, 0);
		gridPane.add(z2Field, 3, 1);
		gridPane.add(y2Field, 3, 2);
		
		removeButton.setDisable(true);
		removeButton.disableProperty().bind(listViewNew.getSelectionModel().selectedItemProperty().isNull());
		
		setRemove.getChildren().addAll(setXZ, removeButton);
		createHBox.getChildren().addAll(create);
		contextMenuNewSZ.getChildren().addAll(newNameLabel, newNameField,gridPane, setRemove,listViewNew,createHBox);
				
		newSZScreen.setTitle("Create New Residence/Area");
		newSZScreen.getIcons().add(new Image("Resources/icon.png"));
		newSZScreen.setScene(newSZScreenScene);
		newSZScreen.setMaximized(false);
		newSZScreen.setAlwaysOnTop(true);
		newSZScreen.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				
				killNewSZ();
			}
		});	
	}
	
	//kill all traces of a new safezone (on closing or cancelling)
	public static void killNewSZ() {
		
		newSafeZoneCoord.clear();
		newSafeZoneCoordString.clear();
		newSafeZoneCoordConv.clear();
		allLinesNew.clear();
		newSZholder.getChildren().clear();
		resetNewSZMouseEvent();
	}
	
	public static void showHideStage(Stage stage) {
		
		if(stage.isShowing()) {
			
			stage.hide();
			
		} else {
			
			stage.show();
		}
	}
	
	public boolean hasEmptyFields() {
		
		boolean hasEmptyFields = false;
		
		if (newNameField.getText().isEmpty()) {
			hasEmptyFields = true;
		}
		
		if (y1Field.getText().isEmpty()) {
			hasEmptyFields = true;
		}
		
		if (y2Field.getText().isEmpty()) {
			hasEmptyFields = true;
		}
		
		if (x1Field.getText().isEmpty()) {
			hasEmptyFields = true;
		}
		
		if (x2Field.getText().isEmpty()) {
			hasEmptyFields = true;
		}

		return hasEmptyFields;
	}
	
	public boolean hasIllegalIntersection() { //check if there is no intersection with the current residence but the same name = illegal
		
		boolean illegal = false;
		int i = 0;
		
		
		//WORK TO DO
		List<String> allAreasResNames = new ArrayList<String>();
		List<int[]> allAreas = new ArrayList<int[]>();
		
		for(Residence r : allRes) {
			for (int[] coordsConv : r.getAreaCoordinatesConv()) {
				allAreas.add(coordsConv);
				allAreasResNames.add(r.getResName());
			}
		}
		
		for(int[] newSZ : newSafeZoneCoordConv) { //test each new area
			for(int[] curSZ : allAreas) { //test if new area intersects with existing ones, except if area is added to existing safezone!
				
				Rectangle rect1 = new Rectangle(Math.min(newSZ[0], newSZ[3]),Math.min(newSZ[2], newSZ[5]),Math.max(newSZ[0], newSZ[3])-Math.min(newSZ[0], newSZ[3]),Math.max(newSZ[2], newSZ[5])-Math.min(newSZ[2], newSZ[5]));
				Rectangle rect2 = new Rectangle(Math.min(curSZ[0], curSZ[3]),Math.min(curSZ[2], curSZ[5]),Math.max(curSZ[0], curSZ[3])-Math.min(curSZ[0], curSZ[3]),Math.max(curSZ[2], curSZ[5])-Math.min(curSZ[2], curSZ[5]));
				            
				Shape intersect = Shape.intersect(rect1, rect2);
								
				if (intersect.getBoundsInParent().getWidth() > 0) { //intersection
					
					System.out.println(true);
					
					if(newNameField.getText().equals(allAreasResNames.get(allAreas.indexOf(curSZ))) == false) {
						
						illegal = true; //if intersection (not with the same residence)
					} else {
						
						i++; //count of legal intersections (for below)
					}
					
				} 
			}
		}
		
		if(allAreasResNames.contains(newNameField.getText()) && i == 0) { //if the residence already exists but there is no intersection with it, then it is illegal
			
			illegal = true;
		}
		
		return illegal;
	}
	
	public void updatePrintCmdButton() {
		printCmds.disableProperty().bind(Bindings.size(allCmds.returnAllCmdsObs()).greaterThan(0).not());
	}
	
	public void importResidences() {
		
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		
		Map yaml;
		try {
			String fileName = "\\src\\main\\java\\Resources\\res_world5.yml";			
			yaml = mapper.readValue(new File(dir + fileName), Map.class);

			for (Object resworld5 : yaml.keySet()) {
			    					
				if(resworld5.equals("Residences")) {
					
					for(Object residence : ((Map) yaml.get(resworld5)).keySet()) { // returns all the residences, residence = residence name (e.g. world5_Suyeon)
						
						//create instance of class Residence for each residence in the file
						if(((String) residence).equals("world5_Worldborder")) { //exclude worldborder for now
							
						} else {
							
							Residence r = new Residence();
							allRes.add(r);
							r.setResName((String) residence);
							
							for(Object feature : ((Map)((Map) yaml.get(resworld5)).get(residence)).keySet()) { // returns all the features of the residence, feature = feature name (e.g. Areas)
								
								if(feature.equals("Areas")) {
									
									for(Object area : ((Map)((Map)((Map) yaml.get(resworld5)).get(residence)).get(feature)).keySet()) {
										
										r.addAreaName((String)area);
										
										int j = 0;
										int[] allCoords = new int[6];
										
										for(Object coord : ((Map)((Map)((Map)((Map) yaml.get(resworld5)).get(residence)).get(feature)).get(area)).values()) {
											
											allCoords[j] = (int) coord;
											j++;
										}
									
										r.addAreaCoordinates(allCoords);
										r.addAreaCoordinatesConv(allCoords, conversionFactor, halfWidth);
										
									}
									
									r.addAreaLines(r.getAreaCoordinatesConv());	//when all areas are set
									
								} else if (feature.equals("Permissions")) {
									
									for(Object permissions : ((Map)((Map)((Map) yaml.get(resworld5)).get(residence)).get(feature)).keySet()) {
										
										if(permissions.equals("AreaFlags")) {
											
											for(Object areaFlags : ((Map)((Map)((Map)((Map) yaml.get(resworld5)).get(residence)).get(feature)).get(permissions)).keySet()) {
												
												if(areaFlags.equals("fly")) {
													
													r.setFly((boolean) ((Map)((Map)((Map)((Map) yaml.get(resworld5)).get(residence)).get(feature)).get(permissions)).get(areaFlags));
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
		} catch (JsonParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//update map
		updateMap();
	}
	
	public void updateListViewRes() {
				
		List<String> resNames = new ArrayList<String>();
		
		for(Residence r : allRes) {
			
			resNames.add(r.getResName());
		}
		
		ObservableList<String> obs = FXCollections.observableArrayList(resNames);
		listViewRes.setItems(obs);
	}
	
	public void updateListViewAreas(int index) {
				
		List<String> areaNames = new ArrayList<String>();
		
		for(String area : allRes.get(index).getAreaNames()) {
			
			areaNames.add(area);
		}
		
		ObservableList<String> obs = FXCollections.observableArrayList(areaNames);
		listViewAreas.setItems(obs);	
	}
	
	//get index of the first line in all residences (to access each line in curSZholder!)
	public int getFirstLineIndex(int listViewIndex, List<Residence> residences) {
				
		int i = 0; //position of residence
		int j = 0; //index of residence lines in curSZholder
			
		while(i<listViewIndex) {
			
			j = j + residences.get(i).getAreaLinesSize();
			i++;
		}
		
		return j;
	}
	
	//Residence Getters
	public List<String> getAllResNames() {
		
		List<String> allResNames = new ArrayList<String>();
		
		for(Residence r : allRes) {
			
			for(int[] area : r.getAreaCoordinatesConv()) {
				allResNames.add(r.getResName());
			}
		}
		return allResNames;
	}
	
	public int getIndexofResName(String name) {
		
		int index = 0;
		int i = 0;
		
		for(Residence r : allRes) {

			if(r.getResName().equals(name)) {
				index = i;
			}
			i++;
		}
		
		return index;
	}
	
	public List<Line> getAllResLines() {
		
		List<Line> allResLines = new ArrayList<Line>();
		
		for(Residence r : allRes) {
			
			for(Line line : r.getAreaLines()) {
				allResLines.add(line);
			}
		}
		
		return allResLines;
	}
	
	public void updateMap() {
		
		curSZholder.getChildren().clear();
		for(Residence r : allRes) {
			
			for(Line line : r.getAreaLines()) {
				curSZholder.getChildren().add(line);
			}
		}
	}
	
	public void updateYAML() {
				
		try {
			
			URL url = new URL("https://static.minecracy.de/dl/Safezones/res_world5.yml");
			InputStream in = url.openStream();
			String fileName = "\\src\\main\\java\\Resources\\res_world5.yml";			
			File file = new File(dir + fileName);
			FileOutputStream out = new FileOutputStream(file, false);
		    int read;
		    byte[] bytes = new byte[8192];
		    
		    while ((read = in.read(bytes)) != -1) {
		    	
		                out.write(bytes, 0, read);
            }
		    
		    out.close(); 
			
		} catch (IOException e) {
		   // handle exception
		}
	}
	
	public ImageView updateImageView() {
		
		ImageView mapView = new ImageView();
		
		try {
			
			URL url = new URL("https://maps.minecracy.de/world5/current-5000px.jpg");
			InputStream in = url.openStream();
			String fileName = "\\src\\main\\java\\Resources\\imageView.jpg";			
			File file = new File(dir + fileName);			
			FileOutputStream out = new FileOutputStream(file, false);
		    int read;
		    byte[] bytes = new byte[8192];
		    
		    while ((read = in.read(bytes)) != -1) {
		    	
		                out.write(bytes, 0, read);
            }
		    
		    out.close();
		    
		    
		    mapView = new ImageView(new Image("file:" + dir + fileName));
			
		} catch (IOException e) {
		   // handle exception
			System.out.print("didnt work");
		}	
		
		return mapView;
	}
	
	//MAIN
	
	public static void main(String[] args) {
		launch(args);
	}

}
