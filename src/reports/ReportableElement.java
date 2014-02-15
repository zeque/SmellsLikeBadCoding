package reports;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public interface ReportableElement {

	/**
	 * class that reprensent an ideal element to be reported.
	 * In this project we always report methods, but if in the future
	 * another element (i.e. a class) needs to be reported, it has to implement this
	 * interface and everything will work the same as before 
	 * @return
	 */
	abstract public String getName();
	abstract public String getScope();
	abstract public IMethod getImethod();
	abstract public IType getJavaClass();
	abstract public IJavaProject getJavaProject();
	abstract public int getLine();
	abstract public IFile getIFile();
	abstract public String toString();
	abstract public String getSuggestion4Tooltip();

}
