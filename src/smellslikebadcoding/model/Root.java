package smellslikebadcoding.model;

import java.util.ArrayList;

import detectors.Detector;


/**
 * 
 * class that represents the root of the tree within the statistic view.
 * Its children are the detectors that the user selected within the preference
 * page
 *
 */
public class Root {
	
	private ArrayList<Detector> children;
	private String name;
	
	public Root(ArrayList<Detector> children){
		this.children = children;
		this.name = "root";
	}
	
	public ArrayList<Detector> getChildren(){
		return this.children;
	}
	
	public void addChildren(Detector d){
		this.children.add(d);
	}
	
	@Override
	public String toString(){
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Root other = (Root) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
