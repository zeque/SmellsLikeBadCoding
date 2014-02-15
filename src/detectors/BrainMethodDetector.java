package detectors;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import reports.Method;
import reports.ReportableElement;
import smellslikebadcoding.builder.SmellsLikeBadCodingBuilder;
import filters.IFilter;


public class BrainMethodDetector extends Detector{

	/**
	 * constants needed to the calculations required by the brain methods smell
	 */
	private static final int LOC_LIMIT = 60;
	private static final int CC_LIMIT = 4;
	private static final int ACC_LIMIT = 7;
	private static final String ACCESSED_VARIABLE_INDICATOR = "=";
	private static final String BREAK = "\n";
	private static final String TYPE = "Brain method(s)";
	private static final String IF_SENTENCE = "if";
	private static final String CASE_SENTENCE = "case";
	private static final String WHILE_SENTENCE = "while";
	private static final String FOR_SENTENCE = "for";
	private static final String FOREACH_SENTENCE = "foreach";

	/**
	 * 
	 * convenience constructor that sets the type of the detector
	 */
	public BrainMethodDetector(SmellsLikeBadCodingBuilder b) {
		super(b, TYPE);
		this.name = "brain";
	}
	
	/**
	 * this methods obtain all the methods of the project that the filter accepts
	 * @param javaProject the actual project
	 * @param f, filter the methods that the user doesn't want to be analised
	 * @return
	 */
public ArrayList<Method> getAllMethods(IJavaProject javaProject, IFilter f) throws JavaModelException {
		
		
		// Creo el arreglo de metodos del projecto
		ArrayList<Method> paraRetornar = new ArrayList<Method>();
		
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
								paraRetornar.add(m);
							
							//actualización de conveniencia para borrar markers más eficientemente
							if(!this.files.contains(m.getIFile()))
								this.files.add(m.getIFile());
						}
						this.methodsPerClass.put(type, methodsPerClass);
					}

				}

			}
			this.methodsPerPackage.put(mypackage, methodsPerPackage);
		}
		return paraRetornar;
	}
	


	/**
	 * 
	 * @param f filter the unwanted methods
	 * @return the list of brain methods of the project p
	 */
	private ArrayList<ReportableElement> getBrainMethods(IProject p, IFilter f){
		
		IJavaProject javaProject = JavaCore.create(p);
		ArrayList<ReportableElement> toReturn = new ArrayList<ReportableElement>();
		
		try {
			ArrayList<Method> methods = this.getAllMethods(javaProject, f);
			
			for(Method m : methods){
				
				//obtención de las métricas
				int cc = 0;
				int ifCount = this.patronMatch(m.getiMethod().getSource(), IF_SENTENCE);
				int caseCount = this.patronMatch(m.getiMethod().getSource(), CASE_SENTENCE);
				int whileCount = this.patronMatch(m.getiMethod().getSource(), WHILE_SENTENCE);
				int forCount = this.patronMatch(m.getiMethod().getSource(), FOR_SENTENCE);
				int forEachCount = this.patronMatch(m.getiMethod().getSource(), FOREACH_SENTENCE);
				
				//complejidad ciclomática
				cc = ifCount + caseCount + whileCount + forCount + forEachCount + 1;
				if(cc == 1) cc = 0;
				
				//LOC
				int loc = m.getiMethod().getSource().split(BREAK).length - 1;
				
				//cantidad de variables accedidas
				int parameterCount = m.getiMethod().getNumberOfParameters();
				int accessedVariables = this.patronMatch(m.getImethod().getSource(), ACCESSED_VARIABLE_INDICATOR);
				int count = parameterCount + accessedVariables;
				if( loc > LOC_LIMIT && cc/2 > CC_LIMIT && count > ACC_LIMIT){
					m.setSuggestion("");
					toReturn.add(m);
				}
			}
			
		} catch (JavaModelException e) {
			System.out.println("No se pudieron obtener los métodos");
		}
		
		return toReturn;
		
	}
	
	/**
	 * overrided method that complete the materialization of the Detector class
	 */
	@Override
	public ArrayList<ReportableElement> detect(IProject p, IFilter f, IProgressMonitor monitor) {
		return this.getBrainMethods(p,f);	
	}
	
	
	
	 private int patronMatch(String cadena, String patron){
		 int cant = 0;
		 while (cadena.indexOf(patron) > -1){
			 cadena = cadena.substring(cadena.indexOf(patron)+patron.length(),cadena.length());
			 cant++;
		 }
		 return cant;
	 }
	
}