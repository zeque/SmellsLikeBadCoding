package smellslikebadcoding.builder;


import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

import smellslikebadcoding.model.Modifier;
import smellslikebadcoding.model.Root;
import smellslikebadcoding.visitor.ProjectVisitor;
import detectors.BrainMethodDetector;
import detectors.Detector;
import detectors.ExhibicionistMethodDetector;
import filters.AndCompositeFilter;
import filters.AnnotationFilter;
import filters.IFilter;
import filters.OrCompositeFilter;
import filters.ScopeFilter;
//import smellslikebadcoding.Callings;
import filters.WildcardFilter;



public class SmellsLikeBadCodingBuilder extends IncrementalProjectBuilder {
	 

	public static final String BUILDER_ID = "SmellsLikeBadCoding.smellsLikeBadCodingBuilder";
	public static final String MARKER_ID = "SmellsLikeBadCoding.Exhibicionism";
	
	private IPreferenceStore store;
	
	/**
	 * adds a marker to the the given file in the given line number, 
	 * with the corresponding message or tool tip, and severity
	*/
	public void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	/**
	 
	 * Obtain the changes in the project with getDelta() and activates the 
	 * the preference store, where the user preferences are located.
	 * @see org.eclipse.core.internal.events.InternalBuilder
	 */
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
			IResourceDelta delta = getDelta(getProject());
			store = Activator.getDefault().getPreferenceStore();
			incrementalBuild(delta, monitor);
			return null;
	}


	/**
	*It invokes the project visitor, which triggers the smell calculation, then with
	*with that result it actualises the statistics tree in order to mantain the
	*statistics view
	*/
	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException{
		
			//this.forgetLastBuiltState();
			
			//crea los detectores seleccionados en las preferencias
			ArrayList<Detector> detectors = createDetectors(store);
			//si hay detectores seleccionados en las preferencias
			
			long time_start, time_end;
			time_start = System.currentTimeMillis();
			
			
			if (detectors.size() > 0) {
				IFilter storeFilters = createDetectorFilter(store);
				if (storeFilters != null) {
					if (delta != null) {

						IFilter annoFilter     = new AndCompositeFilter(storeFilters, new AnnotationFilter("SLBC_Exclude"));
						IFilter wildcardFilter = new AndCompositeFilter(annoFilter, new WildcardFilter(store.getString("Wildcard")));
						
						System.out.print("prueba - " + store.getString("Wildcard")+"\n");
						
						IJavaProject javaProject = JavaCore.create((IProject)delta.getResource());
						
						//invoco al visitor del proyecto
						delta.accept(new ProjectVisitor(detectors, wildcardFilter, monitor));
						
						//cambio el arbol de la vista de estadisticas
						monitor.worked(500);
						
						//creo el nuevo árbol de estadísticas
						Root r = new Root(detectors);
						
						//modifico la vista
						this.modifyView(r);
						monitor.done();
						
						//reseteo las preferencias
//						this.store.setValue("Exhibicionist methods", false);
//						this.store.setValue("Brain methods", false);
					}
				}
				
			}
			time_end = System.currentTimeMillis();
			System.out.println("the task has taken "+ ( time_end - time_start ) +" milliseconds");
	}
	
	private void modifyView(Root root){
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Modifier(root));
	}
	
	

	/**
	Create the filter accordingly to the user preferences regarding the scope
	of the method he/she wants to be analised
	*/
	private IFilter createDetectorFilter(IPreferenceStore store) {

		IFilter toReturn = null;
		
		boolean publicP = store.getBoolean("Public");
		boolean protectedP = store.getBoolean("Protected");
		boolean packageP = store.getBoolean("Package");
		
		ScopeFilter sfpub = new ScopeFilter("public");
		ScopeFilter sfpac = new ScopeFilter("package"); //el scope de paquete es " "
		ScopeFilter sfpro = new ScopeFilter("protected");
		
		if(publicP && protectedP && packageP){
			OrCompositeFilter f = new OrCompositeFilter(sfpub, sfpro);
			toReturn = new OrCompositeFilter(f, sfpac);
		}
		else if(publicP && protectedP) toReturn = new OrCompositeFilter(sfpro, sfpub);
		else if(protectedP && packageP)	toReturn = new OrCompositeFilter(sfpro, sfpac);
		else if(publicP && packageP) toReturn = new OrCompositeFilter(sfpac, sfpub);
		else if(publicP) toReturn = sfpub;
		else if(protectedP) toReturn = sfpro;
		else if(packageP) toReturn = sfpac;

		return toReturn;
	}
	
	
	/**
	 * 
	 * 
	 * @return the list of detectors selected by the user within the preferences
	 */
	private ArrayList<Detector> createDetectors(IPreferenceStore store) {
		
		ArrayList<Detector> toReturn = new ArrayList<Detector>();
		boolean ex = store.getBoolean("Exhibicionist methods");
		boolean brain = store.getBoolean("Brain methods");
		
		if(ex){ 
			Detector exD = new ExhibicionistMethodDetector(this);
			toReturn.add(exD);
		}
		
		if(brain){
			Detector brD = new BrainMethodDetector(this);;
			toReturn.add(brD);
		}
		
		return toReturn;
	}
	
}
