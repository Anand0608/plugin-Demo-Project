package com.prakash.refsearchpoc.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.prakash.refsearchpoc.view.RefSearchView;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * Our Reference Back handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 * 
 * @author PrakashA
 */

public class RefBackHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		List<String> list = RefSearchView.list;
		IEditorPart edtPart = RefSearchView.edtPart;
		int currentPosition = 0;

		if (!list.isEmpty()) {
			if (edtPart instanceof ITextEditor) {
				ISelectionProvider selectionProvider = ((ITextEditor) edtPart).getSelectionProvider();
				ISelection selection = selectionProvider.getSelection();
				if (selection instanceof ITextSelection) {
					ITextSelection textSelection = (ITextSelection) selection;
					currentPosition = textSelection.getEndLine() + 1;
				}
			}
			boolean isStartFromLast = false;

			for (int i = list.size()-1; i >= 0;) {
				String searchLine = list.get(i);
				int indexNo = searchLine.indexOf(":");
				int lineNo = Integer.parseInt(searchLine.substring(0, indexNo));
				if ((lineNo < currentPosition || isStartFromLast)  && edtPart instanceof ITextEditor) {
					RefSearchView refSearchView = new RefSearchView();
					refSearchView.highLightText((ITextEditor) edtPart, lineNo);
					break;
				}
				
				if (i == 0) {
					i = list.size()-1;
					isStartFromLast = true;
				} else {
					i--;
				}
			}
		} else {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			MessageDialog.openInformation(window.getShell(), "Info", "Please perform the search operation before using this option.");
		}

		return null;
	}
}
