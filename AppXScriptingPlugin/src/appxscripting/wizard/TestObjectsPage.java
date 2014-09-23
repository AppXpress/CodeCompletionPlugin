package appxscripting.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import appxscripting.Constants;
import appxscripting.connections.Connections;

public class TestObjectsPage extends WizardPage {

	public Text mainIdField;
	Label labelMainId;

	protected TestObjectsPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);

		//section for mainobject id
		final Label labelId = new Label(container, SWT.NONE);
		final GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		labelId.setLayoutData(gridData);
		labelId.setText("");
		labelMainId = new Label(container, SWT.NONE);
		final GridData gridData_1 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelMainId.setLayoutData(gridData_1);
		labelMainId.setText("Reference object uid:");
		mainIdField = new Text(container, SWT.BORDER);
		//mainIdField.setText(Constants.DEFAULT_URL);
		mainIdField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		System.out.println("Fourth page called");
		
		SelectScriptingPage prevPage = (SelectScriptingPage)this.getPreviousPage();
		String mainObjectVal = prevPage.mainObjectSelection.getText();
		System.out.println("Page 3 selction: " + mainObjectVal);
		
		labelMainId.setText("Reference "+mainObjectVal+" uid:");
		
	}

//	public IWizardPage getNextPage() {
//		IWizardPage nextPage = super.getNextPage();
//		}
//
//		return nextPage;
//	}

}
