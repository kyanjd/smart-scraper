package main;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import maths_areas.ArithmeticAlgebraLogic;
import maths_areas.Constants;
import maths_areas.Relations;
import maths_areas.Sets;
import maths_areas.Trigonometry;

public class Presentation2Content {
	
	public Document doc;
	
	public Presentation2Content(Document doc) {
		this.doc = doc;
		convertIndexed();
	}
	
	/*
	 * Convert all mathematical areas in the package in the following order
	 */
	private void convertIndexed() {

		removeWhiteSpace();
		
		Sets s = new Sets(doc);
		s.convert();
		ArithmeticAlgebraLogic a = new ArithmeticAlgebraLogic(doc);
		a.convert();
		Trigonometry trig = new Trigonometry(doc);
		trig.convert();
		Relations r = new Relations(doc);
		r.convert();
		Constants c = new Constants(doc);
		c.convert();
		//TODO add other areas
		
		reduceMrows();
	}
	
	/*
	 * Reduces all remaining cases of mrow/mfenced tags which should
	 * only have one element in and therefore can be removed from Content
	 */
	private void reduceMrows() {
		// TODO reorganise/remove all of the mrow and mfenced tags 
		
	}

	/*
	 * Remove all whitespace in the document
	 */
	private void removeWhiteSpace() {
		try {
			XPathFactory xpathFactory = XPathFactory.newInstance();
			// XPath to find empty text nodes.
			XPathExpression xpathExp;
			xpathExp = xpathFactory.newXPath().compile(
			        "//text()[normalize-space(.) = '']");
			NodeList emptyTextNodes = (NodeList) 
			        xpathExp.evaluate(doc, XPathConstants.NODESET);
			// Remove each empty text node from document.
			for (int i = 0; i < emptyTextNodes.getLength(); i++) {
			    Node emptyTextNode = emptyTextNodes.item(i);
			    emptyTextNode.getParentNode().removeChild(emptyTextNode);
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}  		
	}
	
	
	/*
	 * Helper method to print the xml file to console
	 */
	public String print() {
		try {			
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			Writer out = new StringWriter();
			tf.transform(new DOMSource(doc), new StreamResult(out));
			String result = out.toString().replaceAll("&amp;", "&");
			result = result.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>", "");
			return result;
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

}
