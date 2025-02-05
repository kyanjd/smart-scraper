package maths_areas.sets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Intervals {
	
	private Document doc;

	public Intervals(Document doc) {
		this.doc = doc;
		convert();
	}

	private void convert() {
		while(findNext() != null) {
			Element fenced = (Element) findNext();
			String open = fenced.getAttribute("open");
			String close = fenced.getAttribute("close");
			if(open.equals("")||open.equals("(")) open = "open";
			if(close.equals("")||close.equals(")")) close = "open";
			if(open.equals("[")) open = "closed";
			if(close.equals("]")) close = "closed";
			String closure = open;
			if(!close.equals(closure))closure += "-" + close;
			while (fenced.getAttributes().getLength() > 0) {
			    Node att = fenced.getAttributes().item(0);
			    fenced.getAttributes().removeNamedItem(att.getNodeName());
			}
			fenced.setAttribute("closure", closure);
			doc.renameNode(fenced, null, "interval");
		}
		
	}

	private Node findNext() {
		NodeList nl = doc.getElementsByTagName("mfenced");
		for(int i = 0; i< nl.getLength(); i++) {
			Node fenced = nl.item(i);
			if(fenced.getChildNodes().getLength() == 2) {
				return fenced;
			}
		}
		return null;
	}

}
