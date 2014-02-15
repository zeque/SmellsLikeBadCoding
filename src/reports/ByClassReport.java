package reports;

import java.util.ArrayList;

import detectors.Detector;


public class ByClassReport extends Report{
	
	private ArrayList<ReportableElement> children;
	private ByPackageReport parent;
	
	private Detector detector;
	private String name;
	private int badM;
	private int goodM;
	private String toShow;
	public String v_percent;
	public String v_cant;
		
	public ArrayList<ReportableElement> getChildren() {
		return children;
	}

	/**
	 * 
	 * @param d the detector to report
	 * @param name the name of the class
	 * @param i the amount of problematic methods of the class
	 * @param j	the amount of methods of the class
	 */
	public ByClassReport(Detector d, String name, int i, int j) {
		this.detector = d;
		this.name = name;
		this.badM = i;
		this.goodM = j;
		this.children = new ArrayList<ReportableElement>();
		
		//obtain the percentage of problematic methods
		double percent = ((double)(this.badM) / (double)(this.goodM)) * 100;
		String spercent = "" + percent;
		spercent = (spercent.contains(".")) ?  spercent.substring(0,spercent.indexOf(".") + 2) : spercent;
		
		//variable de retorno
		v_percent = spercent+"%"; 
		v_cant =  "" + this.badM;
		
		toShow = this.name;   //  + " [ " + this.badM + " ]"+" ("+ spercent +"%)
		
	}
	
	public void setChildren(ArrayList<ReportableElement> children){
		this.children = children;
	}

	public ByPackageReport getParent() {
		return parent;
	}

	public void setParent(ByPackageReport parent) {
		this.parent = parent;
	}
	
	public String toString(){
		return toShow; //toShow; 
	}

	// retorna porcentaje
	
	public String getPercent(){
		return v_percent;
	}
	
	// retorna cantidad
	
	public String getCantidad(){
		return v_cant;
	}
}
