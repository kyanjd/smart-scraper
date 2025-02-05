package maths_areas.arithmetic_algebra_logic;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FunctionApplication {

	private Document doc;
	
	public FunctionApplication(Document doc) {
		this.doc = doc;
		convert();
	}
	
	private void convert() {
		while(findFunction() != null) {
			Node apply = findFunction();
			doc.renameNode(apply, null, "apply");
			apply.setTextContent(null);
			apply.appendChild(apply.getPreviousSibling());
			apply.appendChild(apply.getNextSibling());
		}
	}
	
	/*
	 * Find the function by node text content 
	 */
	private Node findFunction() {
		NodeList nl = doc.getElementsByTagName("mo");		
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getTextContent().equalsIgnoreCase("&#x2061;")) {		
				return nl.item(i);
			}
		}
		return null;
	}

}
