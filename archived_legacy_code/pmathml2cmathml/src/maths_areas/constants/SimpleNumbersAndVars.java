package maths_areas.constants;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleNumbersAndVars {

	private Document doc;

	public SimpleNumbersAndVars(Document doc) {
		this.doc = doc;
		removeMrows();
		convert();
	}

	/*
	 * Convert all non-special characters
	 */
	private void convert() {
		NodeList nl = doc.getElementsByTagName("mn");
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(!n.getTextContent().endsWith(";")) {
				doc.renameNode(n, null, "cn");
			}
		}
		nl = doc.getElementsByTagName("mi");
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(!n.getTextContent().endsWith(";")) {
				doc.renameNode(n, null, "ci");
			}
		}
		
	}

	private void removeMrows() {
		NodeList nl = doc.getElementsByTagName("mrow");
		for(int i = 0; i < nl.getLength(); i++) {
			Node row = nl.item(i);
			if(row.getChildNodes().getLength() == 1) {
				row.getParentNode().replaceChild(row.getFirstChild(), row);
				i --;
			}
		}		
	}

}
