package ch.chrissharkman.accessibility.rcp.application.poc.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class QuitHandler {
	@Execute
	public void execute(IWorkbench workbench, Shell shell){
		if (MessageDialog.openConfirm(shell, "Anwendung schliessen",
				"Wollen Sie die «Proof of Concept»-Anwendung schliessen?")) {
			workbench.close();
		}
	}
}
