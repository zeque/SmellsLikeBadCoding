package smellslikebadcoding.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import smellslikebadcoding.builder.Activator;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

/**
 * class that represents the preference page. Provides access to the user preference selection.
 * 
 *
 */
public class SmellsLikeBadCodingPreferencePage extends
		FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor exMBoolPref, brMBoolPref, publicMethods,
			packageMethods, protectedMethods;

	private IPreferenceStore store;

	public SmellsLikeBadCodingPreferencePage() {
		super(GRID);
		store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		setDescription("Choose your preferences for the SmellsLikeBadCoding plugin");

	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 * In order to add a new detector, it has to be added here too, so it can be selected
	 */
	@Override
	public void createFieldEditors() {

		Group detectors = new Group(getFieldEditorParent(), 2);
		detectors.setParent(getFieldEditorParent());
		detectors.setEnabled(true);
		detectors.setText("Detectors");

		this.brMBoolPref = new BooleanFieldEditor(
				"Brain methods", "Brain methods", detectors);
		this.exMBoolPref = new BooleanFieldEditor(
				"Exhibicionist methods", "Exhibicionist methods",
				detectors);

		Group scope = new Group(getFieldEditorParent(), SWT.LINE_DOT);
		scope.setParent(getFieldEditorParent());
		scope.setText("Methods scope");
		detectors.setEnabled(true);
		

		this.publicMethods = new BooleanFieldEditor(
				"Public", "Public", scope);
		this.packageMethods = new BooleanFieldEditor(
				"Package", "Package", scope);
		this.protectedMethods = new BooleanFieldEditor(
				"Protected", "Protected", scope);

		this.addField(brMBoolPref);
		this.addField(packageMethods);
		this.addField(publicMethods);
		this.addField(protectedMethods);
		this.addField(exMBoolPref);
	}

	@Override
	public boolean performOk() {
		if(store != null){
			store.setValue("Exhibicionist methods", this.exMBoolPref.getBooleanValue());
			store.setValue("Brain methods", this.brMBoolPref.getBooleanValue());
			store.setValue("Public", this.publicMethods.getBooleanValue());
			store.setValue("Protected", this.protectedMethods.getBooleanValue());
			store.setValue("Package", this.packageMethods.getBooleanValue());

		}
		return true;
	}

	@Override
	public void performApply(){
		this.performOk();
	}
	
	@Override
	public void performDefaults(){
		if(store != null){
			store.setDefault("Exhibicionist methods", false);
			store.setDefault("Brain methods", false);
			store.setDefault("Public", false);
			store.setDefault("Protected", false);
			store.setDefault("Package", false);

		}
	}
	
	@Override
	public boolean performCancel(){
		return true;
	}
	
	@Override
	public void init(IWorkbench workbench) {
		store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		store.setToDefault("Exhibicionist methods");
		store.setValue("Exhibicionist methods", false);
		store.setValue("Brain methods", false);
		this.performDefaults();
		setDescription("Choose your preferences for the SmellsLikeBadCoding plugin");
	}
	
	
	public IPreferenceStore getPreference(){
		return this.store;
	}
	
	

}