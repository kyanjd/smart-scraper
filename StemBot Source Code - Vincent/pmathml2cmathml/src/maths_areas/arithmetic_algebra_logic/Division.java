package maths_areas.arithmetic_algebra_logic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Division {
	
	private Document doc;;
	
	public Division(Document doc) {
		this.doc = doc;
		frac();
		signs();
	}
	
	/*
	 * Convert all mfrac tags to an apply
	 */
	private void frac() {
		while(getNext("mfrac") != null) {
			Node frac = getNext("mfrac");
			Node apply = doc.renameNode(frac, null, "apply");
			apply.insertBefore(doc.createElement("divide"), apply.getFirstChild());			
		}
	}
	
	/*
	 * Convert signs that represent division to an apply tag and add 
	 * the one element before and the one after.
	 */
	private void signs() {
		NodeList nl = doc.getElementsByTagName("mo");
		List<Node> divideSigns = new ArrayList<Node>();
		
		for (int i = 0; i < nl.getLength(); i++) {
			Node div = nl.item(i);
			if(div.getTextContent().equals("/") ||
					div.getTextContent().equals("&#x2215;") ||
					div.getTextContent().equals("÷") ||
					div.getTextContent().equals("&#xF7;")) {
				divideSigns.add(div);
			}
		}
		
		for(Node div : divideSigns) {
			doc.renameNode(div, null, "apply");
			div.setTextContent(null);
			div.appendChild(doc.createElement("divide"));
			div.appendChild(div.getPreviousSibling());
			div.appendChild(div.getNextSibling());
		}
		
	}
	
	/*
	 * Get the next tag by tagname; return null if not in document;
	 */
	private Node getNext(String tagName) {
		NodeList tempNodes = doc.getElementsByTagName(tagName);
		if(tempNodes.getLength() > 0) {
			return tempNodes.item(0);
		}else		
			return null;
	}

}
