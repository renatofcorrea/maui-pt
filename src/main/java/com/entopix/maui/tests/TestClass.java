package com.entopix.maui.tests;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used for tests during development. Should be removed before release.
 * @author Rahmon Jorge
 *
 */

public class TestClass {
	
	public static void main(String[] args) throws Exception {
		
		List<Object[]> list1;
		List<String[]> list2 = new ArrayList<>();
		list2.add(new String[] {"1","a","b"});
		
		list1 = new ArrayList<Object[]>(list2);
		
		list1.toString();
	}
}
