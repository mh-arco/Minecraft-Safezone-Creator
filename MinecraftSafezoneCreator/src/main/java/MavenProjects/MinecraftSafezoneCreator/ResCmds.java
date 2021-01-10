package MavenProjects.MinecraftSafezoneCreator;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ResCmds {
	
	List<ArrayList<String>> allCmds = new ArrayList<ArrayList<String>>();
	String lastCmd = "";
	
	public void createResidence(String name, int x1, int y1, int z1, int x2, int y2, int z2) { //includes first area
				
		ArrayList<String> newResidence = new ArrayList<String>();
		
		String desc = "Create residence:";
		String pos1 = "//pos1 " + x1 + " " + y1 + " " + z1;
		String pos2 = "//pos2 " + x2 + " " + y2 + " " + z2;
		String worldEdit = "/res select worldedit";
		String selectVert = "/res select vert";
		String createRes = "/res create world5_" + name;
		
		if(!lastCmd.equals(desc)) {
			
			newResidence.add(desc);
		}
		
		newResidence.add(pos1);
		newResidence.add(pos2);
		newResidence.add(worldEdit);
		newResidence.add(selectVert);
		newResidence.add(createRes);
		
		allCmds.add(newResidence);
		lastCmd = desc;
	}
	
	public void addArea(String name, int areaNo, int x1, int y1, int z1, int x2, int y2, int z2) {
		
		ArrayList<String> addArea = new ArrayList<String>();
		
		String desc = "Add area:";
		String pos1 = "//pos1 " + x1 + " " + y1 + " " + z1;
		String pos2 = "//pos2 " + x2 + " " + y2 + " " + z2;
		String worldEdit = "/res select worldedit";
		String selectVert = "/res select vert";
		String createArea = "/res area add world5_" + name + " " + "Area_" + areaNo;
		
		if(!lastCmd.equals(desc)) {
			
			addArea.add(desc);
		}
		
		addArea.add(pos1);
		addArea.add(pos2);
		addArea.add(worldEdit);
		addArea.add(selectVert);
		addArea.add(createArea);
		
		allCmds.add(addArea);
		lastCmd = desc;
	}
	
	public void removeResidence(String resName) {
		
		ArrayList<String> removeResidence = new ArrayList<String>();
		
		String desc = "Remove residence:";
		String delRes = "/res remove " + resName;
		String confirmation = "/res confirm";
		
		if(!lastCmd.equals(desc)) {
			
			removeResidence.add(desc);
		}
		
		removeResidence.add(delRes);
		removeResidence.add(confirmation);
		
		allCmds.add(removeResidence);
		lastCmd = desc;
	}
	
	public void removeArea(String resName, String areaName) {
		
		ArrayList<String> removeArea = new ArrayList<String>();
		
		String desc = "Remove area:";
		String delArea = "/res area remove " + resName + " " + areaName;
		
		if(!lastCmd.equals(desc)) {
			
			removeArea.add(desc);
		}
		
		removeArea.add(delArea);
		
		allCmds.add(removeArea);
		lastCmd = desc;
	}
	
	public void removeCmdAtIndex(int index) {
		allCmds.remove(index);
	}
	
	public List<ArrayList<String>> returnAllCmds() {
		
		return allCmds;
		
	}
	
	public ObservableList<ArrayList<String>> returnAllCmdsObs() {
		
		ObservableList<ArrayList<String>> allCmdsObs = FXCollections.observableArrayList(allCmds);
		return allCmdsObs;
		
	}
	
	public void printCmds() {
		
		try {
			String dir	= System.getProperty("user.dir");
			FileWriter message = new FileWriter(dir + "\\allCommands_MCSC.txt");
		      
		    for(ArrayList<String> cmd : allCmds) {
		    	  
		    	  for(String lineCmd : cmd) {
		    		  
		    		  message.write(lineCmd);
			    	  message.append(System.getProperty("line.separator"));
		    	  }
		    	  
		    	  message.append(System.getProperty("line.separator"));
		    	  
		      }
		      
		      message.close();
		      
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	
}
