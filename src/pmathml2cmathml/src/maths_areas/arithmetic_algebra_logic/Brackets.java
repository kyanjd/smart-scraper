package maths_areas.arithmetic_algebra_logic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Brackets {

	private Document doc;
	
	public Brackets(Document doc) {
		this.doc = doc;
		try {
			sortBrackets();
		}catch (NullPointerException e) {
			System.out.println("The bracket grouping you have selected does not fit.\n"
					+ "Please try again");
			System.exit(0);
		}
	}
	
	
	/*
	 * Group together sibling nodes in <mo> brackets
	 */
	private void sortBrackets() {		
		// Pair together the brackets
		List<Node> checkList;
		while(getNextBracket() != null) {
			checkList = new ArrayList<Node>();
			Node next = getNextBracket();
			checkList.add(next);
			do {
				next = next.getNextSibling();
				while(!next.getTextContent().equals(")")){
					checkList.add(next);
					next = next.getNextSibling();
				}
				checkList.add(next);
			}while(!check(checkList));
			enlcose(checkList);
		}
	}
		
	/*
	 * Create an <mfenced> node and adopt children of the <mo> bracket grouping
	 */
	private void enlcose(List<Node> checkList) {
		Node fenced  = doc.renameNode(checkList.get(0), null, "mrow");
		fenced.setTextContent(null);
		for(int i = 1; i < checkList.size(); i ++) {
			fenced.appendChild(checkList.get(i));
		}
		fenced.removeChild(fenced.getLastChild());
		reduceMrows(fenced);
			
	}
	
	/*
	 * If it is a single mrow tag inside another then it can be reduced
	 */
	private void reduceMrows(Node fenced) {		
		Node parent = fenced.getParentNode();
		if(parent.getChildNodes().getLength() == 1 && parent.getNodeName().equals("mrow")) {
			Node next = fenced.getFirstChild();
			while(next != null) {
				parent.appendChild(next);
				next = fenced.getFirstChild();
			}
			parent.removeChild(fenced);
		}			
		
	}


	/*
	 * Get the next available bracket from the document or return null if EOF
	 */
	private Node getNextBracket() {
		NodeList tempNodes = doc.getElementsByTagName("mo");
		for(int i = 0; i < tempNodes.getLength(); i++) {
			if (tempNodes.item(i).getTextContent().equals("(")) {
				return tempNodes.item(i);
			}
		}		
		return null;
	}
	
	/*
	 * Check if the number of open and closed brackets is balanced (intervals will need to be converted first)
	 */
	private boolean check(List<Node> nodes) {
		int open = 0;
		int closed = 0;
		for(Node n : nodes) {
			if(n.getTextContent().equals("("))
				open ++;
			if(n.getTextContent().equals(")"))
				closed ++;
		}
		return open == closed;
		
	}

}
