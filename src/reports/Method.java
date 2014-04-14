package reports;

import java.lang.reflect.Type;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;



public class Method implements ReportableElement {

	private IMethod iMethod;
	private IJavaProject iJavaProject;
	private ICompilationUnit file;
	private IType javaClass;
	private IPackageFragment iPackage;
	private String reason;
	
	private String suggestion;
	
	/**
	 * convenience class that maps JDT structures representing java elements of a project
	 * @param iJavaProject
	 * @param iPackage
	 * @param file
	 * @param javaClass
	 * @param iMethod
	 */
	public Method(IJavaProject iJavaProject, IPackageFragment iPackage, ICompilationUnit file, IType javaClass, IMethod iMethod) {
		this.iMethod = iMethod;
		this.iJavaProject = iJavaProject;
		this.file = file;
		this.javaClass = javaClass;
		this.iPackage = iPackage;
	}
	
	
	public void setSuggestion(String s){
		this.suggestion = s;
	}
	
	public IMethod getiMethod() {
		return iMethod;
	}

	@Override
	public IJavaProject getJavaProject() {
		return iJavaProject;
	}

	public ICompilationUnit getFile() {
		return file;
	}

	@Override
	public IType getJavaClass() {
		return javaClass;
	}

	public IPackageFragment getiPackage() {
		return iPackage;
	}

	
	
	public boolean isSimilitarTo(IMethod iMethod) {
		return this.iMethod.isSimilar(iMethod);
	}
	
	public boolean isAPrivateMethod(){
		try {
			return(Flags.toString(iMethod.getFlags()).equals("private"));
		} catch (JavaModelException e) {
			return false;}
	}
	
	public String getScope(){
		try {
			return Flags.toString(iMethod.getFlags());
		} catch (JavaModelException e) {
			return null;
		}
	}
	
	public boolean isAProtectedMethod(){
		try {
			return(Flags.toString(iMethod.getFlags()).equals("protected"));
		} catch (JavaModelException e) {

			return false;
		}	
		}
	
	public boolean isAPublicMethod() throws JavaModelException{
		try {
			return(Flags.toString(iMethod.getFlags()).equals("public"));
		} catch (JavaModelException e) {

			return false;
		}
	}
	
	
	
	public boolean isAPackageMethod() throws JavaModelException{
		return(!(this.isAPrivateMethod() || 
				 this.isAProtectedMethod() || 
				 this.isAPublicMethod()));
	}

	public IMember getIMethod() {
		return this.iMethod;
	}
	
	@Override
	public String toString() {
		return this.iMethod.getElementName();
	}
	
	public boolean itsAChildOf(Method m){
		return false;
	}	
	
	/**
	 * get the line of a method in a java file
	 */
	public int getLine(){
		try {
			return(this.getLineNumFromOffset(this.getFile(), this.getiMethod().getSourceRange().getOffset()));
		} catch (JavaModelException e) {
			return -1;
		}
		
	}
	
	/**
	 * get the line of a method in a java file, given the offset
	 */
	private int getLineNumFromOffset(ICompilationUnit cUnit, int offSet){
        try {
                String source = cUnit.getSource();
                IType type = cUnit.findPrimaryType();
                if(type != null) {
                        String sourcetodeclaration = source.substring(0, offSet);
                        int lines = 0;
                        char[] chars = new char[sourcetodeclaration.length()];
                        sourcetodeclaration.getChars(
                                        0,
                                        sourcetodeclaration.length(),
                                        chars,
                                        0);
                        for (int i = 0; i < chars.length; i++) {
                                if (chars[i] == '\n') {
                                        lines++;
                                }
                        }
                        return lines + 1;
                }
        } catch (JavaModelException jme) {
        }
        return 0;      
}

	public IFile getIFile() {
		return (IFile) this.getiMethod().getCompilationUnit().getResource();
	}


	@Override
	public String getName() {
		return this.getiMethod().getElementName();
	}



	@Override
	public IMethod getImethod() {
		return this.iMethod;
	}

	@Override
	public String getSuggestion4Tooltip() {
		return this.suggestion;
	}

	public void setReason(String r) {
		this.reason = r;
	}

	public String getReason() {
		return this.reason;
	}
	  static void printInterfaceNames(Object o) {
	      Class c = o.getClass();
	      Class[] theInterfaces = c.getInterfaces();
	      for (int i = 0; i < theInterfaces.length; i++) {
	         String interfaceName = theInterfaces[i].getName();
	         System.out.println(interfaceName);
	      }
	   }
	  
		
		public boolean isDefinedInParent() throws JavaModelException {
		//	ITypeHierarchy typeHierarchie = javaClass.newTypeHierarchy(new NullProgressMonitor());
	    //    IType tipo01[] = typeHierarchie.getSuperInterfaces(javaClass);
	     //   tipo01 = typeHierarchie.getAllInterfaces();
	     //   IMethod[] metodosI = tipo01[0].findMethods(iMethod);
//	        IType tipo = typeHierarchie.getSuperclass(javaClass); 
//	        IType tipo02[] = typeHierarchie.getAllSuperclasses(javaClass);
		//	IMethod[]  aClass = getJavaClass().getSuperInterfaceNames();
			//Class[] aa= aClass.getInterfaces();
			
	        	if(getJavaClass().isClass()){  // si es classs obtengo sus interfaces y si tiene el metodo return true
	              	//System.out.println("superclass: " + this.javaClass);
	        		ITypeHierarchy typeHierarchie = javaClass.newTypeHierarchy(new NullProgressMonitor());
	                IType tipo01[] = typeHierarchie.getSuperInterfaces(javaClass);
	                IMethod[] metodosI = tipo01[0].findMethods(iMethod);
	        		for(int  j=0; j< metodosI.length; j++) // recorro los metodos de la interface
	        		{
	        			if(metodosI[j].getElementName().equals(getName())){
	        				System.out.println("nombre metodo: " + metodosI[j].getElementName());
	        			     return true;
	        			}
	        		    else{
	        				System.out.println("algo anda mal " + getName());
	        			}	
	        		
	        		}
	        	}

	     return false;
		}
	
}