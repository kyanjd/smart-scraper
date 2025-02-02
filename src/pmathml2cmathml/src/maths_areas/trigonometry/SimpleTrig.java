package maths_areas.trigonometry;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleTrig {

	private Document doc;

	public SimpleTrig(Document doc) {
		this.doc = doc;
		convert("sin");
		convert("cos");
		convert("tan");
		convert("sinh");
		convert("cosh");
		convert("tanh");

		convert("arcsin");
		convert("arccos");
		convert("arctan");
		convert("arcsinh");
		convert("arccosh");
		convert("arctanh");
	}

	private void convert(String pres) {
		while(findNext(pres)!=null) {
			Node next = findNext(pres);
			next.setTextContent(null);
			doc.renameNode(next, null, pres);
		}
	}

	private Node findNext(String pres) {
		NodeList nl = doc.getElementsByTagName("mi");
		for(int i = 0; i < nl.getLength(); i ++) {
			Node next = nl.item(i);
			if(next.getTextContent().equals(pres))
				return next;
		}
		return null;
	}

}
