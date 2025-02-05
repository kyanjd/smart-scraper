package maths_areas.arithmetic_algebra_logic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Absolute {
	
	public Document doc;

	/*
	 * Constructor
	 */
	public Absolute(Document doc) {
		this.doc = doc;
		convert();
	}
	
	/*
	 * Convert list of elements to an apply tag
	 */
	private void convert() {
		List<Node> e = findNextElements();
		while(e != null) {
			Node parent = e.get(0).getParentNode();
			Node apply = doc.createElement("apply");
			parent.insertBefore(apply, e.get(0));
			String fun = e.get(0).getTextContent().equals("|")? "abs" : "ceiling";
			apply.appendChild(doc.createElement(fun));
			for(Node n : e) {
				apply.appendChild(n);
			}
			apply.removeChild(e.get(0));
			apply.removeChild(e.get(e.size() - 1));
			e = findNextElements();
		}
	}
	
	/*
	 * Return a list of elements enclosed in floor brackets or surrounding a <mo>div</mo>
	 */
	private List<Node> findNextElements(){
		NodeList nl = doc.getElementsByTagName("mo");
		List<Node> elements = new ArrayList<Node>();
		
		for (int i = 0; i < nl.getLength(); i++) {
			Node next = nl.item(i);
			boolean complete = false;
			
			if(next.getTextContent().equalsIgnoreCase("|")) {
				elements.add(next);
				next = next.getNextSibling();
				while(next != null) {
					if(next.getTextContent().equalsIgnoreCase("|")) {
						elements.add(next);
						next = null;
						complete = true;
					}
					else {
						elements.add(next);
						next = next.getNextSibling();
					}
				}
				if(complete) return elements;
			}else if(next.getTextContent().equalsIgnoreCase("&#x2308;")) {
				elements.add(next);
				next = next.getNextSibling();
				while(next != null) {
					elements.add(next);
					if(next.getTextContent().equalsIgnoreCase("&#x2309;")) {
						next = null;
						complete = true;
					}
					else {
						next = next.getNextSibling();
					}
				}
				if(complete) return elements;
			}
		}
		
		return null;
	}

}
