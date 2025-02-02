package maths_areas.arithmetic_algebra_logic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StandardFunctions {

	public Document doc;
	
	public StandardFunctions(Document doc) {
		this.doc = doc;
		//TODO add all variations of these 
		convert("arg", "arg");
		convert("real", "real");
		convert("imaginary", "im");
	}
	
	private void convert(String contentTag, String presText) {
		Node f = findFunction(presText);
		while(f!=null) {
			removeInvisible(f);
			boolean mfl = mfencedList(f, contentTag);
			if(!mfl) {
				mfencedRow(f);//mfenced:mrow -> mrow
				bracketsRow(f);//<mo>( or {</mo> -> mrow
				createApply(f, contentTag);					
			}
			f = findFunction(contentTag);
		}
	}

	/*
	 * Find the function by node text content 
	 */
	private Node findFunction(String presText) {
		NodeList nl = doc.getElementsByTagName("mo");		
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getTextContent().equalsIgnoreCase(presText)) 		
				return nl.item(i);
		}nl = doc.getElementsByTagName("mi");		
		for (int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getTextContent().equalsIgnoreCase(presText)) 		
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
	private boolean mfencedList(Node f, String contentTag) {
		List<Node> objects = new ArrayList<Node>();
		objects.add(f);
		Node next = f.getNextSibling();
		if(next.getNodeName().equals("mfenced") && next.getChildNodes().getLength() > 1) {
			doc.renameNode(f, null, "apply");
			f.setTextContent(null);
			f.appendChild(next);
			doc.renameNode(next, null, contentTag);
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
	private void createApply(Node f, String contentTag) {
		doc.renameNode(f, null, "apply");
		Node next = f.getNextSibling();
		f.setTextContent(null);
		f.appendChild(next);
		Node max = doc.renameNode(next, null, contentTag);
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
	
}