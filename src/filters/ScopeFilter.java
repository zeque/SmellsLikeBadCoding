package filters;

import reports.Method;
import reports.ReportableElement;

public class ScopeFilter implements IFilter {

	//scope of a method
	private String scope;
	
	public ScopeFilter(String s){
		this.scope = s;
	}
	
	/**
	 * returns whether a reportable element (a method) match the required scope
	 */
	@Override
	public boolean accept(ReportableElement re) {
		Method m = (Method)re;
		if ("".equals(m.getScope()) && "package".equals(this.scope)) return true; 
		return scope.equals(m.getScope());
	}
	
	
}
