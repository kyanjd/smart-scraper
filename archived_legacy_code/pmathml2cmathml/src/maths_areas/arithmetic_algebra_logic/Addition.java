package maths_areas.arithmetic_algebra_logic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Addition {

	private Document doc;
	public List<String> symbols;
	
	public Addition(Document doc) {
		this.doc = doc;
		symbols = new ArrayList<String>();
		symbols.add("+");
		convert();
	}
	
	/*
	 * Convert all occurrences of the symbols in the symbols list to an apply tag
	 */
	private void convert() {
		NodeList nl = doc.getElementsByTagName("mo");
		List<Node> plusSigns = new ArrayList<Node>();
		
		for (int i = 0; i < nl.getLength(); i++) {
			String s = nl.item(i).getTextContent();
			if(symbols.contains(s))
				plusSigns.add(nl.item(i));
		}
		
		for(int i = 0; i < plusSigns.size(); i ++) {
			Node plus = plusSigns.get(i);
			plus.setTextContent(null);
			doc.renameNode(plus, null, "apply");
			plus.appendChild(doc.createElement("plus"));
			plus.appendChild(plus.getPreviousSibling());
			try {
				plus.appendChild(plus.getNextSibling());
				while(plus.getNextSibling().equals(plusSigns.get(i + 1))) {
					Node nullPlus = plus.getNextSibling();
					plus.appendChild(nullPlus);
					plus.removeChild(nullPlus);
					plus.appendChild(plus.getNextSibling());
					i++;
					if(plus.getNextSibling() == null)
						break;
				}
			}catch (NullPointerException| IndexOutOfBoundsException e) {
				// Expected to catch the last element to be multiplied and the last multiplication
			}
		}
	}
}
