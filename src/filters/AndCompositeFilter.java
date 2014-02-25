package filters;

import reports.ReportableElement;

public class AndCompositeFilter implements IFilter{

	private IFilter f1, f2;
	
	/**
	 * returns the or combination of two different filters
	 * @param f1 
	 * @param f2
	 */
	public AndCompositeFilter(IFilter f1, IFilter f2){
		this.f1 = f1;
		this.f2 = f2;
	}


	@Override
	public boolean accept(ReportableElement re) {
		return f1.accept(re) && f2.accept(re);
	}
}
