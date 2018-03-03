package com.prakash.refsearchpoc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.prakash.refsearchpoc.view.RefSearchView;

public class RefSearchUtil {
	private int noOfSearch;
	private List<String> list = new ArrayList<String>();
	RefSearchView eView = null;
	
	public void currentEditorSearch(final IEditorPart edtPart, final String[] wholecontent, final String searchedString) {
		noOfSearch = 0;
		if (enableOS2200SearchView()) {
			Pattern pattern = Pattern.compile(searchedString);
			Matcher matcher = null;
			for (int linecount = 0; linecount < wholecontent.length; linecount++) {
				matcher = pattern.matcher(wholecontent[linecount]);
				if (matcher != null && matcher.find()) {
					list.add(linecount +1 + ": " + wholecontent[linecount]);
					noOfSearch++;
				}
			}
			
		if (list.size() > 0) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					eView.showSearchResult(edtPart ,list, searchedString, noOfSearch);
				}
			});
		}
		}
	}
	
	private boolean enableOS2200SearchView(){
		try {
			IWorkbench iW = (IWorkbench) PlatformUI.getWorkbench();
			IWorkbenchWindow[] windows = iW.getWorkbenchWindows();
			IWorkbenchPage pg = windows[0].getActivePage();
			IViewReference vr = pg.findViewReference(RefSearchView.ExtSearchViewId);
			if (vr != null) {
				eView = (RefSearchView) vr.getView(true);
			}
			if (eView == null) {
				eView = (RefSearchView) pg.showView(RefSearchView.ExtSearchViewId);
			}

			if (eView != null) {
				pg.showView(RefSearchView.ExtSearchViewId);
				eView.cleanview();
				list.clear();
				return true;
			}
			else {
				//Could not open the search View
			}
		} catch (Exception e) {
			//log exception
			return false;
		}
		return false;
}

}
