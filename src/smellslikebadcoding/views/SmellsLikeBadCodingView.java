package smellslikebadcoding.views;


import java.util.ArrayList;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;

import reports.ReportableElement;
import smellslikebadcoding.model.Root;
import detectors.Detector;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class SmellsLikeBadCodingView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "smellslikebadcoding.views.SmellsLikeBadCodingView";
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	public String algo1;

	private static final String VIEWER = "SmellsLikeBadCoding.viewer";


	@Override
	public String toString(){return "SmellsLikeBadCoding view to string";}
	
	//m�todos a�adidos por mante
	public TreeViewer getTreeViewer(){ 
		return this.viewer;
	}	/**
	 * The constructor.
	 */
	public SmellsLikeBadCodingView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		
		// cambios		
		Tree addressTree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		addressTree.setHeaderVisible(true);
	    viewer = new TreeViewer(addressTree);

	    // codigo agregado
	    TreeColumn column1 = new TreeColumn(addressTree, SWT.LEFT);
        addressTree.setLinesVisible(true);
	    column1.setAlignment(SWT.LEFT);
	    column1.setText("Raiz");
	    column1.setWidth(400);
	    TreeColumn column2 = new TreeColumn(addressTree, SWT.RIGHT);
	    column2.setAlignment(SWT.LEFT);
	    column2.setText("Cantidad");
	    column2.setWidth(200);
	    TreeColumn column3 = new TreeColumn(addressTree, SWT.RIGHT);
	    column3.setAlignment(SWT.LEFT);
	    column3.setText("Porcentaje");
	    column3.setWidth(200);
	    TreeColumn column4 = new TreeColumn(addressTree, SWT.RIGHT);
	    column4.setAlignment(SWT.LEFT);
	    column4.setText("Posible Raz�n Exhibicionismo");
	    column4.setWidth(200);

		//no iria
		//viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
	    viewer.setContentProvider(new SmellsLikeBadCodingViewContentProvider());
		viewer.setLabelProvider(new SmellsLikeBadCodingTableProvider());
		drillDownAdapter = new DrillDownAdapter(viewer);

		Root r = new Root(new ArrayList<Detector>());
		
		viewer.setInput(r);
		viewer.refresh();
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "SmellsLikeBadCoding.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				SmellsLikeBadCodingView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(new Separator());
//		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//		manager.add(new Separator());
//		drillDownAdapter.addNavigationActions(manager);
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//		manager.add(new Separator());
//		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
//		action1 = new Action() {
//			@Override
//			public void run() {
//				showMessage("Action 1 executed");
//				this.setText("PEPE");
//			}
//		};
//		action1.setText("Action 1");
//		action1.setToolTipText("Action 1 tooltip");
//		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
//		action2 = new Action() {
//			@Override
//			public void run() {
//				showMessage("Action 2 executed");
//			}
//		};
//		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			@Override
			public void run() {
				
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
					if(obj instanceof ReportableElement){
					ReportableElement e = (ReportableElement)obj;
					
					
					IWorkbenchWindow iw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					
					System.out.println(PlatformUI.getWorkbench().getWorkbenchWindows()[0].getPages().length);
					try {
						JavaUI.openInEditor(e.getImethod().getPrimaryElement());
					} catch (PartInitException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (JavaModelException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"SmellsLikeBadCodingView",
			message);
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
		
	}
}