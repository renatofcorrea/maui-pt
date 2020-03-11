package com.entopix.maui.tests;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used for tests during development. Should be removed before release.
 * @author Rahmon Jorge
 *
 */

public class Testing {
	
	public static void main(String[] args) throws Exception {
		
		//String[] header = new String[] {"Very very very very long name","Short Data","Medium Sized Data","A Long Name for Data", "Data"};
		List<String[]> content = new ArrayList<>();
		content.add(new String[] {"First Line of Content", "Line 1, Column 1","Line 1, Column 2", "Line 1, Column 3","Line 1, Column 4"});
		content.add(new String[] {"Second Line of Content", "Line 2, Column 1","Line 2, Column 2", "Line 2, Column 3","Line 2, Column 4"});
		content.add(new String[] {"Third Line of Content", "Line 3, Column 1","Line 3, Column 2", "Line 3, Column 3","Line 3, Column 4"});
		
		
	}
}
