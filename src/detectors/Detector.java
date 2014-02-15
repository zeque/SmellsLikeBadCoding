package detectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;
import org.eclipse.ui.internal.ReopenEditorMenu;
import org.omg.PortableInterceptor.INACTIVE;

import filters.IFilter;
import reports.Method;
import reports.Report;
import reports.ReportableElement;
//import smellslikebadcoding.Statistic;
import smellslikebadcoding.builder.SmellsLikeBadCodingBuilder;
import smellslikebadcoding.model.Root;

public abstract class Detector{


	protected String type;
	protected String v_cantidad;
	protected String v_percent;
	protected ArrayList<Method> smellyMethods;
	
	//PARA LA JERARQUIA DE ARBOL
	protected ArrayList<Report> children;
	protected Root parent;
	
	
	protected SmellsLikeBadCodingBuilder builder; 
	protected ArrayList<Report> reports;
	protected String name;
	protected String project;
	protected HashMap<IPackageFragment, Integer> methodsPerPackage;
	protected HashMap<IType, Integer> methodsPerClass;
	protected ArrayList<IFile> files;

	
	public void setCantidad(String c){
		v_cantidad = c;
	}

	public void setPercent(String p){
		v_percent = p;
	}
	
	public String getCantidad(){
		return this.v_cantidad;
	}
	
	public String getPercent(){
		return this.v_percent;
	}
	
	
	
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getName() {
		return name;
	}

	public Root getParent() {
		return parent;
	}

	public void setParent(Root parent) {
		this.parent = parent;
	}
	
	
	public void addReport(Report r){
		this.reports.add(r);
	}
	
	public ArrayList<Report> getReports(){
		return this.reports;
	}
	
	public SmellsLikeBadCodingBuilder getBuilder(){
		return this.builder;
	}
	
	/**
	 * Convenience constructor needed when you already know the children for a given
	 * Detector
	 * @param b the builder
	 * @param children the childrens
	 * @param type the name of the constructor, used to tell this apart from the other
	 * 		  detector in the same tree
	 */
	public Detector(SmellsLikeBadCodingBuilder b, ArrayList<Report> children, String type){

		this.builder = b;
		this.children = children;
		this.type = type;
		this.smellyMethods = new ArrayList<Method>();
		this.methodsPerClass = new HashMap<IType, Integer>();
		this.methodsPerPackage = new HashMap<IPackageFragment, Integer>();
		this.files = new ArrayList<IFile>();
	}

	/**
	 * Convenience constructor needed when you don't know the children for a given
	 * Detector
	 * @param b the builder
	 * @param children the childrens
	 * @param type the name of the constructor, used to tell this apart from the other
	 * 		  detector in the same tree
	 */
	public Detector(SmellsLikeBadCodingBuilder b, String type){

		this.builder = b;
		this.children = new ArrayList<Report>();
		this.type = type;
		this.smellyMethods = new ArrayList<Method>();
		this.reports = new ArrayList<Report>();
		this.methodsPerClass = new HashMap<IType, Integer>();
		this.methodsPerPackage = new HashMap<IPackageFragment, Integer>();
		this.files = new ArrayList<IFile>();
	}
	
	public abstract ArrayList<ReportableElement> detect(IProject p, IFilter f, IProgressMonitor monitor);
	
	public void setChildren(ArrayList<Report> r){
		this.children = r;
	}
	
	public ArrayList<Report> getChildren(){
		return this.children;
	}
	
	public String getType(){
		return this.type;
	}
	
	public ArrayList<Method> getSmellyMethods(){
		return this.smellyMethods;
	};
	 
	 @Override
	 public String toString(){
		 return this.type + " (" + this.project + ") ";
	 }
	 
			

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Detector other = (Detector) obj;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	/**
	 * Convenience method that helps to create a tree structure with
	 * the obtained data
	 * @param s is added to the Detector list of children within the tree
	 */
	public void addChildren(Report s){
		this.children.add(s);
	}

	public void setType(String string) {
			this.type = string;		
	}

	
	/**
	 * utility method that helps return the caller methods of a method
	 * 
	 */
	protected HashSet<IMethod> getIMethods(MethodWrapper[] methodWrappers) {
		HashSet<IMethod> c = new HashSet<IMethod>();
		for (MethodWrapper m : methodWrappers) {
			IMethod im = getIMethodFromMethodWrapper(m);
			if (im != null) {
				c.add(im);
			}
		}
		return c;
	}

	/**
	 * utility method that cast a MethodWrapper to a more friendly
	 * representation like IMethod
	 * 
	 */
	protected IMethod getIMethodFromMethodWrapper(MethodWrapper m) {
		try {
			IMember im = m.getMember();
			if (im.getElementType() == IJavaElement.METHOD) {
				return (IMethod) m.getMember();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
