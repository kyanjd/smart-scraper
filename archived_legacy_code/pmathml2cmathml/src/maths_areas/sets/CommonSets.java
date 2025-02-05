package maths_areas.sets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CommonSets {
	
	private Document doc;
	
	public CommonSets(Document doc) {
		this.doc = doc;
		convertEmpty();
		convert("Z", "integers");
		convert("N", "naturalnumbers");
		convert("C", "complexes");
		convert("R", "realnumbers");
		convert("Q", "rationals");
		convert("P", "primes");
	}

	private void convertEmpty() {
		NodeList nl = doc.getElementsByTagName("mi");
		
		for (int i = 0; i < nl.getLength(); i++) {
			Node next = nl.item(i);
			if(next.getTextContent().equalsIgnoreCase("&#x2205;")) {
				next.setTextContent(null);
				doc.renameNode(next, null, "emptyset");
			}
		}
		
	}

	private void convert(String pres, String cont) {
		while(findNext(pres)!= null) {
			Element el = findNext(pres);
			while (el.getAttributes().getLength() > 0) {
			    Node att = el.getAttributes().item(0);
			    el.getAttributes().removeNamedItem(att.getNodeName());
			}
			el.setTextContent(null);
			doc.renameNode(el, null, cont);
		}		
	}
	
	private Element findNext(String pres){
		NodeList nl = doc.getElementsByTagName("mi");
		
		for (int i = 0; i < nl.getLength(); i++) {
			Node next = nl.item(i);
			if(next.getTextContent().equalsIgnoreCase(pres)) {
				Element el = (Element)next;
				if(el.getAttribute("mathvariant").equals("double-struck")) {
					return el;
				}
			}
		}		
		return null;
	}


}
