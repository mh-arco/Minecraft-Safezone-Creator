package MavenProjects.MinecraftSafezoneCreator;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Line;

public class Residence {

	private String resName;
	private boolean fly;
	private ArrayList<String> areaNames = new ArrayList<String>();
	private ArrayList<int[]> areaCoordinates = new ArrayList<int[]>();
	private ArrayList<int[]> areaCoordinatesConv = new ArrayList<int[]>();
	private ArrayList<Line> areaLines = new ArrayList<Line>();
	
	private final static String orangeRedCSS = "-fx-stroke: orangered; -fx-stroke-width: 2";

	//Constructor
	public Residence() {
		
	}
	
	//Getter
	public String getResName() {
		return resName;
	}
	
	public boolean getFly() {
		return fly;
	}
	
	public ArrayList<String> getAreaNames() {
		return areaNames;
	}
	
	public int getAreaLinesSize() {
		return areaLines.size();
	}
	
	public ArrayList<int[]> getAreaCoordinates() {
		return areaCoordinates;
	}
	
	public int getAreaCoordinatesSize() {
		return areaCoordinates.size();
	}
	
	public ArrayList<int[]> getAreaCoordinatesConv() {
		return areaCoordinatesConv;
	}
	
	public int getAreaCoordinatesConvSize() {
		return areaCoordinatesConv.size();
	}
	
	public ArrayList<Line> getAreaLines() {
		return areaLines;
	}
		
	//Setter
	public String setResName(String name) {
		resName = name;
		return resName;
	}
	
	public boolean setFly(boolean isFly) {
		fly = isFly;
		return fly;
	}
	
	//Adder
	public void addAreaName(String name) {
		areaNames.add(name);
	}
	
	public void addAreaCoordinates(int[] coordinates) {
		areaCoordinates.add(coordinates);
	}
	
	public void addAreaCoordinatesConv(int[] coordinatesConv, double conversionFactor, double halfWidth) {
		
		int i = 0;
		int[] coordConv = {coordinatesConv[0],coordinatesConv[1],coordinatesConv[2],coordinatesConv[3],coordinatesConv[4],coordinatesConv[5]};

		for(int c : coordConv) {
			
			if(i == 1 || i == 4) {
				i++;
			} else {

				double d = (double) c;
				coordConv[i] = (int) Math.round((d/conversionFactor)+halfWidth);
				i++;
			}
		}
		areaCoordinatesConv.add(coordConv);
	}
	
	public void addAreaLines(List<int[]> areaCoordConv) {
		
		for(int[] sz : areaCoordConv) {
			
			Line line1 = new Line(sz[0],sz[2],sz[0],sz[5]);
			Line line2 = new Line(sz[0],sz[2],sz[3],sz[2]);
			Line line3 = new Line(sz[3],sz[5],sz[0],sz[5]);
			Line line4 = new Line(sz[3],sz[5],sz[3],sz[2]);
			
			line1.setStyle(orangeRedCSS);
			line2.setStyle(orangeRedCSS);
			line3.setStyle(orangeRedCSS);
			line4.setStyle(orangeRedCSS);
			
			areaLines.add(line1);
			areaLines.add(line2);
			areaLines.add(line3);
			areaLines.add(line4);
		}
	}
}
