package reports;

import java.util.ArrayList;

import org.eclipse.jdt.core.IPackageFragment;

import detectors.Detector;


public class ByPackageReport extends Report {
	
	private Detector d;
	private int badMethodsxPackage;
	private int methodsXPackage;
	
	private Detector parent;
	private ArrayList<ByClassReport> children; 
	

	private String namePackage;
	
	private String nameToShow;
	

	/**
	 * 
	 * @param d the detector to report
	 * @param name the name of the package
	 * @param i the amount of problematic methods of the package
	 * @param j	the amount of methods of the package
	 */
	public ByPackageReport(Detector d, String namePackage, int badM, int goodM) {
		this.d = d;
		this.badMethodsxPackage = badM;
		this.methodsXPackage = goodM;
		this.namePackage = namePackage;
		this.children = new ArrayList<ByClassReport>();
		
		double percent = ((double)this.badMethodsxPackage / (double)this.methodsXPackage) * 100;
		String spercent = "" + percent;
		spercent = (spercent.contains(".")) ?  spercent.substring(0,spercent.indexOf(".") + 2) : spercent;
		
		nameToShow = this.namePackage + " [ " +  this.badMethodsxPackage + " ("+ spercent +"%) ]";
	}

	public String toString(){
		return this.nameToShow; 
	}
	
	
	
	public Detector getParent() {
		return parent;
	}

	/**
	 * set the detector parent for this report in the 
	 * statistic tree
	 * @param parent
	 */
	public void setParent(Detector parent) {
		this.parent = parent;
	}
	
	/**
	 * set the report children for this report in the 
	 * statistic tree
	 * @param parent
	 */
	public void addChildren(ByClassReport r){
		this.children.add(r);
	}
	
	public ArrayList<ByClassReport> getChildren() {
		return children;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result
				+ ((nameToShow == null) ? 0 : nameToShow.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
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
		ByPackageReport other = (ByPackageReport) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (nameToShow == null) {
			if (other.nameToShow != null)
				return false;
		} else if (!nameToShow.equals(other.nameToShow))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}
	


}
