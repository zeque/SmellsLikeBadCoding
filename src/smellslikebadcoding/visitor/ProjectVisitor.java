package smellslikebadcoding.visitor;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import reports.ByClassReport;
import reports.ByPackageReport;
import reports.ReportableElement;
import detectors.Detector;
import filters.IFilter;
//import reports.Method;


public class ProjectVisitor implements IResourceDeltaVisitor{
	
	private ArrayList<Detector> detectors;
	private IFilter scopef;
	private IProgressMonitor progress;
	private HashMap<IPackageFragment, Integer> methodsPerPackage;
	private HashMap<IType, Integer> methodsPerClass;
	
	public ProjectVisitor(ArrayList<Detector> d, IFilter f, IProgressMonitor monitor){
		this.detectors = d;
		this.scopef = f;
		this.progress = monitor;
		this.methodsPerClass = new HashMap<IType, Integer>();
		this.methodsPerPackage = new HashMap<IPackageFragment, Integer>();
	}
	
	//does the trick
	public void detectBadSmells(IResource resource, IFilter f) throws JavaModelException,
			CoreException {
		if (resource instanceof IProject) {
			
			for (Detector d : this.detectors) {
				d.setProject(resource.getName());
				HashMap<IPackageFragment, HashMap<IType, ArrayList<ReportableElement>>> affectedElements = new HashMap<IPackageFragment, HashMap<IType, ArrayList<ReportableElement>>>();
				HashMap<IPackageFragment, Integer> badMethodsPerPackage = new HashMap<IPackageFragment, Integer>();
				HashMap<IType, Integer> badMethodsPerClass = new HashMap<IType, Integer>();
				
				//PROGRESO
				progress.worked(15);
				
				
				//obtengo los metodos problematicos
				ArrayList<ReportableElement> toReport = d.detect((IProject)resource, f, progress);				
				progress.worked(50);
				actualiseCounts(d, affectedElements, badMethodsPerPackage, badMethodsPerClass, toReport);
				
				//en este punto tengo el mapa con las estructuras de paquete->[clases->metodos]
				//y la cantidad de metodos problematicos por clase y por paquete

				//ahora reporto y creo las estructuras de arbol
				actualiseModelStructure(d, affectedElements, badMethodsPerPackage, badMethodsPerClass);
			}
			//System.out.println("CAMBIO DE DETECTOR");
		}
		progress.done();
	}

	/**
	 * method that actualise the logical tree structure of the report and detector classes
	 * for the current calculations 
	 * 
	 */
	private void actualiseModelStructure(
			Detector d,
			HashMap<IPackageFragment, HashMap<IType, ArrayList<ReportableElement>>> affectedElements,
			HashMap<IPackageFragment, Integer> badMethodsPerPackage,
			HashMap<IType, Integer> badMethodsPerClass) {
		IPackageFragment e[] = affectedElements.keySet().toArray(new IPackageFragment[0]);

		int totalGoods = 0;
		int totalBads = 0;
		progress.done();

		for(int j = 0; j < e.length; j++){
			
			progress.worked(2);
			IPackageFragment ipf = e[j];

			int bads = badMethodsPerPackage.get(ipf).intValue();
			int goods = this.methodsPerPackage.get(ipf);					
			totalGoods += goods;
			totalBads += bads;
			
			ByPackageReport bpr = new ByPackageReport(d,ipf.getElementName(),bads,goods);
			bpr.setParent(d);
			d.addChildren(bpr);
			
			IType[] classes = affectedElements.get(ipf).keySet().toArray(new IType[0]); 
			
			for(int k = 0; k < classes.length; k++){
				IType clase = classes[k];
				ByClassReport bcr = new ByClassReport(d, clase.getElementName(), badMethodsPerClass.get(clase).intValue(), this.methodsPerClass.get(clase));
				bpr.addChildren(bcr);
				bcr.setParent(bpr);
				bcr.setChildren(affectedElements.get(ipf).get(clase));
			}
		}
		
		double per = ((double)totalBads / (double)totalGoods) * 100;
		
		String spercent;
		if(per != per) 
			spercent = "0";
		else{
			spercent = "" + per;
			spercent = (spercent.contains(".")) ?  spercent.substring(0,spercent.indexOf(".") + 2) : spercent;
		}
		d.setCantidad(""+totalBads);
		d.setPercent(spercent+"%");
		d.setType(d.getType()); //+ " [ " + totalBads + " (" + spercent + "%) ]" cambio
	}

	/**
	 * method that actualise the amount of problematic methods for class and for package.
	 * It also adds the corresponding markers in the code for efficiency purposes
	 * 
	 */
	private void actualiseCounts(
			Detector d,
			HashMap<IPackageFragment, HashMap<IType, ArrayList<ReportableElement>>> affectedElements,
			HashMap<IPackageFragment, Integer> badMethodsPerPackage,
			HashMap<IType, Integer> badMethodsPerClass,
			ArrayList<ReportableElement> toReport) throws JavaModelException {
		for(int i = 0; i < toReport.size(); i++){
			
			progress.worked(2);
			//agrego el marker correspondiente en código
			ReportableElement re = toReport.get(i);
			
			d.getBuilder().addMarker(re.getIFile(), d.getType() + re.getSuggestion4Tooltip(), re.getLine(), IMarker.PRIORITY_LOW);
			
			IType affectedClass = re.getJavaClass();
			IPackageFragment affectedPackage = re.getJavaClass().getPackageFragment();
			this.actualiseMethodsCount(affectedPackage);
			//actualizo el mapa de elementos afectados del projecto
			
			if(affectedElements.containsKey(affectedPackage)){
				HashMap<IType, ArrayList<ReportableElement>> aux = affectedElements.get(affectedPackage);
				
				if(aux.containsKey(affectedClass)){
					//existe el paquete y la clase
					aux.get(affectedClass).add(re);
					Integer cant = new Integer(badMethodsPerClass.get(affectedClass) + 1);
					badMethodsPerClass.put(affectedClass, cant);
				}
				else{
					//no existe la clase
					ArrayList<ReportableElement> auxRe = new ArrayList<ReportableElement>();
					auxRe.add(re);
					aux.put(affectedClass, auxRe);
					Integer cantxClass = new Integer(1);
					badMethodsPerClass.put(affectedClass, cantxClass);
				}
				
				Integer cantxPackage = new Integer(badMethodsPerPackage.get(affectedPackage) + 1);
				badMethodsPerPackage.put(affectedPackage, cantxPackage);
				
			}
			else{
				//no existe el paquete
				ArrayList<ReportableElement> auxRe = new ArrayList<ReportableElement>();
				auxRe.add(re);
				HashMap<IType, ArrayList<ReportableElement>> auxClass = new HashMap<IType, ArrayList<ReportableElement>>();
				auxClass.put(affectedClass, auxRe);
				affectedElements.put(affectedPackage, auxClass);
				Integer one = new Integer(1);
				badMethodsPerClass.put(affectedClass, one);
				badMethodsPerPackage.put(affectedPackage, one);
				
			}//fin de la actualización
				progress.worked(25);
		}//fin del ciclo por los métodos problemáticos
	}

	/**
	 * visits the project if it has been changed, detecting bad smells
	 */
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {

		if(delta != null){
			IResource resource = delta.getResource();
			
			if(resource != null){
				switch (delta.getKind()) {
				case IResourceDelta.ADDED:
					detectBadSmells(resource,this.scopef);
					break;
				case IResourceDelta.CHANGED:
					detectBadSmells(resource, this.scopef);
					break;
				}
			}
		}
		// return true to continue visiting children.
		return false;
	}
	
	/**
	 * method that actualise the total method count for the given package and 
	 * its classes
	 */
	private void actualiseMethodsCount(IPackageFragment mypackage) throws JavaModelException{
		
		int methodsPerPackage = 0;
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			// Obtengo los tipos declarados dentro del archivo
			IType[] allTypes = unit.getAllTypes();
			for (IType type : allTypes) {
				methodsPerPackage += type.getMethods().length;	
			this.methodsPerClass.put(type, type.getMethods().length);
			}
		}
		this.methodsPerPackage.put(mypackage, methodsPerPackage);
	}
}
