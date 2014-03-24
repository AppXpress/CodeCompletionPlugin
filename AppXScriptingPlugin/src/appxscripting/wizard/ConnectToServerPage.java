package appxscripting.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import appxscripting.Constants;

public class ConnectToServerPage extends WizardPage {
	
	public Text serverField;
	public Text datakeyField;
	public Text unameField;
	public Text pwdField;
	public Combo versionSelection;

	protected ConnectToServerPage(String pageName) {
		//page two of the new project wizard
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		
		//create grid layout for page
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
		
		//section for server url
		final Label labelUrl = new Label(container, SWT.NONE);
		final GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		labelUrl.setLayoutData(gridData);
		labelUrl.setText("");
		final Label labelServerField = new Label(container, SWT.NONE);
		final GridData gridData_1 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelServerField.setLayoutData(gridData_1);
		labelServerField.setText(Constants.PAGE_TWO_LABEL_URL);
		serverField = new Text(container, SWT.BORDER);
		serverField.setText(Constants.DEFAULT_URL);
		serverField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//section for api version
		final Label labelVersion = new Label(container, SWT.NONE);
		final GridData gridDataV1 = new GridData();
		gridDataV1.horizontalSpan = 3;
		labelVersion.setLayoutData(gridDataV1);
		labelVersion.setText("");
		final Label labelVersionField = new Label(container, SWT.NONE);
		final GridData gridDataV2 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelVersionField.setLayoutData(gridDataV2);
		labelVersionField.setText(Constants.PAGE_TWO_API_VERSION);
		versionSelection = new Combo(container, SWT.READ_ONLY);
		versionSelection.setItems(Constants.API_VERSIONS);
		versionSelection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL));
		versionSelection.select(0);
		
		//section for datakey
		final Label labelDataKey = new Label(container, SWT.NONE);
		final GridData gridData_2 = new GridData();
		gridData_2.horizontalSpan = 3;
		labelDataKey.setLayoutData(gridData_2);
		labelDataKey.setText("");
		final Label labelDataKeyField = new Label(container, SWT.NONE);
		final GridData gridData_3 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelDataKeyField.setLayoutData(gridData_3);
		labelDataKeyField.setText(Constants.PAGE_TWO_DATA_KEY);
		datakeyField = new Text(container, SWT.BORDER);
		datakeyField.setText(Constants.DEFAULT_DATA_KEY);
		datakeyField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//section for username
		final Label labelUsername = new Label(container, SWT.NONE);
		final GridData gridData_4 = new GridData();
		gridData_4.horizontalSpan = 3;
		labelUsername.setLayoutData(gridData_4);
		labelUsername.setText("");
		final Label labelUnameField = new Label(container, SWT.NONE);
		final GridData gridData_5 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelUnameField.setLayoutData(gridData_5);
		labelUnameField.setText(Constants.PAGE_TWO_UNAME);
		unameField = new Text(container, SWT.BORDER);
		unameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//section for password
		final Label labelPwd = new Label(container, SWT.NONE);
		final GridData gridData_6 = new GridData();
		gridData_6.horizontalSpan = 3;
		labelPwd.setLayoutData(gridData_6);
		labelPwd.setText("");
		final Label labelPwdField = new Label(container, SWT.NONE);
		final GridData gridData_7 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelPwdField.setLayoutData(gridData_7);
		labelPwdField.setText(Constants.PAGE_TWO_PWD);
		pwdField = new Text(container, SWT.BORDER);
		pwdField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	

	

}
