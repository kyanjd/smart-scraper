package maths_areas.arithmetic_algebra_logic;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Indicies {
	
	private Document doc;
	
	public Indicies(Document doc) {
		this.doc = doc;
		sup();
		subsup();
		sqrt();
		root();
	}
	
	
	/*
	 * Find and convert all <msup> to power
	 */
	private void sup() {
		while (getNext("msup") != null) {
			Node sup = getNext("msup");
			doc.renameNode(sup, null, "apply");
			sup.insertBefore(doc.createElement("power"), sup.getFirstChild());
		}
	}

	/*
	 * Find all cases of <msubsup> and convert to power
	 */
	private void subsup() {
		while(getNext("msubsup") != null) {
			Node ss = getNext("msubsup");
			Node apply = doc.createElement("apply");
			apply.appendChild(doc.createElement("power"));
			Node sub = doc.createElement("msub");
			sub.appendChild(ss.getFirstChild());
			sub.appendChild(ss.getFirstChild());
			apply.appendChild(sub);
			apply.appendChild(ss.getLastChild());
			ss.getParentNode().replaceChild(apply, ss);
		}
	}
	
	/*
	 * Find and  convert all <msqrt>
	 */
	private void sqrt() {
		while(getNext("msqrt") != null) {
			Node sqrt = getNext("msqrt");
			Node apply = doc.createElement("apply");
			sqrt.getParentNode().insertBefore(apply, sqrt);
			apply.appendChild(doc.createElement("root"));
			Node degree = doc.createElement("degree");
			Node dnum = doc.createElement("ci");
			dnum.setTextContent("2");
			degree.appendChild(dnum);
			apply.appendChild(degree);
			Node root;
			if(sqrt.getChildNodes().getLength() > 1) {
				root = doc.renameNode(sqrt, null, "mrow");				
			}else {
				root = sqrt.getFirstChild();
				sqrt.getParentNode().removeChild(sqrt);
			}
			apply.appendChild(root);
		}
		
	}
	
	/*
	 * Find and  convert all <mroot>
	 */
	private void root() {
		while(getNext("mroot") != null) {
			Node root = getNext("mroot");
			doc.renameNode(root, null, "apply");
			Node degree = doc.createElement("degree");
			degree.appendChild(root.getLastChild());
			root.insertBefore(degree, root.getFirstChild());
			root.insertBefore(doc.createElement("root"), root.getFirstChild());
			
		}
	}

	/*
	 * Get next occurrence of a tagname
	 */
	private Node getNext(String tagName) {
		NodeList tempNodes = doc.getElementsByTagName(tagName);
		if(tempNodes.getLength() > 0) {
			return tempNodes.item(0);
		}else		
			return null;
	}
	
}
