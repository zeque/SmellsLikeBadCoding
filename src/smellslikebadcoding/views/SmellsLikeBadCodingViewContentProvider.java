package smellslikebadcoding.views;


import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import reports.ByClassReport;
import reports.ByPackageReport;
import smellslikebadcoding.model.Root;
import detectors.Detector;


public class SmellsLikeBadCodingViewContentProvider implements ITreeContentProvider{

	private TreeViewer viewer;
	
	
	
	/**
	 * change the actual underlying tree of the statistic tree for the given viewer
	 */
	@Override
	public void inputChanged(Viewer arg0, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
	}

	/**
	 * method that tells the tree viewer the children of a given element, in order
	 * to show it correctly
	 */
	@Override
	public Object[] getChildren(Object arg0) {
		
		if( arg0 instanceof Detector){
			Detector d = (Detector)arg0;
			return d.getChildren().toArray();
		} else
		if( arg0 instanceof ByClassReport){
			ByClassReport r = (ByClassReport)arg0;
			return r.getChildren().toArray();
		}
		if( arg0 instanceof ByPackageReport){
			ByPackageReport r = (ByPackageReport)arg0;
			return r.getChildren().toArray();
		}
		return new Object[0];
	}
	
	/**
	 * method that tells the tree viewer the elements (in this case an element is the same
	 * as a children) of a given element, in order to show it correctly
	 */
	@Override
	public Object[] getElements(Object arg0) {
		
		if(arg0 instanceof Detector)
			return ((Detector)arg0).getChildren().toArray();
		if(arg0 instanceof Root)
			return ((Root)arg0).getChildren().toArray();
		if(arg0 instanceof ByClassReport)
			return ((ByClassReport)arg0).getChildren().toArray();
		if(arg0 instanceof ByPackageReport)
			return ((ByPackageReport)arg0).getChildren().toArray();

		return new Object[0];
	}

	/**
	 * method that tells the tree viewer the parent of a given element, in order
	 * to show it correctly
	 */
	@Override
	public Object getParent(Object arg0) {
		if(arg0 instanceof ByClassReport)
			return ((ByClassReport)arg0).getParent();
		if(arg0 instanceof ByPackageReport)
			return ((ByPackageReport)arg0).getParent();
		return null;
	}

	@Override
	public boolean hasChildren(Object arg0) {
		if(arg0 instanceof ByClassReport)
			return ((ByClassReport)arg0).getChildren().size() > 0;
		if(arg0 instanceof ByPackageReport)
				return ((ByPackageReport)arg0).getChildren().size() > 0;
		if(arg0 instanceof Detector)
			return ((Detector)arg0).getChildren().size() > 0;
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
