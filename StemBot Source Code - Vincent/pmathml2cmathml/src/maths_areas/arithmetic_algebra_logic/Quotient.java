package maths_areas.arithmetic_algebra_logic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Quotient {
	
	public Document doc;

	/*
	 * Constructor
	 */
	public Quotient(Document doc) {
		this.doc = doc;
		convert();
	}
	
	/*
	 * Convert list of elements to an apply tag
	 */
	private void convert() {
		List<Node> e = findNextElements();
		while(e != null) {
			if(e.size() == 5) {       //<mo>leftfloor</mo><mi>a</<mi><mo>/</mo><mi>b</<mi><mo>rightfloor</mo>
				Node open = e.get(0);
				Node a = e.get(1);
				Node div = e.get(2);
				assert(div.getNodeName().equals("mo")) : "Division operator inside floor brackets is not an operator";
				Node b = e.get(3);
				Node close = e.get(4);
				doc.renameNode(open, null, "apply");
				open.setTextContent(null);
				open.appendChild(doc.createElement("quotient"));
				open.appendChild(a);
				open.appendChild(b);
				open.appendChild(close);
				open.removeChild(close);
				open.appendChild(div);
				open.removeChild(div);
			}else if(e.size() == 3) { //<mo>leftfloor</mo><mfrac><mi>a</<mi><mi>b</<mi></mfrac><mo>rightfloor</mo>
				Node open = e.get(0);
				Node frac = e.get(1);
				Node close = e.get(2);
				doc.renameNode(open, null, "apply");
				open.setTextContent(null);
				if(frac.getNodeName().equals("mfrac")) {
					open.appendChild(doc.createElement("quotient"));
					open.appendChild(frac.getFirstChild());
					open.appendChild(frac.getLastChild());

				}else {
					open.appendChild(doc.createElement("floor"));
					open.appendChild(frac);
				}
				open.appendChild(close);
				open.removeChild(close);

			}else if(e.size() == 2) { //<mi>a</<mi><mo>div</mo><mi>b</<mi>
				Node a = e.get(0);
				Node b = e.get(1);
				Node apply = doc.insertBefore(a, doc.createElement("apply"));
				apply.appendChild(doc.createElement("quotient"));
				apply.appendChild(a);
				apply.appendChild(b);
			}else
				break; //To catch syntax errors in presentation if there are any
			
			e = findNextElements();
		}
	}
	
	/*
	 * Return a list of elements enclosed in floor brackets or surrounding a <mo>div</mo>
	 */
	private List<Node> findNextElements(){
		NodeList nl = doc.getElementsByTagName("mo");
		List<Node> elements = new ArrayList<Node>();
		
		for (int i = 0; i < nl.getLength(); i++) {
			Node next = nl.item(i);
			boolean completeFloor = false;
			
			if(next.getTextContent().equalsIgnoreCase("&#x230a;")) {
				elements.add(next);
				next = next.getNextSibling();
				while(next != null) {
					if(next.getTextContent().equalsIgnoreCase("&#x230b;")) {
						elements.add(next);
						next = null;
						completeFloor = true;
					}
					else {
						elements.add(next);
						next = next.getNextSibling();
					}
				}
				if(completeFloor) return elements;
			}else if(next.getTextContent().equalsIgnoreCase("div")) {
				elements.add(next.getPreviousSibling());
				elements.add(next.getNextSibling());
				return elements;
			}
		}
		
		return null;
	}

}
