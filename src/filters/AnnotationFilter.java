package filters;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.JavaModelException;

import reports.ReportableElement;

public class AnnotationFilter implements IFilter {

	private String annotation;
	
	public AnnotationFilter(String anno) {

		this.annotation = anno;
	}
	
	public boolean accept(ReportableElement re) {

		IMethod iM = re.getImethod();

		try {
			for (IAnnotation annotation : iM.getAnnotations()) {
				if (this.annotation.equals(annotation.getElementName())) {
		            return false;
				}
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return true;
	}
}