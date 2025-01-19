package maths_areas.arithmetic_algebra_logic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Logic2 {
	
	private Document doc;

	public Logic2(Document doc) {
		this.doc = doc;
		convertNot("&#xac;", "not");
		convertTrueFalse("true");
		convertTrueFalse("false");
	}

	private void convertNot(String pres, String cont) {
		List<Node> e = findNextElements(pres);
		while(e != null) {
			Node logic = e.get(0);
			Node a = e.get(1);
			Node parent = a.getParentNode();
			parent.removeChild(logic);
			Node apply = parent.insertBefore(doc.createElement("apply"), a);
			apply.appendChild(doc.createElement(cont));
			apply.appendChild(a);

			e = findNextElements(pres);
		}
	}
	
	private void convertTrueFalse(String truefalse) {
		NodeList nl = doc.getElementsByTagName("mi");
		
		for (int i = 0; i < nl.getLength(); i++) {
			Node next = nl.item(i);
			if(next.getTextContent().equalsIgnoreCase(truefalse)) {
				next.setTextContent(null);
				doc.renameNode(next, null, truefalse);
				i--;
			}
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
				elements.add(next.getNextSibling());
				return elements;
			}
		}		
		return null;
	}
}
