package maths_areas;

import org.w3c.dom.Document;

import maths_areas.trigonometry.ArcTrig;
import maths_areas.trigonometry.SimpleTrig;

public class Trigonometry {

	private Document doc;

	public Trigonometry(Document doc) {
		this.doc = doc;
	}

	/*
	 * Convert all presentation that maps onto this area of maths
	 */
	@SuppressWarnings("unused")
	public void convert() {
		SimpleTrig st = new SimpleTrig(doc);
		ArcTrig at = new ArcTrig(doc);
	}

}
