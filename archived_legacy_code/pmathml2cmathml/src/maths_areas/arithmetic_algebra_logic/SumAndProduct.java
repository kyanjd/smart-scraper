package maths_areas.arithmetic_algebra_logic;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SumAndProduct {

	private Document doc;
	
	public SumAndProduct(Document doc) {
		this.doc = doc;
		convert("&#x2211;", "sum");
		convert("&#x220f;", "product");
	}

	private void convert(String pres, String cont) {
		while(findNext(pres) != null) {
			Node sum = findNext(pres);
			sum.setTextContent(null);
			doc.renameNode(sum, null, cont);
			
			Node parent = sum.getParentNode();
			if(parent.getNodeName().equals("munderover")) {
				munderover(parent);
			}else if(parent.getNodeName().equals("munder")) {
				munder(parent);
			}else {
				
			}
		}
	}

	private void munder(Node apply) {
		doc.renameNode(apply, null, "apply");
		Node under = apply.getFirstChild().getNextSibling();
		Node func = apply.getNextSibling();
		
		if(under.getFirstChild().getNodeName().equals("apply")) {
			Node bvar = doc.createElement("bvar");
			Node cond = doc.createElement("condition");
			cond.appendChild(under.getLastChild());
			bvar.appendChild(cond.getFirstChild().getFirstChild().getNextSibling().cloneNode(true));
			apply.removeChild(under);
			apply.appendChild(bvar);
			apply.appendChild(cond);
			apply.appendChild(func);
		}else if(under.getNodeName().equals("mi")) {
			Node dom = doc.createElement("domainofapplication");
			dom.appendChild(under);
			Element e = (Element) dom.getFirstChild();
			e.setAttribute("type", "set");
			apply.appendChild(dom);
			apply.appendChild(func);
		}
		setFunctionType(func);
	}

	private void munderover(Node apply) {
		doc.renameNode(apply, null, "apply");
		Node under = apply.getFirstChild().getNextSibling();
		Node over = apply.getLastChild();
		Node func = apply.getNextSibling();
		Node bvar = doc.createElement("bvar");
		Node low = doc.createElement("lowlimit");
		Node up = doc.createElement("uplimit");
		low.appendChild(under.getLastChild());
		bvar.appendChild(under.getFirstChild());
		apply.removeChild(under);
		up.appendChild(over);
		apply.appendChild(bvar);
		apply.appendChild(low);
		apply.appendChild(up);
		apply.appendChild(func);
		setFunctionType(func);
	}
	
	private void setFunctionType(Node func) {
		if(func.getNodeName().equals("mrow")) {
			setFunctionType(func.getFirstChild());
		}else if(func.getNodeName().equals("apply") &&
				func.getFirstChild().getNodeName().equals("mi")) {
			Element e = (Element) func.getFirstChild();
			e.setAttribute("type", "function");
		}			
		
	}


	private Node findNext(String pres) {
		NodeList nl = doc.getElementsByTagName("mo");
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n.getTextContent().equals(pres)) {
				return n;
			}
		}
		return null;
	}

}
