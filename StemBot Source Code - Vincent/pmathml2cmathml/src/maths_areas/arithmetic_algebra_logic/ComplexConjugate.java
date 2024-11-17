package maths_areas.arithmetic_algebra_logic;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ComplexConjugate {
	
	private Document doc;
	
	public ComplexConjugate(Document doc) {
		this.doc = doc;
		convert();
	}

	private void convert() {
		Node n = findMacron();
		while(n != null) {
			Node apply = doc.renameNode(n, null, "apply");
			Node macron = apply.getLastChild();
			macron.setTextContent(null);
			doc.renameNode(macron, null, "conjugate");
			apply.insertBefore(macron, apply.getFirstChild());
			n = findMacron();
		}		
	}

	/*
	 * Find an mover tag where the second element is a macron.
	 */
	private Node findMacron() {
		NodeList nl = doc.getElementsByTagName("mover");
		for(int i = 0; i < nl.getLength(); i ++) {
			Node over = nl.item(i);
			if(over.getLastChild().getTextContent().equalsIgnoreCase("&#xaf;"))
				return over;
		}
		return null;
	}

}
