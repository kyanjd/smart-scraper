package maths_areas.relations;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Equations {

	private Document doc;
	
	public Equations(Document doc) {
		this.doc = doc;
		convert("=", "eq");
		convert("&#x2260;", "neq");
		convert("&gt;", "gt");
		convert("&lt;", "lt");
		convert("&#x2265;", "geq");
		convert("&#x2264;", "leq");
		convert("&#x2261;", "equivalent");
		convert("&#x2243;", "approx");
	}

	private void convert(String pres, String content) {
		while(findNext(pres) != null) {
			Node e = findNext(pres);
			doc.renameNode(e, null, "apply");
			e.setTextContent(null);
			e.appendChild(doc.createElement(content));
			while(e.getPreviousSibling() != null) {
				e.appendChild(e.getPreviousSibling());
			}
			while(e.getNextSibling() != null) {
				e.appendChild(e.getNextSibling());
				if(e.getLastChild().getTextContent().equalsIgnoreCase(pres))
					e.removeChild(e.getLastChild());
			}
		}
		
	}

	private Node findNext(String pres) {
		NodeList nl = doc.getElementsByTagName("mo");
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getTextContent().equalsIgnoreCase(pres))
				return nl.item(i);
		}
		return null;
	}
}
