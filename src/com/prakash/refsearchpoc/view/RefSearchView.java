package com.prakash.refsearchpoc.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.internal.ui.viewsupport.ColoredViewersManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class RefSearchView extends ViewPart {

	static List<Node> nodes = new ArrayList<Node>();
	public static List<String> list = new ArrayList<String>();
	static Tree tree = null;
	static TreeViewer treeViewer = null;
	private List<Node> lister;
	private static Link headerLb;
	private static int totalSearchFound;
	private static String searchStr;
	Node eltPath = null;
	public static IEditorPart edtPart = null;
	public static final String ExtSearchViewId = "com.prakash.refsearchpoc.view.ExtSearchView";

	@Override
	public void createPartControl(Composite parent) {

		parent.setLayout(new FillLayout());
		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);

		GridData g1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		g1.horizontalSpan = 1;

		GridData g2 = new GridData(GridData.FILL_HORIZONTAL);
		g2.horizontalSpan = 1;

		headerLb = new Link(parent, SWT.None);
		headerLb.setText("No search results available.");
		headerLb.setLayoutData(g2);

		tree = new Tree(parent, SWT.H_SCROLL/* | SWT.VIRTUAL */ | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);

		treeViewer = new TreeViewer(tree);
		treeViewer.setContentProvider(new MyTreeContentProvider());
		lister = new ArrayList<Node>();
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
				if (selection.isEmpty())
					return;

				List<?> list = selection.toList();
				RefSearchView.Node nod = (RefSearchView.Node) list.get(0);

				if (nod.getName() != null) {
					int indexNo = nod.getName().indexOf(":");
					int lineNo = Integer.parseInt(nod.getName().substring(0, indexNo));
					if ( edtPart instanceof ITextEditor ) {
						highLightText((ITextEditor)edtPart, lineNo);
					}
				}
			}

		});
		tree.setLayoutData(g1);
		if (nodes != null && searchStr != null && totalSearchFound != 0) {
			treeViewer.setLabelProvider(new MyLabelProvider());
			treeViewer.setInput(nodes);
			headerLb.setText("'" + searchStr + "' - " + totalSearchFound 	+ "\\ line(s) matched "); 
		}

		MenuManager popManager = new MenuManager();

		Menu menu = popManager.createContextMenu(treeViewer.getTree());
		treeViewer.getTree().setMenu(menu);

	}


	public void highLightText(ITextEditor editor, int lineNo) {
		IDocumentProvider prov = editor.getDocumentProvider();
		if (prov == null)
			return;
		IDocument doc = prov.getDocument(editor.getEditorInput());
		try {
			int offset = doc.getLineOffset(lineNo - 1);
			int lineLength = doc.getLineLength(lineNo - 1);
			editor.setHighlightRange(offset, lineLength, true);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void showSearchResult(IEditorPart edtPart, List<String> searchList, String stringSearch, int noOfSearch) {

		RefSearchView.searchStr = stringSearch;
		RefSearchView.edtPart = edtPart;
		RefSearchView.totalSearchFound = noOfSearch;
		RefSearchView.list = searchList;
		IEditorInput input = edtPart.getEditorInput();
		String elementNode = "";
		String eltName ="";
		if (input instanceof FileEditorInput) {
			String filePath = ((FileEditorInput) input).getFile().toString();
			eltName = ((FileEditorInput) input).getName();
			elementNode = eltName + "[" + filePath + "]" + searchList.size();
		}
		headerLb.setText("'" + searchStr + "' - " + totalSearchFound + "\\ line(s) matched in " + eltName);

		Node eltPath = new Node(elementNode, null);
		nodes.add(eltPath);
		for (int i = 0; i < searchList.size(); i++) {
			Node searchnod = new Node(searchList.get(i).toString(), eltPath);
			lister.add(searchnod);
		}

		treeViewer.setLabelProvider(new MyLabelProvider());
		treeViewer.setInput(nodes);
	}

	class MyLabelProvider extends StyledCellLabelProvider {

		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			StyledString text = new StyledString();

			if (((Node) element).parent != null) {
				String searchNode = ((Node) element).getName();
				higilightNode(searchNode, text);

				cell.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OPEN_MARKER));
			} else {

				String fileName = ((Node) element).getName();
				String path = fileName.substring(fileName.indexOf("[") + 1, fileName.indexOf("]") + 1); 
				String fName = fileName.substring(0, fileName.indexOf("[")); 
				String noOfFile = fileName.substring(fileName.indexOf("]") + 1, fileName.length()); 
				text.append(fName + " "); 
				path = "[" + path;
				text.append(path, StyledString.QUALIFIER_STYLER);
				text.append(" (" + noOfFile + "\\ line(s) matched)", StyledString.COUNTER_STYLER);
				cell.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));
			}

			cell.setText(text.toString());
			cell.setStyleRanges(text.getStyleRanges());
			super.update(cell);
		}

		private void higilightNode(String searchNode, StyledString text) {

			int indexNo = searchNode.indexOf(":") + 1; //$NON-NLS-1$
			String lineNo = searchNode.substring(0, indexNo);
			searchNode = searchNode.substring(indexNo);
			int startIndex = 0;
			int endIndex = 0;
			String searchTempNode = searchNode;
			text.append(lineNo, StyledString.QUALIFIER_STYLER);
			while (searchTempNode.contains(searchStr)) {
				startIndex = searchTempNode.indexOf(searchStr) + endIndex;
				text.append(searchNode.substring(endIndex, startIndex));
				endIndex = startIndex + searchStr.length();
				text.append(searchNode.substring(startIndex, endIndex), StyledString.createColorRegistryStyler(
						ColoredViewersManager.INHERITED_COLOR_NAME, ColoredViewersManager.HIGHLIGHT_BG_COLOR_NAME));
				searchTempNode = searchNode.substring(endIndex);

			}
			text.append(searchNode.substring(endIndex));

		}
	}

	class MyTreeContentProvider implements ITreeContentProvider {
		public Object[] getChildren(Object parentElement) {
			List<?> subcats = ((Node) parentElement).getSubCategories();
			return subcats == null ? new Object[0] : subcats.toArray();
		}

		public Object getParent(Object element) {
			return ((Node) element).getParent();
		}

		public boolean hasChildren(Object element) {
			return ((Node) element).getSubCategories() != null;
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement != null && inputElement instanceof List) {
				return ((List<?>) inputElement).toArray();
			}
			return new Object[0];
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	public class Node {
		public String name;

		public ArrayList<Node> subCategories;

		public Node parent;

		public Node(String name, Node parent) {
			this.name = name;
			this.parent = parent;
			if (parent != null)
				parent.addSubCategory(this);
		}

		public ArrayList<Node> getSubCategories() {
			return subCategories;
		}

		private void addSubCategory(Node subcategory) {
			if (subCategories == null)
				subCategories = new ArrayList<Node>();
			if (!subCategories.contains(subcategory))
				subCategories.add(subcategory);
		}

		public String getName() {
			return name;
		}

		public Node getParent() {
			return parent;
		}

		/**
		 * this is used for cleaning the node object from the heap.
		 * 
		 */
		public void clear() {
			if (subCategories != null) {
				Iterator<Node> iter = subCategories.iterator();
				while (iter.hasNext()) {
					Node node = iter.next();
					node.clear();// this will called recursively to clean sub-child
					node.name = null;
				}
			}

		}

	}

	public void cleanview() {
		for (Node node : nodes) {
			node.clear();
		}
		nodes.clear();

		treeViewer.refresh();

		headerLb.setText("No search results available."); //$NON-NLS-1$
		searchStr = ""; //$NON-NLS-1$
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}