package smellslikebadcoding.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import detectors.Detector;
import reports.ByClassReport;
import reports.ByPackageReport;
import reports.ReportableElement;

class SmellsLikeBadCodingTableProvider implements ITableLabelProvider{
	
	 @Override
    public Image getColumnImage(Object element, int columnIndex){
		
		 switch(columnIndex)
		 {
		 case 0:
		 		String imageKey = ISharedImages.IMG_OBJ_ADD;
				if (element instanceof ReportableElement)
				   imageKey = ISharedImages.IMG_OBJ_ELEMENT;
				return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		 case 1: return null;
		 case 2: return null;
		 }
		 return null;
	 }
	 
	 @Override 
    public String getColumnText(Object element, int columnIndex){
      
	  if (element instanceof Detector){
		  Detector d = (Detector)element;
		  switch (columnIndex)
		  {
	          case 0: return d.toString();
	          case 1: return d.getCantidad();
	          case 2: return d.getPercent();
		  }
	  } else
		  if (element instanceof ByClassReport){
			  ByClassReport r = (ByClassReport)element;
			  switch (columnIndex)
			  {
		          case 0: return r.toString();
		          case 1: return r.getCantidad();
		          case 2: return r.getPercent();
			  }
		  }else
			  if (element instanceof ByPackageReport){
				  ByPackageReport pr = (ByPackageReport)element;
				  switch (columnIndex)
				  {
			          case 0: return pr.toString();
			          case 1: return pr.getCantidad();
			          case 2: return pr.getPercent();
				  }
				  
			  }
       return null;
    }

	 @Override
    public void dispose(){
    }
    
    @Override
    public boolean isLabelProperty(Object element, String property){
       return false;
    }

	@Override
    public void removeListener(ILabelProviderListener listener){
    }

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

 }		