package maths_areas.constants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SpecialNumbers {
	
	private Document doc;

	public SpecialNumbers(Document doc) {
		this.doc = doc;
		convert();
	}

	private void convert() {
		convertRational();
		convertBase();
		complexCartesian();
		complexPolar();
		
	}
	
	private void complexPolar() {
		NodeList nl = doc.getElementsByTagName("exponentiale");
		for(int i = 0; i < nl.getLength(); i ++) {
			Node expe = nl.item(i);
			Node power = expe.getParentNode();
			if(power.getChildNodes().getLength() == 3 &&
					power.getFirstChild().getNodeName().equals("power") &&
					power.getLastChild().getNodeName().equals("apply")) {
				Node times = power.getLastChild();
				Node imPart = null;
				if(times.getChildNodes().getLength() == 3 && 
						times.getFirstChild().getNodeName().equals("times")) {
					if(times.getLastChild().getNodeName().equals("imaginaryi") &&
							times.getLastChild().getPreviousSibling().getNodeName().equals("mn")) {
						imPart = times.getLastChild().getPreviousSibling();
					}else if(times.getLastChild().getNodeName().equals("mn") &&
							times.getLastChild().getPreviousSibling().getNodeName().equals("imaginaryi")) {
						imPart = times.getLastChild();
					}					
				
					Node apply = power.getParentNode();
					if(imPart != null && apply.getChildNodes().getLength() == 3 &&
							apply.getFirstChild().getNodeName().equals("times") &&
							apply.getFirstChild().getNextSibling().getNodeName().equals("mn")) {
						
						Node rePart = apply.getFirstChild().getNextSibling();
						Element polar = doc.createElement("cn");
						polar.appendChild(doc.createTextNode(rePart.getTextContent()));
						polar.appendChild(doc.createElement("sep"));
						polar.appendChild(doc.createTextNode(imPart.getTextContent()));
						Node parent = apply.getParentNode();
						parent.replaceChild(polar, apply);
						polar.setAttribute("type", "complex-polar");
						i--;
					}
				}
			}
		}		
	}

	private void complexCartesian() {
		NodeList nl = doc.getElementsByTagName("imaginaryi");
		for(int i = 0; i < nl.getLength(); i ++) {
			Node imaginaryi = nl.item(i);
			Node times = imaginaryi.getParentNode();
			if(times.getChildNodes().getLength() == 3 && 
					times.getFirstChild().getNodeName().equals("times") &&
					imaginaryi.getPreviousSibling().getNodeName().equals("mn")) {
				
				Node plus = times.getParentNode();
				if(plus.getChildNodes().getLength() == 3 && 
						plus.getFirstChild().getNodeName().equals("plus") &&
						plus.getFirstChild().getNextSibling().getNodeName().equals("mn")) {
					
					Node imPart = doc.createTextNode(imaginaryi.getPreviousSibling().getTextContent());
					Node sep = plus.getFirstChild();
					doc.renameNode(sep, null, "sep");
					plus.insertBefore(sep, times);
					plus.replaceChild(imPart, times);
					plus.replaceChild(doc.createTextNode(plus.getFirstChild().getTextContent()), plus.getFirstChild());
					Element complex = (Element)plus;
					complex.setAttribute("type", "complex-cartesian");
					doc.renameNode(complex, null, "cn");
					i--;
				}				
			}
		}		
	}

	private void convertRational() {
		NodeList nl = doc.getElementsByTagName("apply");
		for(int i = 0; i < nl.getLength(); i ++) {
			Node apply = nl.item(i);
			if(apply.getFirstChild().getNodeName().equals("divide")) {
				Node divide = apply.getFirstChild();
				Node num = divide.getNextSibling();
				Node den = num.getNextSibling();
				if(num.getNodeName().equals("mn") && den.getNodeName().equals("mn")) {
					apply.insertBefore(divide, den);
					doc.renameNode(divide, null, "sep");
					doc.renameNode(apply, null, "cn");
					apply.replaceChild(doc.createTextNode(num.getTextContent()),num);
					apply.replaceChild(doc.createTextNode(den.getTextContent()),den);
					Element rational = (Element)apply;
					rational.setAttribute("type", "rational");
					i--;
				}
			}
		}
		
	}

	private void convertBase() {
		NodeList nl = doc.getElementsByTagName("msub");
		for(int i = 0; i < nl.getLength(); i ++) {
			Node msub = nl.item(i);
			if(msub.getFirstChild().getNodeName().equals("mn") && msub.getLastChild().getNodeName().equals("mn")) {
				Node first = msub.getFirstChild();
				Node last = first.getNextSibling();
				msub.replaceChild(doc.createTextNode(first.getTextContent()), first);
				Element base = (Element)msub;
				base.setAttribute("base", last.getTextContent());
				msub.removeChild(last);
				doc.renameNode(msub, null, "cn");
			}
		}
		
	}

}
