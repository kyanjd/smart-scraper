package maths_areas;

import org.w3c.dom.Document;

import maths_areas.relations.Equations;
import maths_areas.relations.FactorOf;

public class Relations {
	
	public Document doc;
	
	public Relations(Document doc) {
		this.doc = doc;
	}
	
	/*
	 * Convert all presentation that maps onto this area of maths
	 */
	@SuppressWarnings("unused")
	public void convert() {
		Equations e = new Equations(doc);
		FactorOf f = new FactorOf(doc);//Sets need to be converted before factorof
	}
}
