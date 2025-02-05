package maths_areas.relations;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FactorOf {

	private Document doc;
	
	//Assuming that sets have been converted first
	
	public FactorOf(Document doc) {
		this.doc = doc;
		convert();
	}

	private void convert() {
		while(findNext() != null) {
			Node e = findNext();
			doc.renameNode(e, null, "apply");
			e.setTextContent(null);
			e.appendChild(doc.createElement("factorof"));
			while(e.getPreviousSibling() != null) {
				e.appendChild(e.getPreviousSibling());
			}
			while(e.getNextSibling() != null) {
				e.appendChild(e.getNextSibling());
			}
		}
		
	}

	private Node findNext() {
		NodeList nl = doc.getElementsByTagName("mo");
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			int pipes = 0;
			if(n.getTextContent().equalsIgnoreCase("|")) {
				Node parent = n.getParentNode();
				NodeList siblings = parent.getChildNodes();
				for(int j = 0; j < siblings.getLength(); j++) {
					if(siblings.item(j).getTextContent().equalsIgnoreCase("|"))
						pipes ++;
				}
				if(pipes == 1)
					return n;
			}
	
		}
		return null;
	}
}
