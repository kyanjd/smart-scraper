package maths_areas.arithmetic_algebra_logic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Logic1 {
	
	private Document doc;

	public Logic1(Document doc) {
		this.doc = doc;
		convert("&#x2227;", "and");
		convert("&#x2261;", "equivalent");
		convert("&#x21d2;", "implies");
		convert("&#x2228;", "or");
		convert("xor","xor");
	}

	private void convert(String pres, String cont) {
		List<Node> e = findNextElements(pres);
		while(e != null) {
			Node logic = e.get(0);
			Node a = e.get(1);
			Node b = e.get(2);
			Node parent = a.getParentNode();
			parent.removeChild(logic);
			Node apply = parent.insertBefore(doc.createElement("apply"), a);
			apply.appendChild(doc.createElement(cont));
			apply.appendChild(a);
			apply.appendChild(b);

			e = findNextElements(pres);
		}
	}
	
	/*
	 * Return a list of elements enclosed in floor brackets or surrounding a <mo>mod</mo>
	 */
	private List<Node> findNextElements(String pres){
		NodeList nl = doc.getElementsByTagName("mo");
		List<Node> elements = new ArrayList<Node>();
		
		for (int i = 0; i < nl.getLength(); i++) {
			Node next = nl.item(i);
			if(next.getTextContent().equalsIgnoreCase(pres)) {
				elements.add(next);
				elements.add(next.getPreviousSibling());
				elements.add(next.getNextSibling());
				return elements;
			}
		}		
		return null;
	}

}
