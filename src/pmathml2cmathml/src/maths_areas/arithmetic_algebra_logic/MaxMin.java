package maths_areas.arithmetic_algebra_logic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MaxMin {

	public Document doc;
	
	public MaxMin(Document doc) {
		this.doc = doc;
		convert("max");
		convert("min");
	}
	
	private void convert(String maxmin) {
		Node f = findFunction(maxmin);
		while(f!=null) {
			removeInvisible(f);
			String s = f.getNextSibling().getNodeName();
			if(s.equals("set")) {
				Node set = f.getNextSibling();
				doc.renameNode(set, null, "apply");
				set.insertBefore(f, set.getFirstChild());
				doc.renameNode(f, null, maxmin);
				f.setTextContent(null);
			}else {
				boolean mfl = mfencedList(f, maxmin);
				if(!mfl) {
					mfencedRow(f);//mfenced:mrow -> mrow
					bracketsRow(f);//<mo>( or {</mo> -> mrow
					createApply(f, maxmin);
					if(!isList(f)) 
						addCondition(f);					
				}
			}
			f = findFunction(maxmin);
		}
	}

	/*
	 * Find the function by node text content 
	 */
	private Node findFunction(String maxmin) {
		NodeList nl = doc.getElementsByTagName("mo");		
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getTextContent().equalsIgnoreCase(maxmin)) 		
				return nl.item(i);
		}
		nl = doc.getElementsByTagName("mi");		
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getTextContent().equalsIgnoreCase(maxmin)) 		
				return nl.item(i);
		}
		return null;
	}

	/*
	 * Remove apply function invisible operator
	 */
	private void removeInvisible(Node n) {
		Node next = n.getNextSibling();
		if(next.getTextContent().equalsIgnoreCase("&ApplyFunction;") ||
				next.getTextContent().equalsIgnoreCase("&#x2061;")) {
			n.appendChild(next);
			n.removeChild(next);
			next = n.getNextSibling();
		}		
	}

	
	/*
	 * Converts the function if it is a list in an mfenced tag
	 */
	private boolean mfencedList(Node f, String maxmin) {
		List<Node> objects = new ArrayList<Node>();
		objects.add(f);
		Node next = f.getNextSibling();
		if(next.getNodeName().equals("mfenced") && next.getChildNodes().getLength() > 1) {
			doc.renameNode(f, null, "apply");
			f.setTextContent(null);
			f.appendChild(next);
			doc.renameNode(next, null, maxmin);
			next = next.getFirstChild();
			while(next != null) {
				f.appendChild(next);
				next = f.getFirstChild().getFirstChild();
			}
			return true;
		}
		return false;
	}


	/*
	 * Bring the mrow node up a level to replace the mfenced node
	 */
	private void mfencedRow(Node f) {
		if(f.getNextSibling().getNodeName().equals("mfenced")){
			Node fenced = f.getNextSibling();
			Node row = fenced.getFirstChild();
			fenced.getParentNode().replaceChild(row, fenced);
		}
	}
	

	private void bracketsRow(Node f) {
		Node next = f.getNextSibling();
		if(next.getTextContent().equals("{")) {// If curly brackets just find next bracket
			doc.renameNode(next, null, "mrow");
			Node row = next;
			row.setTextContent(null);
			next = next.getNextSibling();
			while(next!=null) {
				if(next.getTextContent().equals("}")) {
					row.appendChild(next);
					row.removeChild(next);
					next = null;
				}else {
					row.appendChild(next);
					next = row.getNextSibling();
				}
			}
		}else if(next.getTextContent().equals("(")) {// Brackets will be sorted first so should't trigger this
			doc.renameNode(next, null, "mrow");
			Node row = next;
			row.setTextContent(null);
			next = next.getNextSibling();
			while(next!=null) {
				if(next.getTextContent().equals(")")) {
					row.appendChild(next);
					row.removeChild(next);
					next = null;
				}else {
					row.appendChild(next);
					next = row.getNextSibling();
				}
			}			
		}		
	}
	

	/*
	 * Merge into an apply node when in the form <mo>function</mo><mrow/>
	 */
	private void createApply(Node f, String maxmin) {
		doc.renameNode(f, null, "apply");
		Node next = f.getNextSibling();
		f.setTextContent(null);
		f.appendChild(next);
		Node max = doc.renameNode(next, null, maxmin);
		next = max.getFirstChild();
		if(next.getTextContent().equals("{")) {
			max.removeChild(max.getFirstChild());
			max.removeChild(max.getLastChild());
			next = max.getFirstChild();
		}
		while(next != null) {
			f.appendChild(next);
			next = f.getFirstChild().getFirstChild();
		}
		
	}
	
	/*
	 * Check if function is on a list and remove commas if it is
	 */
	private boolean isList(Node f) {
		NodeList nl = f.getChildNodes();
		if(nl.getLength() > 3) {//function, bvar, seperator, condition
			if(nl.item(2).getTextContent().equals("|") ||
					nl.item(2).getTextContent().equals(":")) {
				return false;
			}
		}
		removeCommas(f);
		return true;
	}
	
	/*
	 * Remove commas from a list 
	 */
	private void removeCommas(Node f) {
		NodeList nl = f.getChildNodes();
		for(int i = 1; i < nl.getLength(); i ++) {
			if(nl.item(i).getTextContent().equals(","))
				f.removeChild(nl.item(i));			
		}
		
	}

	/*
	 *  Convert the apply tag to bvar and condition
	 */
	private void addCondition(Node f) {
		Node bvar = f.insertBefore(doc.createElement("bvar"), f.getFirstChild().getNextSibling());
		bvar.appendChild(f.getFirstChild().getNextSibling().getNextSibling());
		f.removeChild(bvar.getNextSibling());// Remove separator
		Node condition = f.insertBefore(doc.createElement("condition"), bvar.getNextSibling());
		Node next = condition.getNextSibling();
		while(next != null) {
			condition.appendChild(next);
			next = condition.getNextSibling();
		}
	}

	
	
	
	
}
