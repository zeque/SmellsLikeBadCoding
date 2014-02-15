package smellslikebadcoding.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import reports.ReportableElement;
//import smellslikebadcoding.Statistic;

public class SmellsLikeBadCodingLabelProvider extends LabelProvider{
	
	/**
	 * method that the TreeViewer uses to know what to show as the label of each node
	 */
	@Override
	public String getText(Object o){
		return o.toString();
	}
	
	/**
	 * provides the image for each level of the statistic tree in the statistic view
	 */
	@Override
	public Image getImage(Object obj) {
		String imageKey = ISharedImages.IMG_OBJ_ADD;
		if (obj instanceof ReportableElement)
		   imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
}
