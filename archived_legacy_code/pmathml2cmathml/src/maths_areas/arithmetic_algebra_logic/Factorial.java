package maths_areas.arithmetic_algebra_logic;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Factorial {

	public Document doc;

	/*
	 * Constructor
	 */
	public Factorial(Document doc) {
		this.doc = doc;
		convert();
	}

	/*
	 * Convert n! to apply tag
	 */
	private void convert() {
		Node n = findNextFactorial();
		while(n != null) {
			Node apply = doc.renameNode(n, null, "apply");
			apply.setTextContent(null);
			apply.appendChild(doc.createElement("factorial"));
			apply.appendChild(apply.getPreviousSibling());
			n = findNextFactorial();
		}
		
	}

	/*
	 * Find the next ! in an mo tag. Return null if last.
	 */
	private Node findNextFactorial() {
		NodeList nl = doc.getElementsByTagName("mo");
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getTextContent().equals("!"))
				return nl.item(i);
		}
		return null;
	}
}
