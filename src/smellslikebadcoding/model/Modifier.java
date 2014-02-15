package smellslikebadcoding.model;

import java.util.ArrayList;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import smellslikebadcoding.views.SmellsLikeBadCodingView;
import detectors.Detector;

public class Modifier implements Runnable{
	
	private Root root;
	
	public Modifier(Root root){
		this.root = root;
	}
	
	/**
	 * gets the TreeViewer within the statistic view and sets the new elements
	 */
	public void run(){
	
			try {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("smellslikebadcoding.views.SmellsLikeBadCodingView");
			} catch (PartInitException e) {}

	
			SmellsLikeBadCodingView v = (SmellsLikeBadCodingView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("smellslikebadcoding.views.SmellsLikeBadCodingView");
			if(v != null){
				TreeViewer viewer = v.getTreeViewer();				
				viewer.setInput(root);
				viewer.refresh();
			}
		}
		
	}
