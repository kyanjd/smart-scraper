package maths_areas.trigonometry;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ArcTrig {

	private Document doc;

	public ArcTrig(Document doc) {
		this.doc = doc;
		removeMrow();
		convert("sin");
		convert("cos");
		convert("tan");
		convert("sinh");
		convert("cosh");
		convert("tanh");
	}

	private void convert(String pres) {
		while(findNext(pres)!=null) {
			Node parent = findNext(pres);
			doc.renameNode(parent, null, "arc" + pres);
			while(parent.getFirstChild()!=null) {
				parent.removeChild(parent.getFirstChild());
			}
		}
	}

	private Node findNext(String pres) {
		NodeList nl = doc.getElementsByTagName(pres);
		for(int i = 0; i < nl.getLength(); i ++) {
			Node next = nl.item(i);
			Node parent = next.getParentNode();
			if(parent.getNodeName().equals("apply") && parent.getFirstChild().getNodeName().equals("power")) {
				Node minusOne = parent.getLastChild();
				if(minusOne.hasChildNodes() && minusOne.getFirstChild().getNodeName().equals("minus") &&
						minusOne.getLastChild().getTextContent().equals("1"))
					return parent;					
			}
		}
		return null;
	}
	
	
	private void removeMrow() {
		NodeList nl = doc.getElementsByTagName("mrow");
		for(int i = 0; i < nl.getLength(); i ++) {
			Node row = nl.item(i);
			if(row.getChildNodes().getLength() == 1) {
				row.getParentNode().insertBefore(row.getFirstChild(), row);
				row.getParentNode().removeChild(row);
				i--;
			}				
		}
	}

}
