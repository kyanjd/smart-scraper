package maths_areas.arithmetic_algebra_logic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Remainder {
	
	public Document doc;

	/*
	 * Constructor
	 */
	public Remainder(Document doc) {
		this.doc = doc;
		convert();
	}
	
	/*
	 * Convert list of elements to an apply tag
	 */
	private void convert() {
		List<Node> e = findNextElements();
		while(e != null) {
			//<mi>a</<mi><mo>mod</mo><mi>b</<mi>
			Node mod = e.get(0);
			Node a = e.get(1);
			Node b = e.get(2);
			Node parent = a.getParentNode();
			parent.removeChild(mod);
			Node apply = parent.insertBefore(doc.createElement("apply"), a);
			apply.appendChild(doc.createElement("rem"));
			apply.appendChild(a);
			apply.appendChild(b);

			e = findNextElements();
		}
	}
	
	/*
	 * Return a list of elements enclosed in floor brackets or surrounding a <mo>mod</mo>
	 */
	private List<Node> findNextElements(){
		NodeList nl = doc.getElementsByTagName("mo");
		List<Node> elements = new ArrayList<Node>();
		
		for (int i = 0; i < nl.getLength(); i++) {
			Node next = nl.item(i);
			if(next.getTextContent().equalsIgnoreCase("mod")) {
				elements.add(next);
				elements.add(next.getPreviousSibling());
				elements.add(next.getNextSibling());
				return elements;
			}
		}		
		return null;
	}

}
