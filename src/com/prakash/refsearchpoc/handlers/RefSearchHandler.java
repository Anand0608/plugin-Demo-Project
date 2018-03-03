package com.prakash.refsearchpoc.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.prakash.refsearchpoc.util.RefSearchUtil;
import com.prakash.refsearchpoc.util.ValidateSearch;

/**
 * Our Reference Search handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 * 
 * @author PrakashA
 */

public class RefSearchHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart edtPart = page.getActiveEditor();
		String searchString = "";
		if (edtPart instanceof ITextEditor) {
			ISelectionProvider selectionProvider = ((ITextEditor) edtPart).getSelectionProvider();
			ISelection selection = selectionProvider.getSelection();
			if (selection instanceof ITextSelection) {
				ITextSelection textSelection = (ITextSelection) selection;
				if (textSelection.getLength() != 0) {
					searchString = textSelection.getText().trim();
				} 
				ITextEditor editor = (ITextEditor) edtPart;
				IDocumentProvider dp = editor.getDocumentProvider();
				IDocument doc = dp.getDocument(editor.getEditorInput());
				if(searchString.isEmpty()) {
					int currentPosition = textSelection.getOffset();
					try {
						searchString = getIdentifierText(doc, currentPosition);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
				
				if (!searchString.isEmpty() && ValidateSearch.getValidTypes(searchString)) {
					String[] wholecontent = doc.get().replace("\r", "").split("\n");
					RefSearchUtil searchUtill = new RefSearchUtil();
					searchUtill.currentEditorSearch(edtPart, wholecontent, searchString);
				} else {
					IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
					MessageDialog.openInformation(window.getShell(), "Operation Unavailable", 
							"The operation is unavailable on the current selection. Please select a valid java element name.");
				}
			}
		}
		
		return null;
	}

	/**
	 * Returns the word on which cursor is placed.
	 * 
	 * @return String
	 * @throws BadLocationException
	 */
	private String getIdentifierText(IDocument document, int currentPosition) throws BadLocationException {
		String text = null;
		String delimiters = ".+<>=()*?; \\n\\t";
		int length = 0;
		int newOffset = currentPosition;
		while (delimiters.indexOf(document.getChar(newOffset)) == -1) {
			newOffset--;
			length++;
		}

		while (delimiters.indexOf(document.getChar(currentPosition)) == -1) {
			currentPosition++;
			length++;
		}
		text = document.get(newOffset + 1, length - 1).trim();
		return text;
	}
}
