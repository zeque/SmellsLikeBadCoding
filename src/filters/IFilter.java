package filters;

import reports.ReportableElement;

public interface IFilter {
	
	public boolean accept(ReportableElement re);
}
