package maths_areas.sets;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleSets {
	
	private Document doc;

	public SimpleSets(Document doc) {
		this.doc = doc;
		convert();
	}
	
	private void convert() {
		while(findFunction()!= null) {
			List<Node> set = findFunction();
			if(set.size() > 3 && (set.get(2).getTextContent().equals("|")
									||set.get(2).getTextContent().equals(":") )) {
				createCondition(set);
			}else
				createList(set);
		}
		removeMrow();
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

	/*
	 * Find the function by node text content 
	 */
	private List<Node> findFunction() {
		List<Node> set = new ArrayList<Node>();
		NodeList nl = doc.getElementsByTagName("mo");		
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getTextContent().equalsIgnoreCase("{")) {		
				Node toAdd = nl.item(i);
				while(toAdd != null && !toAdd.getTextContent().equals("}")) {
					set.add(toAdd);
					toAdd = toAdd.getNextSibling();
				}
				set.add(toAdd);
				return set;
			}
		}
		return null;
	}
	

	private void createCondition(List<Node> set) {
		Node apply = createSet(set);
		Node bvar = doc.createElement("bvar");
		apply.insertBefore(bvar, apply.getFirstChild());
		bvar.appendChild(set.get(1));
		apply.removeChild(set.get(2));
		Node condition = doc.createElement("condition");
		apply.insertBefore(condition, bvar.getNextSibling());
		while(condition.getNextSibling() != null) {
			condition.appendChild(condition.getNextSibling());
		}
		apply.appendChild(set.get(1).cloneNode(true));
		sortBvar(bvar);	
	}
	
	private void sortBvar(Node bvar) {
		List<Node> vars = new ArrayList<Node>();
		Node next = bvar.getFirstChild();
		if(next.getNodeName().equals("mi"))
			vars.add(next);
		else {
			next = next.getFirstChild();
			while(next!=null) {
				if(next.getNodeName().equals("mi"))
					vars.add(next);
				next = next.getNextSibling();
			}
		}
		next = bvar.getFirstChild();
		while(next!=null) {
			bvar.removeChild(next);
			next = bvar.getFirstChild();
		}
		for(Node n : vars) {
			bvar.appendChild(n);
		}
		
	}

	private void createList(List<Node> set) {
		Node apply = createSet(set);
		NodeList nl = apply.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getTextContent().trim().equals(","))
				apply.removeChild(nl.item(i));
		}
	}

	private Node createSet(List<Node> set) {
		Node apply = set.get(0);
		apply.setTextContent(null);
		doc.renameNode(apply, null, "set");
		for(int i = 1; i < set.size(); i ++) {
			if(set.get(i)!=null)apply.appendChild(set.get(i));
		}
		apply.removeChild(apply.getLastChild());
		return apply;
	}



}
