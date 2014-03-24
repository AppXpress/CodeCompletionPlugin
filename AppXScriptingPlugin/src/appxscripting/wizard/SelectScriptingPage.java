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

import appxscripting.Constants;
import appxscripting.connections.Connections;

public class SelectScriptingPage extends WizardPage {

	public Combo mainObjectSelection;
	public String[] mainObjectsList = {};
	List supportList;
	String objectListStr = null;
	Label label1;

	protected SelectScriptingPage(String pageName) {
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

		label1 = new Label(container, SWT.NONE);
		final GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		label1.setLayoutData(gridData);
		label1.setText("");
		final Label labelMainObject = new Label(container, SWT.NONE);
		final GridData gridData_1 = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelMainObject.setLayoutData(gridData_1);
		labelMainObject.setText(Constants.PAGE_THREE_SCRIPT_FOR);
		mainObjectSelection = new Combo(container, SWT.READ_ONLY);
		mainObjectSelection.setItems(mainObjectsList);
		mainObjectSelection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL));
		mainObjectSelection.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Selected index: "
						+ mainObjectSelection.getSelectionIndex()
						+ ", selected item: "
						+ mainObjectSelection.getItem(mainObjectSelection
								.getSelectionIndex())
						+ ", text content in the text field: "
						+ mainObjectSelection.getText());

				java.util.List<String> currentSupportItemList = Arrays
						.asList(mainObjectsList);
				ArrayList<String> allItems = new ArrayList<String>(
						currentSupportItemList);
				int indexOfITemToRemove = mainObjectSelection
						.getSelectionIndex();
				allItems.remove(indexOfITemToRemove);
				String[] newList = (String[]) (allItems
						.toArray(new String[allItems.size()]));
				supportList.setItems(newList);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		final Label label2 = new Label(container, SWT.NONE);
		final GridData gridData1 = new GridData();
		gridData.horizontalSpan = 3;
		label2.setLayoutData(gridData1);
		label2.setText("");
		final Label labelSupportObjects = new Label(container, SWT.NONE);
		final GridData gridData_2 = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelSupportObjects.setLayoutData(gridData_2);
		labelSupportObjects.setText(Constants.PAGE_THREE_SUPPORT_LIST);
		supportList = new List(container, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.MULTI);
		supportList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL));
		// supportList.setBounds(clientArea.x, clientArea.y,400,100);
		supportList.setItems(mainObjectsList);

	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		System.out.println("Third page called");
	}

	public IWizardPage getNextPage() {
		IWizardPage nextPage = super.getNextPage();
		if (nextPage == null) {// select scripting objects page is the final
								// page in the wizard
			System.out.println("Select scripting page called");
			CustomProjectNewWizard wiz = (CustomProjectNewWizard) getWizard();

			// get object list
			final ConnectToServerPage pageTwo = (ConnectToServerPage) wiz
					.getPages()[1];
			final String urlStr = pageTwo.serverField.getText();
			final String versionStr = pageTwo.versionSelection.getText();
			final String dataKeyStr = pageTwo.datakeyField.getText();
		    String unameStr = pageTwo.unameField.getText();
		    String pwdStr = pageTwo.pwdField.getText();
		    String strToEncode = unameStr +":"+pwdStr;
		    byte[] encodedBytes = Base64.encodeBase64(strToEncode.getBytes());
		    final String authStr = "Basic "+ new String(encodedBytes);
			// do in background thread
			try {
				wiz.getContainer().run(true, false,new IRunnableWithProgress(){
				     public void run(IProgressMonitor monitor) {
				         monitor.beginTask("Fetching object list ...", 100);
				         // execute the task ...
							Display.getDefault().asyncExec(new Runnable() {

								@Override
								public void run() {
									try {
										Connections.authStr = authStr;//set static auth string value during first access
										objectListStr = Connections.sendGet(urlStr, versionStr,
												"", dataKeyStr);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									System.out.println("Object list: " + objectListStr);
									if (objectListStr != null && objectListStr.length() > 0) {
										try {
											JSONObject jsonObject = JSONObject.fromObject(objectListStr);
											JSONArray objectList = jsonObject.getJSONArray("objectType");
											ArrayList<String> objectListArr = new ArrayList<String>();
											for (int i=0; i < objectList.size(); i++) {
												String objName = objectList.getString(i);
												//if(objName.charAt(0) != "$".toCharArray()[0])//excludes custom objects
													objectListArr.add(objName);
											}
											mainObjectSelection.setItems((String[]) objectListArr
													.toArray(new String[objectListArr
																		.size()]));
											String defaultSelection = "orderdetail";// set orderdetail as default
											mainObjectSelection.setText(defaultSelection);
											mainObjectsList = (String[]) (objectListArr
													.toArray(new String[objectListArr.size()]));
											objectListArr.remove(defaultSelection);
											String[] listItems = (String[]) (objectListArr
													.toArray(new String[objectListArr.size()]));
											supportList.setItems(listItems);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							});
				         monitor.done();
				     }
				 });
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1){
				e1.printStackTrace();
			}


			// getContainer().updateButtons();
		}

		return nextPage;
	}

}
