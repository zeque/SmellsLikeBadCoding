package reports;

import java.util.ArrayList;

//import smellslikebadcoding.Statistic;
import detectors.Detector;



public abstract class Report{

	protected ArrayList<ReportableElement> toReport;
	protected String name;
	protected ArrayList<Detector> detectores;
	
	public ArrayList<Detector> getDetectores() {
		return detectores;
	}

	public void setDetectores(ArrayList<Detector> detectores) {
		this.detectores = detectores;
	}
	public Report() {
	
	}
	

	public void setReportableElementList(ArrayList<ReportableElement> l) {
		this.toReport = new ArrayList<ReportableElement>(l);
	}

	public ArrayList<ReportableElement> getReportableElements() {
		return this.toReport;
	}
	
	@Override
	public String toString(){
		return this.name;
	}

	
	
}
