package com.prakash.refsearchpoc.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.prakash.refsearchpoc.view.RefSearchView;

/**
 * Our Reference Next handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 * 
 * @author PrakashA
 */

public class RefNextHandler extends AbstractHandler {

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

			for (int i = 0; i < list.size();) {
				String searchLine = list.get(i);
				int indexNo = searchLine.indexOf(":");
				int lineNo = Integer.parseInt(searchLine.substring(0, indexNo));
				if (lineNo > currentPosition && edtPart instanceof ITextEditor) {
					RefSearchView refSearchView = new RefSearchView();
					refSearchView.highLightText((ITextEditor) edtPart, lineNo);
					break;
				}
				if (i == list.size() - 1) {
					i = 0;
					currentPosition = 0;
				} else {
					i++;
				}
			}
		} else {
			System.out.println("reach here");
		}

		return null;
	}
}
