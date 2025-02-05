package maths_areas.constants;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Numbers {
	
	private Document doc;

	public Numbers(Document doc) {
		this.doc = doc;
		convert("&#x3c0;", "pi");
		convert("&#x221e;", "infinity");
		convert("nan", "notanumber");
		convert("i", "imaginaryi");
		convert("&#x3b3;", "eulergamma");
		convert("e", "exponentiale");
	}
	
	private void convert(String pres, String cont) {
		while(findNext(pres)!= null) {
			Node node = findNext(pres);
			while (node.getAttributes().getLength() > 0) {
			    Node att = node.getAttributes().item(0);
			    node.getAttributes().removeNamedItem(att.getNodeName());
			}
			node.setTextContent(null);
			doc.renameNode(node, null, cont);
		}		
	}
	
	private Node findNext(String pres){
		NodeList nl = doc.getElementsByTagName("mi");
		
		for (int i = 0; i < nl.getLength(); i++) {
			Node next = nl.item(i);
			if(next.getTextContent().equalsIgnoreCase(pres)) {
				return next;
			}
		}		
		return null;
	}

}
