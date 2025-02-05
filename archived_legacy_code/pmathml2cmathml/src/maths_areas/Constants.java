package maths_areas;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import maths_areas.constants.Numbers;
import maths_areas.constants.SimpleNumbersAndVars;
import maths_areas.constants.SpecialNumbers;

public class Constants {

	private Document doc;
	
	public Constants(Document doc) {
		this.doc = doc;
	}
	
	/*
	 * Convert all presentation that maps onto this area of maths
	 */
	@SuppressWarnings("unused")
	public void convert() {
		removeMrows();
		Numbers nums = new Numbers(doc);
		SpecialNumbers snums = new SpecialNumbers(doc);
		SimpleNumbersAndVars nv = new SimpleNumbersAndVars(doc);
		removeMrows();
	}

	/*
	 * Remove all mfenced and mrows with only 1 child tag
	 */
	private void removeMrows() {
		NodeList nl = doc.getElementsByTagName("mfenced");
		for(int i = 0; i < nl.getLength(); i ++) {
			Node fence = nl.item(i);
			if(fence.getChildNodes().getLength() == 1) {
				Node parent = fence.getParentNode();
				parent.insertBefore(fence.getFirstChild(), fence);
				parent.removeChild(fence);
				i--;
			}
		}
		nl = doc.getElementsByTagName("mrow");
		for(int i = 0; i < nl.getLength(); i ++) {
			Node row = nl.item(i);
			if(row.getChildNodes().getLength() == 1) {
				Node parent = row.getParentNode();
				parent.insertBefore(row.getFirstChild(), row);
				parent.removeChild(row);
				i--;
			}
		}
		
	}
}
