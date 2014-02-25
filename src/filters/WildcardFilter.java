package filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reports.ReportableElement;
import filters.IFilter;

public class WildcardFilter implements IFilter {

	private String wildcard; 


	public WildcardFilter(String wc) {
		
		this.wildcard = wc;
	}


	private boolean matchPattern(Pattern wildCardPattern, String name) {

		Matcher match = wildCardPattern.matcher(name);

		if (match.find()) {
			String fileMatch = match.group();

			if (name.equals(fileMatch)) {
				return false;
			}
		}

		return true;		
	}


	public boolean accept(ReportableElement re) {

		Pattern wildCardPattern = Pattern.compile(".*" + this.wildcard + ".*", Pattern.CASE_INSENSITIVE);
		
		String className   = re.getJavaClass().getElementName();
		String packageName = re.getJavaClass().getPackageFragment().getElementName();

		return (this.matchPattern(wildCardPattern, className) && this.matchPattern(wildCardPattern, packageName));
	}
}