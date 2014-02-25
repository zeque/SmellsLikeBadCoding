package detectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.*;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;

import reports.Method;
import reports.ReportableElement;
import smellslikebadcoding.builder.SmellsLikeBadCodingBuilder;
import filters.IFilter;

public class ExhibicionistMethodDetector extends Detector {

	private static final String TYPE = "Exhibicionist method(s)";
	
	public ExhibicionistMethodDetector(SmellsLikeBadCodingBuilder b) {
		super(b, TYPE);
		this.name = "exhibicionist";
		this.methodsPerPackage = new HashMap<IPackageFragment, Integer>();
		this.methodsPerClass = new HashMap<IType,Integer>();
		this.files = new ArrayList<IFile>();
	}

	public static String MESSAGE = "SmellsLikeBadCoding: this method smells ";

	/**
	 * overrided method that complete the materialization of the Detector class
	 * and returns the list of exhibicionist methods within a project, fulfilling the 
	 * user preferences
	 */
	@Override
	public ArrayList<ReportableElement> detect(IProject p, IFilter f, IProgressMonitor monitor) {
		
		ArrayList<ReportableElement> toReport = new ArrayList<ReportableElement>();
		try {
				IJavaProject javaProject = JavaCore.create(p);
				
				return this.getExhibicionists(javaProject, monitor, f);
			} catch (CoreException e) {
		}	
		return toReport;
	}
	
	/**
	 * return the exhibicionist methods and erase the markers of previous executions
	 * of the smell detector
	 * @param javaProject
	 * @param monitor
	 * @param f filter of preferences
	 * @return the exhibicionist methods
	 * 
	 */
	private ArrayList<ReportableElement> getExhibicionists(IJavaProject javaProject, IProgressMonitor monitor, IFilter f)
			throws CoreException {
			
		
			ArrayList<ReportableElement> toReturn = this.getExhibicionistMethods(javaProject, f);						
			
			//delete the previous markers in the code
			for (IFile file : files) {
				file.deleteMarkers(null, false, IResource.DEPTH_ZERO);
			}

		return toReturn;

	}

	/**
	 * tells whether a methods is or not exhibicionist, 
	 * @param llamado, the actual java method
	 * 
	 * @throws JavaModelException
	 */
	private boolean isExhibicionist(Method llamado) throws JavaModelException {
		if(llamado != null){
			ArrayList<Method> llamadores = this.getCallersOf(llamado);
			if (llamadores != null && !llamadores.isEmpty()) {

				boolean cortar = false;
				int ciclo = 0;
				// si es un metodo publico
				if (llamado.isAPublicMethod()) {
					boolean sameClass = true;
					boolean sprotected = true; 
					while( ciclo < llamadores.size() && !cortar){
						Method m1 = llamadores.get(ciclo);
						// si no está en el mismo paquete cortamos
						if(m1 != null){
							if (!m1.getiPackage().equals(llamado.getiPackage()))
								cortar = true;
							else 
							if(!m1.getJavaClass().equals(llamado.getJavaClass())
								&& !m1.getIFile().equals(llamado.getIFile()))
								sameClass = false;
							ITypeHierarchy hierarchy = m1.getJavaClass()
									.newSupertypeHierarchy(null);
							if (!hierarchy.contains(llamado.getJavaClass()))
								sprotected = false;
						}
						//no es exhibicionista
						ciclo++;
					}
					if (!cortar){
						if(sameClass)
							llamado.setSuggestion(" (try making it private)");
						else
						if(sprotected)	
							llamado.setSuggestion(" (try making it protected)");
						else
							llamado.setSuggestion(" (try making it package)");
						return true;
					}
				}
				// si es un metodo protegido
				else if (llamado.isAProtectedMethod()) {
					while( ciclo < llamadores.size() && !cortar ){
						Method m1 = llamadores.get(ciclo);
						if(m1 != null)
							if (!llamado.getJavaClass().equals(m1.getJavaClass()))
								cortar = true;
								//no es exhibicionista
						ciclo++;
					}
					if (!cortar){
						llamado.setSuggestion(" (try making it private)");
						return true;
					}
				}
				// si es un metodo de paquete
				else if (llamado.isAPackageMethod()) {
					boolean sameClass1 = true;
					while( ciclo < llamadores.size() && !cortar ){
						Method llamador = llamadores.get(ciclo);
						if(llamador != null){
							if (!llamado.getJavaClass().equals(
									llamador.getJavaClass())) {
								sameClass1 = false;
								ITypeHierarchy hierarchy = llamador.getJavaClass()
										.newSupertypeHierarchy(null);
								if (!hierarchy.contains(llamado.getJavaClass()))
									cortar = true;
							}
						}
								ciclo++;
					}
					if (!cortar){
						if(sameClass1)
							llamado.setSuggestion(" (try making it private)");
						else 
							llamado.setSuggestion(" (try making it protected)");
						return true;
					}
				}
			}
		}
		return false;
	}

	private ArrayList<Method> getCallersOf2(Method m) throws JavaModelException{
		
		// Creo el arreglo de metodos del projecto
		ArrayList<Method> paraRetornar = new ArrayList<Method>();
		
		// Le saco los paquetes al proyecto
		IPackageFragment[] packages = m.getJavaProject().getPackageFragments();
		
		// Por cada paquete del source code
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				
				// Por cada unidad compilable
				for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
					// Obtengo los tipos declarados dentro del archivo
					IType[] allTypes = unit.getAllTypes();
					
					for (IType type : allTypes) {
						// Obtengo sus métodos
						IMethod[] methods = type.getMethods();
						// Por cada uno
						for (IMethod method : methods) {
							if(method.getSource() != null && method.getSource().contains(m.getName())){
									
									if(method.hasChildren()){
									System.out.println(method.getChildren()[0].getElementName() + " " + method.getChildren()[0].getElementType());
									}
									paraRetornar.add(new Method(method.getJavaProject(), method.getDeclaringType().getPackageFragment(), method.getCompilationUnit(), method.getDeclaringType(), method));
							}
						}
					}

				}

			}
		}
		return paraRetornar;
		
	}

	
	private ArrayList<Method> getCallersOf(Method m) {
		try{

		
		CallHierarchy callHierarchy = CallHierarchy.getDefault();

		IMember[] members = { m.getIMethod() };
		MethodWrapper[] methodWrappers = callHierarchy.getCallerRoots(members);
		ArrayList<IMethod> callers = new ArrayList<IMethod>();
		HashSet<IMethod> temp = new HashSet<IMethod>(); 
		if(methodWrappers.length > 0){
			for (MethodWrapper mw : methodWrappers) {
				if(mw != null){
					MethodWrapper[] mw2 = mw.getCalls(new NullProgressMonitor());
					if(mw2 != null)
						temp = getIMethods(mw2);
				}
				
				callers.addAll(temp);
			}
		}
		
		
		ArrayList<Method> toReturn = new ArrayList<Method>();
			for(int i = 0; i < callers.size(); i++){
				IMethod iMethod = callers.get(i);
				toReturn.add(new Method(iMethod.getJavaProject(), iMethod.getDeclaringType().getPackageFragment(), iMethod.getCompilationUnit(), iMethod.getDeclaringType(), iMethod));
			}
		
		return toReturn;
		}
		catch (NullPointerException exc){
			return new ArrayList<Method>();
		}
	}
	
public ArrayList<ReportableElement> getExhibicionistMethods(IJavaProject javaProject, IFilter f) throws JavaModelException {
		
		
		// Creo el arreglo de metodos del projecto
		ArrayList<ReportableElement> paraRetornar = new ArrayList<ReportableElement>();
		
		// Le saco los paquetes al proyecto
		IPackageFragment[] packages = javaProject.getPackageFragments();
		
		// Por cada paquete del source code
		for (IPackageFragment mypackage : packages) {
			int methodsPerPackage = 0;
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				
				// Por cada unidad compilable
				for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
					// Obtengo los tipos declarados dentro del archivo
					IType[] allTypes = unit.getAllTypes();
					
					for (IType type : allTypes) {
						// Obtengo sus métodos
						IMethod[] methods = type.getMethods();
						int methodsPerClass = methods.length;
						// Por cada uno
						for (IMethod method : methods) {
							// Creo la instancia del método
							methodsPerPackage++;
							Method m = new Method(javaProject, mypackage, unit, type, method);
							if(f.accept(m))
								if (this.isExhibicionist(m)) {
									paraRetornar.add((ReportableElement)m);
									if (m.isDefinedInParent()) {
									}
								}
							if(!this.files.contains(m.getIFile()))
								this.files.add(m.getIFile());
						}
						//actualise the count of methods per class
						this.methodsPerClass.put(type, methodsPerClass);
					}

				}

			}
			//actualise the count of methods per package
			this.methodsPerPackage.put(mypackage, methodsPerPackage);
		}
		return paraRetornar;
	}

	
	

}
